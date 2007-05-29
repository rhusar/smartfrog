package org.smartfrog.services.slp;
/**
 * The constant definition for mSLP
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

public class Const {

//-----------------------------------------
// The common constants for SLPv2 messages
//-----------------------------------------

	static final int port 		= 2427; // 427 is IANA assigned port
	static final int version 	= 2;
	static final int header_len	= 12;	// not include language tag
	static final int EndOfExt       = 0; 	// end of extension offset
	static final int max_packet	= 4096; // max packet size 
	static final String mcast_addr  = "239.255.255.253";
	static final String DAAdvert_Rqst = "service:directory-agent";
/* 
 * The four defined flags in SLP message common header
 */
	static final int oflow_flag     = 0x8000;
	static final int fresh_flag     = 0x4000;
	static final int mcast_flag     = 0x2000;
	static final int normal_flag    = 0x0000;

/*
 * Some default values for DA/UA/SA
 */

	static final String defaultStype = "service:dummyservice.test"; 	 // Service type // changed: was "bank"
	static final String defaultScope =  "DEFAULT";    // changed : was "columbia";
	static final String defaultLtag  = "en";  	 // Language tag
	static final String defaultURL   = "service:dummyservice.test://dummyURL:1234/dummyURLPath";//changed: was "disco.cs.columbia.edu:1234"
	static final String defaultLtime = "65535"; 	 // Lifetime in seconds
	static final String defaultDbase   = "slp.database"; // database
	static final String defaultSummary = "slp.summary";  // summary

//--------------------------------------
// Timing parameters, all in milisecond
//--------------------------------------

/*
 * The initial delay  & inteval for main timer scheduler
 */
	static final int mainTimer_delay     = 3 * 1000;
	static final int mainTimer_interval  = 10 * 1000;

/*
 * The interval for checking database entry expiration time: 1 hour
 * This operation is expensive (scan whole database), should be infrequent
 */
	static final int dbCheck_interval    = 3600 * 1000;

/*
 * The interval for multicast DAAdvert: 1 minute 
 * We set it much shorter than SLPv2 default value for testing purpose
 */
	static final int daAdvert_interval   = 60 * 1000;

/*
 * The interval for sending a DAAdvert (keepalive) to peers: 1 minute 
 */
	static final int keepalive_interval  = 60 * 1000;

/*
 * The interval for claiming a peer crash or network partition: 1.5 minutes
 * This interval must > keepalive_interval 
 */
	static final int expire_interval     = 90 * 1000;

/*
 * The socket timeout interval for a UDP or TCP receive()
 */
	static final int SoTimeout           = 10 * 1000;

/*
 * The interval for next DA discovery
 */
	static final int daDiscover_interval = 1 * 1000;

/*
 * A UA/SA tries maxTryNum requests if no response is received from a DA
 */
	static final int maxTryNum   	     = 3;

//-------------------------
// SLP message type (1-12)
//-------------------------

	static final int SrvRqst      	= 1;
	static final int SrvRply   	= 2;
	static final int SrvReg   	= 3;
	static final int SrvDeReg     	= 4;
	static final int SrvAck       	= 5;
	static final int AttrRqst     	= 6;
	static final int AttrRply     	= 7;
	static final int DAAdvert     	= 8;
	static final int SrvTypeRqst  	= 9;
	static final int SrvTypeRply  	= 10;
	static final int SAAdvert       = 11;
// Data Request (newly defined), it carries an accept ID.
// it requests new registration states after the sepcified accept ID
	static final int DataRqst       = 12;  

//----------------------------------------------
// five operators for the attribute comparision
//----------------------------------------------

	static final int equal		= 1;   // "="
	static final int less		= 2;   // "<"
	static final int greater	= 3;   // ">"
	static final int lequal		= 4;   // "<="
	static final int gequal		= 5;   // ">="

//----------------------------
// SLP error code (0-7, 9-15)
//----------------------------

	static final short OK				= 0;
	static final short LANGUAGE_NOT_SUPPORTED	= 1;
	static final short PARSE_ERROR			= 2;
	static final short INVALID_REGISTRATION		= 3;
	static final short SCOPE_NOT_SUPPORTED		= 4;
	static final short AUTHENTICATION_UNKNOWN	= 5;
	static final short AUTHENTICATION_ABSENT	= 6;
	static final short AUTHENTICATION_FAILED 	= 7;
	static final short VER_NOT_SUPPORTED		= 9;
	static final short INTERNAL_ERROR		= 10;
	static final short DA_BUSY_NOW			= 11;
	static final short OPTION_NOT_UNDERSTOOD  	= 12;
	static final short INVALID_UPDATE         	= 13;
	static final short MSG_NOT_SUPPORTED      	= 14;
	static final short REFRESH_REJECTED		= 15;

//-------------------------------------------------
// Definitions for the Mesh Forwarding extension 
// It carries the versionTS and the Accept ID for a registration.
// The Accept ID has two parts: 
//     acceptDA: the accept DA for the update 
//     acceptTS: the accept timestamp at the acceptDA
//  (1) extension ID: 0x0006
//  (2) Fwd-ID: 1-2
//-------------------------------------------------

	static final int MeshFwdExt	= 0x0006;

/*
 * It requests to be forwarded, with a new Fwd-ID: "Fwded"
 */
	static final int RqstFwd = 1;

/*
 * It was propagated from a peer DA.
 */
	static final int Fwded = 2;
}
