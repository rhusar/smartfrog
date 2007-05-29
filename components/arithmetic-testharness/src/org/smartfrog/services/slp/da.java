package org.smartfrog.services.slp;
/**
 * SLPv2 DA (directory agent) with mesh-enhanced capability
 * It can be invoked as follows:
 * (1) java da
 *     # use default scope, database and summary file
 *     # DAAdvert multicast is on (default)
 *     # DAAdvert has "mesh-enhanced" keyword (default)
 * (2) java -Dscope=x da
 *     # set DA scopes, scopes should be seperated by comma, no space
 * (3) java -Ddbase=y da
 *     # set database file name
 * (4) java -Dsummary=z da
 *     # set summary file name
 * (5) java -Dmcast=no da
 *     # turn off DAAdvert multicast
 *     # simulate the condition where multicast is NOT supported
 * (6) java -Dmesh=no da
 *     # turn off "mesh-enhanced" keyword in DAAdvert
 *     # function as a non-mesh-emhanced DA
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 * Mods : da made public.
 */

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class da extends JFrame implements ActionListener {

    static int width = 600;
    static int height = 350;

    public static void main(String args[]) {
	try {
	    boolean mcast = true;
	    boolean mesh_enhanced = true;

	    // we need a trick to get the absolute domain name of the host
	    String hname = (InetAddress.getLocalHost()).getHostAddress();
	    hname = (InetAddress.getByName(hname)).getHostName();

	    // set scope for this DA
	    String scope = System.getProperty("scope");
	    if (scope == null) scope = Const.defaultScope;

	    // database file
	    String dbase = System.getProperty("dbase");
	    if (dbase == null) dbase = Const.defaultDbase;

	    // summary file
	    String summary = System.getProperty("summary");
	    if (summary == null) summary = Const.defaultSummary;

	    // multicast DAAdvert or not?
	    String s = System.getProperty("mcast");
	    if (s != null && s.equalsIgnoreCase("no")) mcast = false;

	    // carry "mesh-enhnaced" attribute keyword in DAAdvert or not?
	    s = System.getProperty("mesh");
	    if (s != null && s.equalsIgnoreCase("no")) mesh_enhanced = false;

       	    da f = new da(hname, scope, dbase, summary, mcast, mesh_enhanced,true);
	} catch (Exception e) {
          if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
//          System.err.println(e);

            System.exit(1);
        }
    }

    JMenuBar   mbar;
    JMenu      fileMenu, funcMenu, setupMenu, helpMenu;
    JMenuItem  exitItem, aboutItem, dataItem, summaryItem, saveItem, loadItem,
	       confItem, peerItem, advertItem, daItem;
    JLabel     urlLabel;
    JTextField urlField;
    JTextArea  textarea;
    TreeMap  peerTable;	   // key: absolute domain name, value: Peer class
    TreeMap  summaryTable; // summary of database & DAAdvert
    Database database;
    String   myurl, myscope, myattr="", myspi="", mydbase, mysummary;
    int      myts;
    boolean  mcast, mesh_enhanced;
    slpMsgComposer  composer;
    slpUdpHandler   udp;
    java.util.Timer mainTimer;
    TimerTask       mainTimerTask;
    int             daAdvertTimer, dbCheckTimer, keepaliveTimer;
    public da(String hname, String scope, String dbase, String summary,boolean mcast, boolean mesh_enhanced, boolean gui) {
        super("Welcome to mSLP DA <"+hname+">");
	createGUI();

	// DA parameters
	this.mcast = mcast;
	this.mesh_enhanced = mesh_enhanced;
	if (mesh_enhanced) myattr = "mesh-enhanced";
        try {
	  myurl   = InetAddress.getByName(hname.toLowerCase()).getHostAddress();
        } catch (Exception e) {}


	myscope = (scope == null) ? Const.defaultScope : scope;
        myts = (int) (System.currentTimeMillis()/1000);
	mydbase   = (dbase == null) ? Const.defaultDbase : dbase;
	mysummary = (summary == null) ? Const.defaultSummary : summary ;

	// data structure
	database = new Database(this);
	composer = new slpMsgComposer();
	peerTable   = new TreeMap();
	summaryTable = new TreeMap();
	summaryTable.put(myurl, new Summary(myurl, myscope, myts, myattr, 0));
	loadData();  // load database, summary and advert

	// TCP and UDP server
	(new daTcpServer(this)).start();
	daUdpServer t = new daUdpServer(this);
	udp = t.getUdp();
        t.start();

	// timer initilization
	daAdvertTimer  = Const.mainTimer_interval; // advertise first time
	dbCheckTimer   = Const.dbCheck_interval   + Const.mainTimer_interval;
	keepaliveTimer = Const.keepalive_interval + Const.mainTimer_interval;
	mainTimer = new java.util.Timer();
	mainTimerTask  = new TimerTask() {
	    public void run() { scheduleTask(); }
	};
	mainTimer.scheduleAtFixedRate(mainTimerTask,
		Const.mainTimer_delay, Const.mainTimer_interval);
	if (gui) {
       	    setSize(width, height);
	    setForeground(Color.black);
	    setBackground(Color.lightGray);
	    show();
	}
    }

    private synchronized void scheduleTask() {
        // Timer for multicast DAAdvert (say once a minute)
	if (mcast) { // only if multicast is supported
	    daAdvertTimer -= Const.mainTimer_interval;
	    if (daAdvertTimer <= 0) {
		multicastDAAdvert();
		daAdvertTimer = Const.daAdvert_interval;
	    }
	}

        // Timer for purging expired data in database (say once a day)
	dbCheckTimer -= Const.mainTimer_interval;
	if (dbCheckTimer <= 0) {
	    database.rmExpiredEntry();
	    dbCheckTimer = Const.dbCheck_interval;
	}

        // Remove expired peers in peerTable (say once several minutes)
	rmExpiredPeer();

        // Keepalive timer for avtive peers (say once a minute)
	keepaliveTimer -= Const.mainTimer_interval;
        if (keepaliveTimer <= 0) {
	    sendKeepAlive();
	    keepaliveTimer = Const.keepalive_interval;
	}
    }

    private synchronized void multicastDAAdvert() {
	byte[] buf = composer.DAAdvert(0, Const.mcast_flag, "en",
			myts, myurl, myscope, myattr, myspi);
	String ret = udp.send(buf, Const.mcast_addr, Const.port);
	if (ret != null) append(ret);
    }

    private synchronized void rmExpiredPeer() {
	Iterator keys = peerTable.keySet().iterator();
	while (keys.hasNext()) {
	    String k = (String) keys.next();
	    Peer   p = (Peer) peerTable.get(k);
	    if (!myurl.equalsIgnoreCase(p.getURL()) && p.getAliveTimer() < 0) {
		append("Remove " + p.getURL() + ",  keepalive timeout");
		p.closeTcp();
		keys.remove();
	    }
	}
    }

    private synchronized void sendKeepAlive() {
	byte[] buf = composer.DAAdvert(0, Const.normal_flag, "en",
			myts, myurl, myscope, myattr, myspi);
	Iterator values = peerTable.values().iterator();
	while (values.hasNext()) {
	    Peer p = (Peer) values.next();
	    if (!myurl.equalsIgnoreCase(p.getURL())) {  // this DA itself
		(p.getTcp()).send(buf, buf.length);
	    }
	}
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitItem) {
            System.exit(0);
	} else if (e.getSource() == dataItem) {
	    drawLine();
	    String s1 = "Deleted LangTag SrvType URL LifeTime Scope ";
	    String s2 = "versionTS arrivalTS acceptDA acceptTS Attribute";
	    append(s1+s2);
	    database.saveDatabase(null);
	} else if (e.getSource() == summaryItem) {
	    displaySummary();
	} else if (e.getSource() == advertItem) {
	    displayAdvert();
	} else if (e.getSource() == saveItem) {
	    saveData();
	    append("data saved");
	} else if (e.getSource() == loadItem) {
	    if (loadData()) append("data loaded");
	} else if (e.getSource() == confItem) {
	    displayMyState();
	} else if (e.getSource() == peerItem) {
	    displayPeerState();
	} else if (e.getSource() == daItem) {
	    unicastDAAdvert(urlField.getText());
	}
    }

    public boolean getMeshFlag() {
	return mesh_enhanced;
    }

    public String getScope() {
	return myscope;
    }

    public String getURL() {
	return myurl;
    }

    public String getAttr() {
	return myattr;
    }

    public String getSPI() {
	return myspi;
    }

    public int getTS() {
	return myts;
    }

    public synchronized TreeMap getPeerTable() {
	return peerTable;
    }

    public synchronized Database getDatabase() {
	return database;
    }

    public synchronized void append(String s) {
        textarea.append(s + "\n");
	int line = textarea.getLineCount();
	try {
	    int pos = textarea.getLineStartOffset(line);
	    textarea.setCaretPosition(pos);
	} catch (Exception ex) {
            // System.out.println("Exception " + ex);
        }
    }

    //-----------------------------------------------------------------------
    // unicast a DAAdvert to the destDA, simulate multicast is not supported
    //-----------------------------------------------------------------------
    private void unicastDAAdvert(String destDA) {
	if (destDA.equals("")) {
	    append("please give To-URL for dest DA");
	    return;
	}
	byte[] buf = composer.DAAdvert(0, Const.normal_flag, "en",
			myts, myurl, myscope, myattr, myspi);
	String ret = udp.send(buf, destDA, Const.port);
	if (ret == null) {
	    append("unicast DAAdvert --> " + destDA);
	} else {
	    append(ret);
	}
    }

    //------------------------------------------------------------
    // (1) create a peering connection to "peer" if needed
    // (2) add the "peer" to "peerTable", and create summary entry
    // (3) send DAAdvert(s) & DataRqst to "peer" if it is meshDA
    //-------------------------------------------------------------
    public synchronized boolean newPeerHandler(String peer, String scope,
		String attr, int bts, slpTcpHandler tcp) {
	if (!Util.shareString(myscope, scope, ",")) return false; // not a peer
	peer = peer.toLowerCase();
	Summary s = (Summary) summaryTable.get(peer);
	if (s == null) {
   //       System.out.println( "Putting new peer ");
	    summaryTable.put(peer, new Summary(peer, scope, bts, attr, 0));
	} else {
//	  System.out.println( "Peer alrady know ");
          if (bts > s.getBootTS())
            summaryTable.put(peer, new Summary(peer,
					scope, bts, attr, s.getAcceptTS()));
	}





    	Peer p = (Peer) peerTable.get(peer);
        if (p != null) return false; 	// not a new peer
	// now it is a new peer: p == null and share scopes
        if (tcp == null) { 	// need to create a peering connection
	    tcp = createPeerConn(peer);
	    if (tcp == null) {
		append("Cannot create peering connection to " + peer);
		mcastDataRqst(peer, scope);
		return false;
	    }
	}
	p = new Peer(peer, scope, tcp);
	if (attr.lastIndexOf("mesh-enhanced") < 0) { // non-mesh peer
	    p.setDirectFwd(true);
	}
	peerTable.put(peer, p);
	append("Add " + peer + " to peerTable");
	if (attr.lastIndexOf("mesh-enhanced") >= 0) { // mesh peer
	    forwardDAAdvert(p);
    	    ucastDataRqst(peer, peer);
	}
	return true;
    }

    //-------------------------------------------------------------
    // (1) create a peering connection to "peer"
    // (2) fork a new thread to read messages from this connection
    // (3) send a DAAdvert to "peer" via the peering connection
    //-------------------------------------------------------------
    private synchronized slpTcpHandler createPeerConn(String peer) {
        slpTcpHandler tcp = null;
	peer = peer.toLowerCase();
	try {
	    Socket socket = new Socket(peer, Const.port);
	    daTcpHandler h = new daTcpHandler(socket, this);
	    tcp = h.getTcp();
	    h.start();
            append("Create peering connection to " + peer);
	    byte[] buf = composer.DAAdvert(0, Const.normal_flag, "en",
			 myts, myurl, myscope, myattr, myspi);
	    tcp.send(buf, buf.length);
	} catch (ConnectException e) { // keep silent
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
	return tcp;
    }

    //---------------------------------
    // forward DAAdvert(s) to peer "p"
    //---------------------------------
    private synchronized void forwardDAAdvert(Peer p) {
	Iterator values = summaryTable.values().iterator();
	while (values.hasNext()) {
	    Summary s = (Summary) values.next();
	    if (!s.getURL().equalsIgnoreCase(myurl) &&
		(s.getAcceptTS() != 0 ||
		 !s.getURL().equalsIgnoreCase(p.getURL()) &&
		 peerTable.containsKey(s.getURL())) &&
		Util.shareString(s.getScope(), p.getScope(), ",")) {
	        byte[] buf = composer.DAAdvert(0, Const.normal_flag, "en",
		    s.getBootTS(), s.getURL(), s.getScope(), s.getAttr(), "");
	        (p.getTcp()).send(buf, buf.length);
	    }
	}
    }

    //--------------------------------------------------------------------
    // unicast DataRqst to "peer" to request new states after "ada":"ats"
    //--------------------------------------------------------------------
    public synchronized void ucastDataRqst(String ada, String peer) {
	peer = peer.toLowerCase();
	Summary s = (Summary) summaryTable.get(ada);
	long ats = 0;
	if (s != null) ats = s.getAcceptTS();
	append("Send DataRqst to " + peer + ", ID = " + ada + ":" + ats);
	byte[] buf = composer.DataRqst("en", ada, ats);
	(((Peer) peerTable.get(peer)).getTcp()).send(buf, buf.length);
    }

    //------------------------------------------------------------
    // multicast DataRqst to request new states after "ada":"ats"
    //------------------------------------------------------------
    private synchronized void mcastDataRqst(String ada, String scope) {
	Summary s = (Summary) summaryTable.get(ada);
	long ats = 0;
	if (s != null) ats = s.getAcceptTS();
	byte[] buf = composer.DataRqst("en", ada, ats);
        Iterator v = peerTable.values().iterator();
        while (v.hasNext()) {
            Peer p = (Peer) v.next();
	    if (Util.shareString(p.getScope(), scope, ",")) {
	        (p.getTcp()).send(buf, buf.length);
	        append("Send DataRqst to " + p.getURL() +
		       ", ID = " + ada + ":" + ats);
	    }
	}
    }

    //-------------------------------------------------------
    // (1) anti-entropy: batch transfer updates (in orderng)
    // (2) enable direct forwarding
    //-------------------------------------------------------
    public synchronized void DataRqst_Handler(String peer, String ada,
						 long ats) {
	peer = peer.toLowerCase();
	append("Receive DataRqst from " + peer + ", ID = " + ada + ":" + ats);
	Peer p = (Peer) peerTable.get(peer);
	if (p == null) return;	// not a peer at all
	Summary s = (Summary) summaryTable.get(ada);
	if (s != null && s.getAcceptTS() > ats) { // anti-entropy
	    database.antiEntropy(p.getTcp(), p.getScope(), ada, ats);
	}
	if (ada.equalsIgnoreCase(myurl)) { // enable direct forwarding
	    p.setDirectFwd(true);
	}
    }

    public synchronized void KeepAlive_Handler(String peer) {
	peer = peer.toLowerCase();
	Peer p = (Peer) peerTable.get(peer);
	if (p == null) return;  // not a peer at all
	p.resetAliveTimer();
    }

    public synchronized void setSummary(String ada, long ats, String peer) {
	Summary s = (Summary) summaryTable.get(ada);
	if (s == null) return;	// unknown ADA
	if (ada.equalsIgnoreCase(peer)) { // from accept DA
	    s.setAcceptTS(ats);
	} else {
      //    System.out.println("Peer is " + peer);
	    Summary p = (Summary) summaryTable.get(peer);
   //         if (p==null) System.out.println(" Big trouble ");
	    String cs = Util.commonString(s.getScope(), p.getScope(), ",");
	    cs = Util.commonString(cs, myscope, ",");
	    s.setAcceptTS(ats, cs);
	}
    }

    public synchronized void checkPeerConn(String peer, Socket socket) {
	peer = peer.toLowerCase();
	Peer p = (Peer) peerTable.get(peer);
	if (p != null) {
	    Socket peerSocket = (p.getTcp()).getSocket();
	    if (peerSocket.equals(socket)) {
		append("Remove " + peer + ",  peering connection closed");
	        p.closeTcp();
	        peerTable.remove(peer);
	    }
	}
    }

    //-----------------------------------------------------
    // Forward message in "buf", its length is at buf[2-4],
    // forward to the specified "scope", exclude "source"
    //-----------------------------------------------------
    public synchronized void forward2Peer(byte[] buf, String scope,
					  String source) {
        int len = Util.parseInt(buf, 2, 3);
     //   System.out.println(" Forward 2 peer ");
        Iterator v = peerTable.values().iterator();
        while (v.hasNext()) {
            Peer p = (Peer) v.next();
            if (!(p.getURL()).equalsIgnoreCase(source) &&     // not to source
                Util.shareString(p.getScope(), scope, ",") && // share scopes
                (Util.parseInt(buf, 1, 1) == Const.DAAdvert ||
                 p.isDirectFwd())) { // can fwd mesgs
                (p.getTcp()).send(buf, len);
                append("forward from " + source + " ---> " + p.getURL());
            }
        }
  //      System.out.println(" End of peer table");

    }

//----------------------------------------------------------------------------
// DISPLAY useful information
//----------------------------------------------------------------------------

    private synchronized void displayPeerState() {
	drawLine();
	append("PeerURL \t\tScopes \tEnableDirectFwd");
	Iterator values = peerTable.values().iterator();
	while (values.hasNext()) {
	    Peer p = (Peer) values.next();
	    append(p.getURL() + "\t" + p.getScope() + "\t" + p.isDirectFwd());
	}
    }

    private void displayMyState() {
	drawLine();
	append("Domain Name:\t" + myurl);
	append("Port Number:\t\t" + Const.port);
	append("Serving Scopes:\t" + myscope);
	append("Boot Timestamp:\t" + myts);
        append("Attributes:\t\t" + myattr);
	append("Database File:\t\t" + mydbase);
	append("Summary File:\t\t" + mysummary);
	append("Multicast DAAdvert:\t" + mcast);
    }

    private synchronized void displaySummary() {
	drawLine();
	append("DA-URL \t\tSummaryTS");
        Iterator values = summaryTable.values().iterator();
	while (values.hasNext()) {
	    Summary s = (Summary) values.next();
	    if (s.getAcceptTS() != 0) {
	        append(s.getURL() + "\t" + s.getAcceptTS());
	    }
	}
    }

    private synchronized void displayAdvert() {
        drawLine();
        append("PeerURL \t\tScopes \tBootTS \tAttributes");
        Iterator values = summaryTable.values().iterator();
	while (values.hasNext()) {
	    Summary s = (Summary) values.next();
	    if (s.getURL().equalsIgnoreCase(myurl) || s.getAcceptTS() != 0 ||
		peerTable.containsKey(s.getURL())) {
	        append(s.getURL() + "\t" + s.getScope() + "\t" +
		       s.getBootTS() + "\t" + s.getAttr());
	    }
	}
    }

    private void drawLine() {
	String s = "-----------------";
	append(s+s+s+s);
    }

//---------------------------------------------------------------------------
// SAVE & LOAD data (database, summary, DAAdvert)
//---------------------------------------------------------------------------

    private synchronized void saveData() {
	try {
	    BufferedWriter o = new BufferedWriter(new FileWriter(mydbase));
	    database.saveDatabase(o);
	    o.close();
            o = new BufferedWriter(new FileWriter(mysummary));
	    saveSummary(o);
	    o.close();
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

    private synchronized void saveSummary(BufferedWriter o) {
	try {
	    StringBuffer buf = new StringBuffer();
	    Iterator values = summaryTable.values().iterator();
	    while (values.hasNext()) {
	        Summary s = (Summary) values.next();
	        if (buf.length() > 0) buf.append("\n");
	        buf.append(s.getURL() + " " + s.getScope() + " " +
			   s.getBootTS() + " " + s.getAttr() + " " +
			   s.getAcceptTS());
	    }
	    if (buf.length() > 0) buf.append("\n");
	    o.write(buf.toString(), 0, buf.length());
	} catch (Exception e) {
            if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
        }
    }

    private synchronized boolean loadData() {
	try {
            File f = new File(mydbase);
            if (!f.exists()) return false;
	    f = new File(mysummary);
	    if (!f.exists()) return false;
	    database.loadDatabase(mydbase);
	    loadSummary();
	} catch (Exception e) {
//            if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
        }
        return true;
    }

    private synchronized void loadSummary() {
	try {
	    String line;
            BufferedReader in =
                new BufferedReader(new FileReader(mysummary));
            while ((line = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
		String url   = st.nextToken();
		String scope = st.nextToken();
		int    bts    = Integer.parseInt(st.nextToken());
		String attr  = st.nextToken();
		int    ats    = Integer.parseInt(st.nextToken());
		summaryTable.put(url, new Summary(url, scope, bts, attr, ats));
	    }
            in.close();
        } catch (Exception e) {
            if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
        }
    }

//-------------------------------------------------------------------------
// CREATE GUI
//-------------------------------------------------------------------------

    private void createGUI() {
        fileMenu = new JMenu("File");
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        funcMenu = new JMenu("Operation");
        dataItem = new JMenuItem("Display Database");
        dataItem.addActionListener(this);
        funcMenu.add(dataItem);

        summaryItem = new JMenuItem("Display Summary");
        summaryItem.addActionListener(this);
        funcMenu.add(summaryItem);

        advertItem = new JMenuItem("Display DAAdvert");
        advertItem.addActionListener(this);
        funcMenu.add(advertItem);

	funcMenu.addSeparator();
        loadItem = new JMenuItem("Load Data");
        loadItem.addActionListener(this);
        funcMenu.add(loadItem);

        saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(this);
        funcMenu.add(saveItem);

        setupMenu = new JMenu("Setup");
        peerItem = new JMenuItem("Peering State");
        peerItem.addActionListener(this);
        setupMenu.add(peerItem);

        confItem = new JMenuItem("My Configuration");
        confItem.addActionListener(this);
        setupMenu.add(confItem);

	setupMenu.addSeparator();
        daItem = new JMenuItem("Unicast DAAdvert");
        daItem.addActionListener(this);
        setupMenu.add(daItem);

        helpMenu = new JMenu("Help");
        aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        mbar = new JMenuBar();
	Border raisedbevel, loweredbevel, compound;
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        loweredbevel = BorderFactory.createLoweredBevelBorder();
        compound = BorderFactory.createCompoundBorder(
                                raisedbevel, loweredbevel);
	mbar.setBorder(compound);
        mbar.add(fileMenu);
        mbar.add(funcMenu);
        mbar.add(setupMenu);
        mbar.add(helpMenu);
//      mbar.setHelpMenu(helpMenu);
        setJMenuBar(mbar);

        GridBagLayout gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;

	urlLabel = new JLabel(" To-URL: ", JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
	gridbag.setConstraints(urlLabel, c);
	getContentPane().add(urlLabel);

        c.weightx = 1.0;
	urlField = new JTextField();
	urlField.setForeground(Color.black);
        urlField.setBackground(Color.lightGray);
	urlField.setBorder(compound);
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 10;
        c.gridheight = 1;
        gridbag.setConstraints(urlField, c);
        getContentPane().add(urlField);

        c.weighty = 1.0;
        textarea = new JTextArea();
        textarea.setEditable(false);
	textarea.setForeground(Color.black);
        textarea.setBackground(Color.lightGray);
	textarea.setBorder(compound);
	JScrollPane textpane = new JScrollPane(textarea);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 12;
        c.gridheight = 2;
        gridbag.setConstraints(textpane, c);
        getContentPane().add(textpane);

    }
}
