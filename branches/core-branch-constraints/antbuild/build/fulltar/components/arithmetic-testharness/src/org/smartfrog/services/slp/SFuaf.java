/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.slp;



import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * The SFuaf is the base class used for both UA and SA.
 * It is heavily based on the Columbia mslp ua/sa and therefore needs a DA to work.
 * Have been added:
 *  - internal enumeration classes to transform mslp results (strings)
 *    into ServiceLocationEnumeration objects.
 *  - interfaces for external access on the number of attempts at DA discovery,
 *    choice of the DA and so on.
 * There should be one instance of SFuaf associated to each instance of
 * SmartFrog's SLP components. This class is not designed to be
 * shared by several objects. (The ua class uses class variables, obtained
 * by the class through getNewPara, and  modified for each request.
 * To avoid clashes it is therefore better to reserve the modification of this
 * attributes to a single object.)
 * DA discovery related operations remain the same as in mslp's ua.
 * We added changeDA to allow non gui-based DA change.
 *
 * @author Guillaume Mecheneau
 */
public class SFuaf extends ua {
  private String result = null;
  private static int instanceCount = 0;
  /** The number of DA discovery attempts
   *  by default, try five time to discover a when none is scpecified, then fail.
   *  n.b.: mslp implementation does not work without DAs
   */
  public int discoverDAAttemptNumber = 5;

  public class internalUaAction extends uaAction{
    internalUaAction(ua uaf) {
      super(uaf);
    }
    public void action(slpMsgParser parser, byte[] buf, int index) {
	int func_id = parser.getFuncID();
	int ia[] = { index };
        parser.LangTag(buf, ia);
	switch (func_id) {
	    case Const.SrvTypeRply:
	       parser.SrvTypeReply(buf, ia);
		prtReply(parser, parser.getTypeList());
	        break;
	    case Const.SrvAck:
		parser.SrvAck(buf, ia);
		prtErrCode(parser.getEcode());
		break;
	    case Const.AttrRply:
			parser.AttrReply(buf, ia);
		prtReply(parser, parser.getAttrList());
		break;
	    case Const.SrvRply:
		parser.SrvReply(buf, ia);
		prtReply(parser, parser.getUrlList());
		break;
	    case Const.DAAdvert:
		parser.DAAdvert(buf, ia);
		String s = Util.url2dname(parser.getURL()); // domian name of DA
		uaf.addDA(s, parser.getScope());
		break;
	    default:
		System.err.println("Unknown SLP packet");
		break;
	}
    }


    private void prtErrCode(int n) {
        switch (n) {
    	    case Const.OK:				// 0
	        uaf.append("OK");
	        break;
    	    case Const.LANGUAGE_NOT_SUPPORTED:		// 1
	        uaf.append("LANGUAGE_NOT_SUPPORTED");
	        break;
    	    case Const.PARSE_ERROR:			// 2
	        uaf.append("PARSE_ERROR");
	        break;
    	    case Const.INVALID_REGISTRATION:		// 3
	        uaf.append("INVALID_REGISTRATION");
	        break;
    	    case Const.SCOPE_NOT_SUPPORTED: 		// 4
	        uaf.append("Scope NOT supported");
	        break;
    	    case Const.AUTHENTICATION_UNKNOWN:		// 5
	        uaf.append("AUTHENTICATION_UNKNOWN");
	        break;
    	    case Const.AUTHENTICATION_ABSENT:		// 6
	        uaf.append("AUTHENTICATION_ABSENT");
	        break;
    	    case Const.AUTHENTICATION_FAILED:		// 7
	        uaf.append("AUTHENTICATION_FAILED");
	        break;
    	    case Const.VER_NOT_SUPPORTED:		// 9
	        uaf.append("VER_NOT_SUPPORTED");
	        break;
    	    case Const.INTERNAL_ERROR:			// 10
	        uaf.append("INTERNAL_ERROR");
	        break;
    	    case Const.DA_BUSY_NOW:			// 11
	        uaf.append("DA_BUSY_NOW");
	        break;
    	    case Const.OPTION_NOT_UNDERSTOOD:		// 12
	        uaf.append("OPTION_NOT_UNDERSTOOD");
	        break;
    	    case Const.INVALID_UPDATE:			// 13
	        uaf.append("INVALID_UPDATE");
	        break;
    	    case Const.MSG_NOT_SUPPORTED:		// 14
	        uaf.append("MSG_NOT_SUPPORTED");
	        break;
    	    case Const.REFRESH_REJECTED:		// 15
	        uaf.append("REFRESH_REJECTED");
	        break;
    	    default:
	        uaf.append("Unknown SrvAck error code");
	        break;
        }
    }

    private void prtReply(slpMsgParser parser, String s)  { //throws ServiceLocationException{
 //     System.out.println("prtReply got "+ s);
      if (parser.getEcode() != Const.OK) {
        prtErrCode(parser.getEcode());
//          throw new ServiceLocationException((short)parser.getEcode());
        return;
      } else if (s.equals("")) {
        //uaf.append("No match!");
        result = null;
      } else {
        result = s;
      }
//      notifyResult();
   }
  }

  public void append(String s) {
   // super.append(s);
//   System.out.println(" mslp Message -" + s );

    }
  /**
   * Internal enumeration class for service URLs. As mslp's ua result is a
   * unique String formed with the result, these are based on StringTokenizer.
   */
  public class MSLPServiceLocationEnumerationImpl implements ServiceLocationEnumeration {
    public int rqstType; //= Const.SrvRqst;
    StringTokenizer st;
    MSLPServiceLocationEnumerationImpl() { //throws ServiceLocationException {
      setRequestType();
      mesgHandler(rqstType); // synchronous call !
      if (result!=null) {
        st = new StringTokenizer(result,getSeparator());
      }
    }
    protected void setRequestType(){
      rqstType = Const.SrvRqst;
    }
    private String getSeparator() {
      return ",";
    }
    public synchronized boolean hasMoreElements() {
      if (result!=null) {
        return st.hasMoreElements();
      } else {
        return false; //true;
      }
    }
    protected Object convert(String s){
      return new ServiceURL(s);
    }
    public synchronized Object nextElement() throws NoSuchElementException {
      if (result!=null) {
        return convert((String) st.nextElement());
      } else {
        throw new NoSuchElementException();
        //return null;
      }
    }
    // no correct implementation for the moment
    public synchronized Object next() throws ServiceLocationException {
        return nextElement();
    }
  }

  /**
   * Internal enumeration class for service attributes.
   * Converts the mslp string into ServiceLocationAttribute objects.
   */
  public class MSLPServiceLocationAttributeImpl extends MSLPServiceLocationEnumerationImpl {

    protected void setRequestType(){
      rqstType = Const.AttrRqst;
    }

    protected Object convert(String s){
     // trim parenthesis
      s = s.substring(s.indexOf("(")+1,s.indexOf(")"));
      int equalIndex = s.indexOf("=");
      String attId = s.substring(0,equalIndex);
      Vector attValues = new Vector();
      for (Enumeration e = new StringTokenizer(s.substring(equalIndex+1),",");e.hasMoreElements();){
        attValues.add(e.nextElement());
      }
      return new ServiceLocationAttribute(attId,attValues);
    }
  }
  /**
   * Internal enumeration class for service types.
   */
  public class MSLPServiceTypeEnumerationImpl extends MSLPServiceLocationEnumerationImpl{
    protected void setRequestType(){
      rqstType = Const.SrvTypeRqst;
    }
    protected Object convert(String s) {
      return new ServiceType(s);
    }
  }

// The following int and strings replace the strings acquired through the mslp ua/sa GUI.
  int timeint = 1;
  String srvtString = "";
  String scopeString = "";
  String ltagString = "";
  String urlString = "";
  String timeString = "";
  String otheraptnString = "";

/**
 * Constructor
 */

  public SFuaf() {
    instanceCount++;
    daListModel = new DefaultListModel();
    daList = new JList(daListModel);
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
    uac      =  new internalUaAction(this);
                //new uaAction(this);
  }
  // !! Will stop the loop but definitely not kill the thread...
  protected void stopThreads() {
    findDa.stopThread();
  }
/**
 * Allows users to ask for DA discovery.
 */
  public void reDiscoverDA() {
    findDa.discover(xid); // need to re-discover DA
    xid = (xid + 1) & 0xFFFF;
  }
/**
 * Gets the current list of discovered DAs.
 * @return : the listModel containing the DAs
 */
  public DefaultListModel getDAList(){
    if (daListModel !=null)
      return daListModel;
    else
      return null;
  }
/**
 * Sets the currently used DA
 * @param da the DA to be used ( format : daHost+" "+scopes_list )
 */
  public void changeDA(String da) {
    String tmp = da;
    int index = tmp.indexOf(" ");
    dirAgent = tmp.substring(0, index);
    append("--Using " + dirAgent + " as DA");
    if (tcp != null) {  // close previous tcp connection
      tcp.close();
      tcp = null;
    }
  }
/**
 * Get the new parameters accessed through the several strings
 */
  public void getNewPara() {
    SRVT  = srvtString;
    SCOPE = scopeString;
    LTAG  = ((ltagString ==  null ) ||(ltagString.equals("") )) ? Const.defaultLtag : ltagString ;
    URL   = urlString;
    LTIME = ( timeint <= 0 ) ? 1 : timeint;
    APTN   = otheraptnString;
  }
  /**
   * To set the number of time the UA/SA will try to discover the DA.
   * @param n the number of attempts to be made
   */
  protected void setRetry(int n){
    discoverDAAttemptNumber = n;
  }

  /**
   * If no DA has been chosen, take the first of the discovered list.
   * If there is no discovered DA, return null.
   * @param id the id of the message type
   */
  protected synchronized void mesgHandler(int id) {
   // System.out.println( " mesgHandler "+ id );
	// mslp does not work without DA. Either SFuaf tries repeatedly as long as it does not have a DA ,
        // or the SFuaf user checks there is a da in the list before calling it.
        // for the moment we chose the first alternative.
        int retry = discoverDAAttemptNumber;
        while (dirAgent == null) {
          if (daListModel.size()!=0) {
     //       System.out.println(" Using existing DA ");
            changeDA((String) daListModel.firstElement()); // take first DA on the list
          } else if (retry-- > 0) {                         // or try to get one
  //          System.out.println(" Trying to discover a DA ");
            reDiscoverDA();
            try {
              Thread.sleep(Const.daDiscover_interval) ;
            } catch (Exception e){}
          } else {
            // eventually give up : should throw an exception....
            return;
          }
        }
	getNewPara(); 	// get all parameters previously set by the operation request functions.
	byte[] sendMesg = null;
	switch (id) { // compose the sending message
	    case Const.SrvReg: // attribute
//                System.out.println("SFuaf send reg " +
//                "[xid=" +xid+"], [slpFlag & Const.fresh_flag= " + (slpFlag & Const.fresh_flag) +
//                "],\n[LTAG = "+LTAG +
//                 "],\n[ URL ="+URL+
//                 "],\n[ LTIME ="+ LTIME +
//                 "],\n[ SRVT ="+SRVT+
//                 "],\n[ SCOPE ="+SCOPE+
//                 "],\n[ APTN ="+ APTN);
		sendMesg = composer.SrvReg(xid, slpFlag & Const.fresh_flag,
		      	   LTAG, URL, LTIME, SRVT, SCOPE, APTN);
                if (useMF) {
          	    sendMesg = composer.MeshFwdExt(sendMesg, Const.RqstFwd,
				System.currentTimeMillis(), dirAgent, 0);
		}

		break;
	    case Const.SrvDeReg:
//                System.out.println("SFuaf send DEreg " +
//                "[xid=" +xid+
//                "],\n[LTAG = "+LTAG +
//                 "],\n[ URL ="+URL+
//                 "],\n[ LTIME ="+ LTIME +
//                 "],\n[ SCOPE ="+SCOPE+
//                 "],\n[ APTN ="+ APTN);
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
	    case Const.SrvRqst: // predicate
//                System.out.println(" Predicate is "+ APTN);
		sendMesg = composer.SrvRqst(xid, Const.normal_flag, LTAG,
				PR, SRVT, SCOPE, APTN, SPI);
		break;
	    case Const.AttrRqst:
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

/**
 * Converts SLP scopes vector into mslp scopes string.
 * @param scopeNames the Vector containing the scopes
 * @return a comma-separated list of scopes as a String
 */
  protected String getScopeList(Vector scopeNames) {
    String scopeList ="";
    for (Enumeration e = scopeNames.elements(); e.hasMoreElements() ; ){
      String newScope = (String) e.nextElement();
      scopeList += (scopeList.equals("")) ? "" : ",";
      scopeList += newScope;
    }
    return scopeList;
  }

/**
 * Convert SLPAttributes into mslp attribute string.
 * Assume attributes have single values. // again !
 * @param SLPAttributes a vector of ServiceLocationAttribute objects
 * @param includeValues flag to indicate whether values should be included in the result or not
 * @return the attribute list as a String
 */
  protected String getAttributeList(Vector SLPAttributes, boolean includeValues){
    String res = "";
    if (SLPAttributes!=null) {
      for(Enumeration e = SLPAttributes.elements();e.hasMoreElements();){
        ServiceLocationAttribute att = (ServiceLocationAttribute) e.nextElement();
        res = res.equals("")?res:res+",";
        res+= (includeValues)? "("+att.getId()+"="+(String)att.getValues().firstElement()+")":att.getId();
      }
    }
    return res;
  }


/**
 * Return an enumeration of ServiceURL objects for service matching the query.
 * @param serviceType the ServiceType to be found
 * @param scopeNames the scopes in which the services should be found
 * @param locale the locale of the service
 * @param query the query on service attributes
 * @return an Enumeration of ServiceURL objects matching the query
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized ServiceLocationEnumeration findServices(ServiceType serviceType,
                                                  Vector scopeNames,
                                                  Locale locale,
                                                  String query) throws ServiceLocationException {
    String serviceTypeName = serviceType.toString();
    srvtString = serviceTypeName;
    scopeString = getScopeList(scopeNames);
    ltagString = locale.getLanguage().substring(0,2);
//    System.out.println( "\n\n\n QUERY for service type "+serviceType+" IS: " +query+"\n\n\n");
    otheraptnString = query;
   // mesgHandler(Const.SrvRqst);
    return new MSLPServiceLocationEnumerationImpl();
    //return new MSLPServiceLocationEnumerationImpl(result);
  }
/**
 * Returns an enumeration of known service types for this scope and naming authority.
 * Unless a proprietary or experimental service is being discovered,
 * the namingAuthority parameter should be null.
 * @param namingAuthority the naming authority of the service types to be found
 * @param scopeNames the scopes in which the service types should be found
 * @return an Enumeration of ServiceType objects matching the query
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized ServiceLocationEnumeration findServiceTypes(String namingAuthority,
                                                  Vector scopeNames) throws ServiceLocationException {
    otheraptnString = namingAuthority ;
    scopeString = getScopeList(scopeNames)  ;
   // mesgHandler(Const.SrvTypeRqst);
    return new MSLPServiceTypeEnumerationImpl();
  }

/**
 * Returns an enumeration of the attributes and their values for the given ServiceURL.
 * @param serviceURL the ServiceURL whose attributes are required
 * @param scopeNames the scopes of the service
 * @param locale the locale of the service locator
 * @param attributeIds  a vector of the desired service attributes (empty if you want them all)
 * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the attributeIds
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized ServiceLocationEnumeration findAttributes(ServiceURL serviceURL,
                                                  Vector scopeNames,
                                                  Locale locale,
                                                  Vector attributeIds) throws ServiceLocationException {
    String sURL = serviceURL.toString();
    srvtString = "";
    scopeString = getScopeList(scopeNames);
    ltagString = locale.getLanguage().substring(0,2);
    urlString = sURL;

    otheraptnString = getAttributeList(attributeIds,true);
 //   mesgHandler(Const.AttrRqst);
//System.out.println( "--SFuaf-- find attrib :"+ otheraptnString +" =?"  +attributeIds+" \n-- "+ltagString+" \n-- "+sURL);
    return new MSLPServiceLocationAttributeImpl();
  }
/**
 * Return an enumeration of all attributes for all serviceURLs having this
 * service type in the specified locale.
 * The attributes id returned match the id patterns in the parameter Vector
 * @param serviceType the type of the service
 * @param scopeNames the scopes of the service type
 * @param locale the locale of the service locator
 * @param attributeIds  a vector of strings identifying the desired service attributes
 * @return a ServiceLocationEnumeration of ServiceLocationAttribute objects matching the attributeIds
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized ServiceLocationEnumeration findAttributes(ServiceType serviceType,
                                                  Vector scopeNames,
                                                  Locale locale,
                                                  Vector attributeIds) throws ServiceLocationException {
    String serviceTypeName = serviceType.toString();
    srvtString = serviceTypeName;
    otheraptnString = serviceType.getNamingAuthority();
    ltagString = locale.getLanguage().substring(0,2);
     scopeString = getScopeList(scopeNames);
    otheraptnString = getAttributeList(attributeIds,true);
  //  mesgHandler(Const.AttrRqst);
    return new MSLPServiceLocationAttributeImpl();
  }

/**
 * Register the service advertisement in all provided scopes.
 * Locale should be provided by the wrapper.
 * @param serviceURL the complete URL of the service
 * @param scopeNames the scopes of the service
 * @param locale the locale of the service agent
 * @param serviceLocationAttributes a vector of ServiceLocationAttribute objects describing the service
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized void  register(ServiceURL serviceURL,
                                  Vector scopeNames,
                                  Locale locale,
                                  Vector serviceLocationAttributes) throws ServiceLocationException {
    // Convert the data into mSLP-readable variable
    srvtString = (serviceURL.getServiceType()).toString();
    scopeString = getScopeList(scopeNames);
    urlString = serviceURL.toString();
    ltagString = locale.getLanguage().substring(0,2);
    timeint = (serviceURL.getLifetime());
    otheraptnString = getAttributeList(serviceLocationAttributes,true);
    mesgHandler(Const.SrvReg);
  }
/**
 * Add attributes to a service advertisement in the locale provided
 * Locale should be provided by the wrapper.
 * @param serviceURL the URL of the service
 * @param scopeNames the scopes of the service
 * @param locale the locale of the service agent
 * @param serviceLocationAttributes a vector of ServiceLocationAttribute objects to add to the advertisement
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized void  addAttributes(ServiceURL serviceURL,
                                      Vector scopeNames,
                                      Locale locale ,
                                      Vector serviceLocationAttributes) throws ServiceLocationException {
    //mslp registers both attributes and the services in one function...
    register(serviceURL,scopeNames,locale,serviceLocationAttributes);
  }
/**
 * Deregister the service advertisement in all scopes and for all
 * locales in which it was advertised. The provided scopes are used to match the
 * SA and the DA.
 * @param serviceURL the URL of the service to deregister
 * @param scopeNames the scopes which this Service Agent is configured with.
 * @throws ServiceLocationException if the operation fails
 */

  public synchronized void deregister(ServiceURL serviceURL,Vector scopeNames) throws ServiceLocationException {//, Vector scopeNames)
    srvtString = (serviceURL.getServiceType()).toString();
    scopeString = getScopeList(scopeNames); // hopefully only used by mslp as DA/SA match
    urlString = serviceURL.toString();
    timeint = (serviceURL.getLifetime());
    otheraptnString = ""; // here deregister all attributes.
    mesgHandler(Const.SrvDeReg);
  }
/**
 * Deregister the service advertisement in all provided scopes and for all
 * locales in which it was advertised.
 * @param serviceURL the URL of the service to deregister
 * @throws ServiceLocationException if the operation fails
 */

//  public synchronized void deregister(ServiceURL serviceURL) throws ServiceLocationException {//, Vector scopeNames)
//    srvtString = (serviceURL.getServiceType()).toString();
//    scopeString = ""; // getScopeList(scopeNames);
//    urlString = serviceURL.toString();
//    otheraptnString = ""; // here deregister all attributes.
//    mesgHandler(Const.SrvDeReg);
//  }
/**
 * Remove attributes from all service advertisement where they appear
 * @param serviceURL the URL of the service advertised
 * @param scopeNames the scopes of the service agent
 * @param serviceLocationAttributes the vector of ServiceLocationAttribute objects indicating the ids to remove from the advertisement
 * @throws ServiceLocationException if the operation fails
 */
  public synchronized void deleteAttributes(ServiceURL serviceURL,
                                          Vector scopeNames,
                                          Vector serviceLocationAttributes) throws ServiceLocationException {
    srvtString = (serviceURL.getServiceType()).toString();
    scopeString = getScopeList(scopeNames);
    urlString = serviceURL.toString();
    timeint = (serviceURL.getLifetime());
    otheraptnString = getAttributeList(serviceLocationAttributes,false); //attributes to be deregistered.
    mesgHandler(Const.SrvDeReg);
  }
}
