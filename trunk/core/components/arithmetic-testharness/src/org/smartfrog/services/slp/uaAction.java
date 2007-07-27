package org.smartfrog.services.slp;

/**
 * Implements the various actions an UA should perform upon receiving
 * an SLPv2 message: (1) parse message, (2) display parsing result
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

public class uaAction {

    ua uaf;

    uaAction(ua uaf) {
	this.uaf = uaf;
    }

    public void action(slpMsgParser parser, byte[] buf, int index) {
	int func_id = parser.getFuncID();
	int ia[] = { index };
        parser.LangTag(buf, ia);
	switch (func_id) {
	    case Const.SrvTypeRply:
		/**
		 * service type list, sperated by comma
		 */
	        parser.SrvTypeReply(buf, ia);
		prtReply(parser, parser.getTypeList());
	        break;
	    case Const.SrvAck:
		parser.SrvAck(buf, ia);
		prtErrCode(parser.getEcode());
		break;
	    case Const.AttrRply:
		/**
		 * attribute list, seperated by comma
		 * (a=1),(b=2),(c=3)
		 */
		parser.AttrReply(buf, ia);
		prtReply(parser, parser.getAttrList());
		break;
	    case Const.SrvRply:
		/**
		 * URL list, seperated by comma
		 * URL1,URL2,URL3
                 */
		parser.SrvReply(buf, ia);
		prtReply(parser, parser.getUrlList());
		break;
	    case Const.DAAdvert:
		/**
		 * DA URL
		 */
		parser.DAAdvert(buf, ia);
		String s = Util.url2dname(parser.getURL()); // domian name of DA
		uaf.addDA(s, parser.getScope());
		break;
	    default:
		System.err.println("Unknown SLP packet");
		break;
	}
    }

    private void prtReply(slpMsgParser parser, String s) {
	if (parser.getEcode() != Const.OK) {
	    prtErrCode(parser.getEcode());
	} else if (s.equals("")) {
	    uaf.append("No match!");
	} else {
	    uaf.append(s);
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
}
