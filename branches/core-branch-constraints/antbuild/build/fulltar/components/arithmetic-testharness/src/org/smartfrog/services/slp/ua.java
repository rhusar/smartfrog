package org.smartfrog.services.slp;
/**
 * A simplified implementation for SLPv2 user agent (UA) plus
 * service agent (SA) with an easy to use GUI.
 * It has following features:
 *
 *    (1) Tries to find all available DAs, building a DA list with scopes.
 *    (2) User choosees to use which DA in the DA list (default is first DA).
 *    (3) Supports both TCP and UDP transfer mode.
 *    (4) All UDP messages, if no response from DA, try three times.
 *    (5) SA is mesh-aware, which means it can use Mesh-Forwarding (MeshFwd)
 *        extension (de)registration.
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 * Mods : mainly shifts from private to public for quick reuse.
 */

import java.util.*;
import java.io.*;

import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class ua extends JFrame implements ActionListener, ListSelectionListener {

    static int width = 600;
    static int height = 350;

    public static void main(String args[]) {
	try {
	    // we need a trick to get the absolute domain name of the host
	    String hname = (InetAddress.getLocalHost()).getHostAddress();
	    hname = (InetAddress.getByName(hname)).getHostName();
   	    ua f = new ua(hname);
   	    f.setSize(width, height);
	    f.setForeground(Color.black);
	    f.setBackground(Color.lightGray);
	    f.show();
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
       	    System.exit(1);
	}
    }

    JMenuBar mbar;
    JMenu fileMenu, funcMenu, setupMenu, helpMenu;

    // File Menu
    JMenuItem exitItem;

    // Normal Operation Menu
    JMenuItem srvReg, srvDeReg, srvTypeRqst, srvRqst, attrRqst;

    //---------------------------------------------------------
    // Setup Menu:
    // (1) discover DA
    // (2) choose UPD/TCP transfer mode, default UDP
    // (3) whether to use MeshFwd extension, default NOT using
    // (4) fresh or incremental SrvReg
    //---------------------------------------------------------
    JMenuItem discoverDA;
    JRadioButtonMenuItem udpMode, tcpMode, freshReg, incReg;
    JCheckBoxMenuItem MFext;

    // Help Menu
    JMenuItem aboutItem;

    // GUI components
    // APTN stands for Attribute, Predicate, Tag, or Naming Authority.
    // As these four items can not appear in an SLPv2 message at the same
    // time, they share one GUI component.
    String[]   aptnString = {"Attribute", "Predicate", "TagList", "NamingA."};
    JComboBox  aptnList;
    JLabel     srvtLabel, scopeLabel, ltagLabel, urlLabel, timeLabel;
    JTextField srvtField, scopeField, ltagField, urlField, timeField, aptnField;
    JTextArea  textarea;
    JList            daList;
    DefaultListModel daListModel;
    String SRVT, SCOPE, URL, LTAG, APTN, PR="", SPI="";
    int    LTIME, xid;

    // Setup components
    uaFindDa       findDa;   	// discover DA thread
    String         dirAgent;	// the domain name of current chosen DA
    String         tranMode = "udp";
    slpUdpHandler  udp;
    slpTcpHandler  tcp = null;
    boolean        useMF = true;  // Using Mesh-Forward extension
    int		   slpFlag = Const.fresh_flag;

    // Basic operation classes
    slpMsgParser   parser;	// parser SLPv2 message
    slpMsgComposer composer;	// compose SLPv2 message
    uaAction       uac;		// UA actions for each SLPv2 message
    ua(){}; // to provide a public default constructor
    ua(String hname) {
        super("Welcome to mSLP UA/SA <"+hname+">");
	createGUI();

	// Setup UDP socket with the timeout for receive
	try {
    	    DatagramSocket udpSocket = new DatagramSocket();
	    udpSocket.setSoTimeout(Const.SoTimeout);
	    udp = new slpUdpHandler(udpSocket);
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}

	// Starting DA discovery thread
	findDa = new uaFindDa(this);
	findDa.start();

	// Basic operation classes
	parser   = new slpMsgParser();
	composer = new slpMsgComposer();
	uac      = new uaAction(this);
    }

    public void append(String s) {
	textarea.append(s + "\n");
	int line = textarea.getLineCount();
	try {
            int pos = textarea.getLineStartOffset(line);
            textarea.setCaretPosition(pos);
        } catch (Exception ex) {
            // System.out.println("Exception " + ex);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitItem) {
            System.exit(0);
        } else if (e.getSource() == udpMode) {
	    setUDPmode();
        } else if (e.getSource() == tcpMode) {
	    setTCPmode();
        } else if (e.getSource() == MFext) {
	    setMFext();
        } else if (e.getSource() == freshReg) {
	    setFreshReg();
        } else if (e.getSource() == incReg) {
	    setIncReg();
        } else if (e.getSource() == discoverDA) {
	    findDa.discover(xid); // need to re-discover DA
	    xid = (xid + 1) & 0xFFFF;
        } else if (e.getSource() == srvReg) {
	    mesgHandler(Const.SrvReg);
        } else if (e.getSource() == srvDeReg) {
	    mesgHandler(Const.SrvDeReg);
        } else if (e.getSource() == srvTypeRqst) {
	    mesgHandler(Const.SrvTypeRqst);
        } else if (e.getSource() == srvRqst) {
	    mesgHandler(Const.SrvRqst);
        } else if (e.getSource() == attrRqst) {
	    mesgHandler(Const.AttrRqst);
	}
    }

//-----------------------
// Setup menu operations
//-----------------------
/**
 * DA discovery related operations: on DA list (domain name + scope)
 *    (1) add a new DA to the list
 *    (2) choose a DA from the list
 *    (3) remove a DA from the list (failure)
 *    (4) remove all DAs, do DA re-discovery
 */
    public void addDA(String url, String scope) {

	String item = new String(url+" "+scope);
	if (!daListModel.contains(item)) {
	    daListModel.addElement(item);
	    append("Adding <" + item + "> to DA list");
	    daList.setSelectedIndex(0);
	}
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        if (e.getSource() == daList) {
            String tmp = (String)daList.getSelectedValue();
	    int index = tmp.indexOf(" ");
	    dirAgent = tmp.substring(0, index);
            append("Using " + dirAgent + " as DA");
	    if (tcp != null) {  // close previous tcp connection
		tcp.close();
		tcp = null;
	    }
        }
    }

    private void removeDA() {
	append("Failure, no response from " + dirAgent);
	int index = daList.getSelectedIndex();
	daListModel.remove(index);
	dirAgent = null;
	if (daListModel.size() > 0) {
	    daList.setSelectedIndex(0);
	}
    }

    public void clearDA() {
	daListModel.clear();
	dirAgent = null;
    }

/**
 * Setup transfer mode: UDP or TCP
 */
    private void setUDPmode() {
	tranMode = "udp";
	append("Switch to UDP transfer mode");
    }

    private void setTCPmode() {
	tranMode = "tcp";
	append("Switch to TCP transfer mode");
    }

/**
 * Setup to use mesh-forwarding extension or NOT
 */
    private void setMFext() {
	if (MFext.isSelected()) {
	    useMF = true;
	    append("Use mesh forwarding");
	} else {
	    useMF = false;
	    append("No mesh forwarding");
	}
    }

/**
 * Setup to use fresh or incremental SrvReg
 */
    private void setFreshReg() {
	slpFlag = slpFlag | Const.fresh_flag;  // set fresh_flag
	append("Use fresh SrvReg");
    }

    private void setIncReg() {
        slpFlag = slpFlag & ~Const.fresh_flag; // clear fresh_flag
	append("Use incremental SrvReg");
    }

//---------------------------------------------------------------------
// Send an SLP message to the current DA (dirAgent), and process reply
//---------------------------------------------------------------------
    private void mesgHandler(int id) {
	if (dirAgent == null) { // choose to use DA, alternative to multicast
	    append("Discover DA first, then choose one");
	    return;
	}
	getNewPara(); 	// get all parameters from GUI

	if (!APTN.equals("")) {  // use the proper parameter if not empty
	    String selected = (String)aptnList.getSelectedItem();
	    switch (id) {
		case Const.SrvReg:	// attribute
		    if (!selected.equals("Attribute")) {
			append("Please choose Attribute");
			return;
		    }
		    break;
	     	case Const.SrvDeReg: 	// tag list
	    	case Const.AttrRqst: 	// tag list
		    if (!selected.equals("TagList")) {
			append("Please choose TagList");
			return;
		    }
		    break;
	    	case Const.SrvTypeRqst:	// naming authority
		    if (!selected.equals("NamingA.")) {
			append("Please choose NamingA. (Naming Authority)");
			return;
		    }
		    break;
	    	case Const.SrvRqst: 	// predicate
		    if (!selected.equals("Predicate")) {
			append("Please choose Predicate");
			return;
		    }
		    break;
	    }
	}

	byte[] sendMesg = null;
	switch (id) { // compose the sending message
	    case Const.SrvReg: // attribute
		sendMesg = composer.SrvReg(xid, slpFlag & Const.fresh_flag,
		      	   LTAG, URL, LTIME, SRVT, SCOPE, APTN);
		if (useMF) {
		    sendMesg = composer.MeshFwdExt(sendMesg, Const.RqstFwd,
				System.currentTimeMillis(), dirAgent, 0);
		}
		break;
	    case Const.SrvDeReg: // tag
		sendMesg = composer.SrvDeReg(xid, LTAG, SCOPE, URL, LTIME,
					     APTN);
		if (useMF) {
		    sendMesg = composer.MeshFwdExt(sendMesg, Const.RqstFwd,
					System.currentTimeMillis(), null, 0);
		}
		break;
	    case Const.SrvTypeRqst:
		sendMesg = composer.SrvTypeRqst(xid, LTAG, PR, APTN, SCOPE);
		break;
	    case Const.SrvRqst:
		sendMesg = composer.SrvRqst(xid, Const.normal_flag, LTAG,
				PR, SRVT, SCOPE, APTN, SPI);
		break;
	    case Const.AttrRqst: // tag
		String target = URL;	// try URL first, srvType second
		if (target.equals("")) target = SRVT;
		sendMesg = composer.AttrRqst(xid, LTAG, PR, target, SCOPE,
						APTN, SPI);
		break;
	}

    	if (tranMode.equals("udp")) { // send message & process response
	    uaUdpHandler(sendMesg);
    	} else {
	    uaTcpHandler(sendMesg);
	}
	xid = (xid + 1) & 0xFFFF;
    }

    private void getNewPara() {
	SRVT  = srvtField.getText();
	SCOPE = scopeField.getText();
	LTAG  = ltagField.getText();
	URL   = urlField.getText();
	LTIME = Integer.parseInt(timeField.getText());
	APTN   = aptnField.getText();
    }

/**
 * Try to send a TCP message to DA, and process the response from DA
 * If no response, then give up
 *  Mod: turned from private to protected
 */
    protected void uaTcpHandler(byte[] sendMesg) {
        if (tcp == null) connectDA();
	if (tcp == null) return;

   //     System.out.println(" tcp handler ");
    	tcp.send(sendMesg, sendMesg.length);
	byte[] recvMesg;
	if ((recvMesg = tcp.getHeader()) == null) { // TimeOut
	    removeDA();
  //          System.out.println(" time out ");
	    return;
	}
	parser.Header(recvMesg);
	if ((recvMesg = tcp.getBody(parser.getPacketLen())) == null) { //TimeOut
	    removeDA();
  //          System.out.println(" the other time out ");
	    return;
	}
	uac.action(parser, recvMesg, 0);
    }

/**
 * Try to send a UDP message to DA, and process the response from DA
 * If no response, try at most three times, then give up
 *  Mod: turned from private to protected
 */
    protected void uaUdpHandler(byte[] sendMesg) {
  //     System.out.println(" udp handler ");

	for (int i=1; i<=Const.maxTryNum; i++) {
            udp.send(sendMesg, dirAgent, Const.port);
  //          System.out.println(" before udp receive ");
	    byte[] recvMesg = udp.receive();
  //           System.out.println(" after udp receive ");
	    if (recvMesg == null) {
  //            System.out.println(" Message null received on udp --> timeout ? ");
	      continue;
	    }
   //         System.out.println(" getting udp answer " + i);
            parser.Header(recvMesg);
            uac.action(parser, recvMesg, Const.header_len);
	    return;
	}
	removeDA();
   //     System.out.println("da time out ");

    }

/**
 * make a TCP connection for TCP transfer mode
 */
    private void connectDA() {
        try {
	    InetAddress addr = InetAddress.getByName(dirAgent);
            append("Connect to: "+addr.toString()+":"+Const.port);
	    Socket socket = new Socket(addr, Const.port);
	    socket.setSoTimeout(Const.SoTimeout);
	    tcp      = new slpTcpHandler(socket, null);
	} catch (ConnectException e) {
	    System.err.println(e.getMessage());
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

//----------------------
// Create GUI for UA/SA
//----------------------
    private void createGUI() {
        fileMenu = new JMenu("File");
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        funcMenu = new JMenu("Operation");

	// SA operations
        srvReg = new JMenuItem("Service Register");
        srvReg.addActionListener(this);
        funcMenu.add(srvReg);

        srvDeReg = new JMenuItem("Service Deregister");
        srvDeReg.addActionListener(this);
        funcMenu.add(srvDeReg);

	// UA operations
	funcMenu.addSeparator();
        srvTypeRqst = new JMenuItem("Service Type Request");
        srvTypeRqst.addActionListener(this);
        funcMenu.add(srvTypeRqst);

        srvRqst = new JMenuItem("Service Request");
        srvRqst.addActionListener(this);
        funcMenu.add(srvRqst);

        attrRqst = new JMenuItem("Attribute Request");
        attrRqst.addActionListener(this);
        funcMenu.add(attrRqst);

	setupMenu = new JMenu("Setup");

	discoverDA = new JMenuItem("DA Discovery");
	discoverDA.addActionListener(this);
	setupMenu.add(discoverDA);

	setupMenu.addSeparator();
	ButtonGroup transGroup = new ButtonGroup();
        udpMode = new JRadioButtonMenuItem("UDP Mode");
	udpMode.setSelected(true);
	udpMode.addActionListener(this);
        transGroup.add(udpMode);
        setupMenu.add(udpMode);

        tcpMode = new JRadioButtonMenuItem("TCP Mode");
	tcpMode.addActionListener(this);
        transGroup.add(tcpMode);
        setupMenu.add(tcpMode);

	setupMenu.addSeparator();
	MFext = new JCheckBoxMenuItem("Use Mesh-Forward", true);
	MFext.addActionListener(this);
	setupMenu.add(MFext);

	setupMenu.addSeparator();
	ButtonGroup regGroup = new ButtonGroup();
        freshReg = new JRadioButtonMenuItem("Use Fresh SrvReg");
	freshReg.setSelected(true);
	freshReg.addActionListener(this);
        regGroup.add(freshReg);
        setupMenu.add(freshReg);

        incReg = new JRadioButtonMenuItem("Use Inc. SrvReg");
	incReg.addActionListener(this);
        regGroup.add(incReg);
	setupMenu.add(incReg);

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
//      mbar.setHelpMenu(helpMenu); // Not implemented by Java Swing
        setJMenuBar(mbar);

        GridBagLayout gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;

        c.weightx = 0.0;
        srvtLabel = new JLabel("SrvType:", JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(srvtLabel, c);
        getContentPane().add(srvtLabel);

        c.weightx = 0.25;
        srvtField = new JTextField(Const.defaultStype);
	srvtField.setForeground(Color.black);
        srvtField.setBackground(Color.lightGray);
	srvtField.setBorder(compound);
        srvtField.addActionListener(this);
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(srvtField, c);
        getContentPane().add(srvtField);

        c.weightx = 0.0;
        scopeLabel = new JLabel("Scope:", JLabel.CENTER);
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(scopeLabel, c);
        getContentPane().add(scopeLabel);

        c.weightx = 0.25;
        scopeField = new JTextField(Const.defaultScope);
	scopeField.setForeground(Color.black);
        scopeField.setBackground(Color.lightGray);
	scopeField.setBorder(compound);
        scopeField.addActionListener(this);
        c.gridx = 6;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(scopeField, c);
        getContentPane().add(scopeField);

        c.weightx = 0.0;
        ltagLabel = new JLabel("LangTag:", JLabel.CENTER);
        c.gridx = 8;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(ltagLabel, c);
        getContentPane().add(ltagLabel);

        c.weightx = 0.25;
        ltagField = new JTextField(Const.defaultLtag);
	ltagField.setForeground(Color.black);
        ltagField.setBackground(Color.lightGray);
	ltagField.setBorder(compound);
        ltagField.addActionListener(this);
        c.gridx = 10;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(ltagField, c);
        getContentPane().add(ltagField);

        c.weightx = 0.25;
        c.weighty = 1.0;
	daListModel = new DefaultListModel();
	daList = new JList(daListModel);
	daList.setBackground(Color.lightGray);
	daList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	daList.addListSelectionListener(this);
	JScrollPane listPane = new JScrollPane(daList);
	listPane.setBorder(compound);
	listPane.setColumnHeaderView(new JLabel("<DA List>", JLabel.CENTER));
        c.gridx = 12;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 5;
        gridbag.setConstraints(listPane, c);
	getContentPane().add(listPane);

        c.weightx = 0.0;
        c.weighty = 0.0;
        urlLabel = new JLabel("URL:", JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(urlLabel, c);
        getContentPane().add(urlLabel);

        c.weightx = 0.50;
        urlField = new JTextField(Const.defaultURL);
	urlField.setForeground(Color.black);
        urlField.setBackground(Color.lightGray);
	urlField.setBorder(compound);
        urlField.addActionListener(this);
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 6;
        c.gridheight = 1;
        gridbag.setConstraints(urlField, c);
        getContentPane().add(urlField);

        c.weightx = 0.0;
        timeLabel = new JLabel("LifeTime:", JLabel.CENTER);
        c.gridx = 8;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(timeLabel, c);
        getContentPane().add(timeLabel);

        c.weightx = 0.25;
        timeField = new JTextField(Const.defaultLtime);
	timeField.setForeground(Color.black);
        timeField.setBackground(Color.lightGray);
	timeField.setBorder(compound);
        timeField.addActionListener(this);
        c.gridx = 10;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(timeField, c);
        getContentPane().add(timeField);

        c.weightx = 0.0;
        aptnList = new JComboBox(aptnString);
	aptnList.setSelectedIndex(0);
     	aptnList.addActionListener(this);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        gridbag.setConstraints(aptnList, c);
        getContentPane().add(aptnList);

        c.weightx = 0.75;
        aptnField = new JTextField("");
	aptnField.setForeground(Color.black);
        aptnField.setBackground(Color.lightGray);
	aptnField.setBorder(compound);
        aptnField.addActionListener(this);
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 10;
        c.gridheight = 1;
        gridbag.setConstraints(aptnField, c);
        getContentPane().add(aptnField);

        c.weightx = 0.75;
        c.weighty = 1.0;
        textarea = new JTextArea();
        textarea.setEditable(false);
	textarea.setForeground(Color.black);
        textarea.setBackground(Color.lightGray);
	textarea.setBorder(compound);
	JScrollPane textpane = new JScrollPane(textarea);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 12;
        c.gridheight = 2;
        gridbag.setConstraints(textpane, c);
        getContentPane().add(textpane);
    }
}
