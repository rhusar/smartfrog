package org.smartfrog.services.slp;
/**
 * Implements the various actions a DA should perform upon receiving
 * a SLPv2 message: (1) parse message, (2) compose reply
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */
import java.net.*;
public class daAction {

    slpMsgComposer composer;
    da		   daf;
    slpTcpHandler  tcp;
    slpUdpHandler  udp;
    boolean	   mesh_enhanced;

    daAction(da daf, slpTcpHandler tcp, slpUdpHandler udp) {
	composer = new slpMsgComposer();
	this.daf = daf;
	this.tcp = tcp;
	this.udp = udp;
	mesh_enhanced = daf.getMeshFlag();
    }

    //------------------------------------
    // buf: whole message (header + body)
    // parser: header has been parsed
    //------------------------------------
    public void action(slpMsgParser parser, byte[] buf) {
	byte[] reply, tmp;
	String s, peer;
   	int id, err;
	id = parser.getFuncID();
	if (tcp != null) {
          peer = tcp.getPeerName();
  //        System.out.println("tcp" + peer);
	} else {
          peer = udp.getPeerName();
//	  System.out.println("udp" + peer);
	}
        try {
          peer = InetAddress.getByName(peer).getHostAddress();
 //         System.out.println("peer has been transformed " + peer);
        } catch (Exception exe){exe.printStackTrace();}
	int ia[] = { Const.header_len }; // skip header parsing
	parser.LangTag(buf, ia);
	switch (id) {
	    case Const.SrvReg:
	    case Const.SrvDeReg:
		parser.Extension(buf, peer);
		if (id == Const.SrvReg) {
		    parser.SrvReg(buf, ia);
		    daf.append("SrvReg: " + parser.getURL() + " from " + peer);
		} else {
		    parser.SrvDeReg(buf, ia);
		    daf.append("SrvDeReg from " + peer);
		}
		if (mesh_enhanced && parser.getEcode() == 0 &&
	            parser.getMeshFwdID() == Const.RqstFwd) {
             //         System.out.println(" DA "+daf.getURL()+" Mesh forwarding ");
	    	    daf.forward2Peer(buf, parser.getScope(), daf.getURL());
		}
		reply = composer.SrvAck(parser.getXID(), parser.getLtag(),
					parser.getEcode());
		break;
	    case Const.SrvAck:
		// from peer DA for forwarded Srv(De)Reg, ignore at this time
		reply = null;
		break;
	    case Const.SrvTypeRqst:
		daf.append("SrvTypeRqst from " + peer);
		parser.SrvTypeRqst(buf, ia);
		if ((parser.getFlag() & Const.mcast_flag) != 0 &&
	     	    !Util.shareString(daf.getScope(), parser.getScope(), ",")) {
		    reply = null;	// mcast & unsupported scope
		} else {
		    reply = composer.SrvTypeReply(parser.getXID(),
				parser.getLtag(), parser.getEcode(),
				parser.getTypeList());
		}
		break;
	    case Const.SrvRqst:
		tmp = parser.SrvRqst(buf, ia);
		if (tmp == null) {
		    daf.append("DAAdvert-Request from " + peer);
		    reply = composer.DAAdvert(parser.getXID(),
			Const.normal_flag, "en", daf.getTS(), daf.getURL(),
			daf.getScope(), daf.getAttr(), daf.getSPI());
		} else {
		    daf.append("SrvRqst from " + peer);
		    if ((parser.getFlag() & Const.mcast_flag) != 0 &&
		        !Util.shareString(daf.getScope(),
					  parser.getScope(), ",")) {
			reply = null;	// mcast & unsupported scope
		    } else {
		        reply = composer.SrvReply(parser.getXID(),
					parser.getLtag(), tmp);
		    }
		}
		break;
	    case Const.AttrRqst:
		daf.append("AttrRqst from " + peer);
		parser.AttrRqst(buf, ia);
		if ((parser.getFlag() & Const.mcast_flag) != 0 &&
	     	    !Util.shareString(daf.getScope(), parser.getScope(), ",")) {
		    reply = null;	// mcast & unsupported scope
		} else {
		    reply = composer.AttrReply(parser.getXID(),parser.getLtag(),
				 parser.getEcode(), parser.getAttrList());
		}
		break;
	    case Const.DAAdvert:
		if (mesh_enhanced) { // no actions for non-mesh DA
	   	    parser.DAAdvert(buf, ia);
             //       System.out.println( "Received da davert from "+ parser.getURL());

		    String dname = Util.url2dname(parser.getURL());
                    try {
                     dname = InetAddress.getByName(dname).getHostAddress();
                    }
                    catch (Exception ex) {
                      ex.printStackTrace();
                    }
          //          System.out.println( "Which i will call  "+ dname);
		    String scope = parser.getScope();
    		    slpTcpHandler pc = null; 	// has peering connection?
		    if (tcp != null && dname.equalsIgnoreCase(peer)) pc = tcp;
		    if (dname.equalsIgnoreCase(daf.getURL())) { // my advert
		 	if (!Util.sameHost(dname, peer)) { // fwded my advert
			    daf.ucastDataRqst(daf.getURL(), peer);
			}
		    } else {
		        boolean newPeer = daf.newPeerHandler(dname, scope,
				parser.getAttr(), parser.getDaBootTS(), pc);
		        if (Util.sameHost(dname, peer)) { // original advert
			    if (newPeer) {
			        daf.forward2Peer(buf, scope, dname); // forward
			    } else {
				daf.KeepAlive_Handler(peer); // keepalive
			    }
		        }
		    }
		}
		reply = null;
		break;
	    case Const.DataRqst:
		parser.DataRqst(buf, ia); 	// anti-entropy
		daf.DataRqst_Handler(peer, parser.getAcceptDA(),
			             parser.getAcceptTS());
		reply = null;
		break;
	    default:
		System.err.println("Unknown type for SLP");
		reply = null;
		break;
	}
	if (reply != null) {
	    if (tcp != null) tcp.send(reply, reply.length);
	    if (udp != null) udp.send(reply, peer, udp.getPort());
	}
    }
}
