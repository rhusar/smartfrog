<!-- /**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
-->
<%@ page language="java" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*"%>
<%@ page import="org.smartfrog.sfcore.reference.*"%>
<%@ page import="java.util.*"%>
   
<%@ include file="InitBeans.jsp" %>

<%
    
    String moduleId = request.getParameter("moduleId");
    String version = request.getParameter("version");
    
    String actionTitle =  request.getParameter("title");
    String engine = request.getParameter("engine");
    String distroId = request.getParameter("distroId");
    String number = request.getParameter("number");
	 String jobname = request.getParameter("jobName");
	 String groupName = request.getParameter("groupName");
	  String sch = request.getParameter("sch");
	  String date = request.getParameter("date1");
	  String type = request.getParameter("type");
	  String repeatcount = request.getParameter("repeat");

   
    SFAdapter adapter = new SFAdapter(factory, scheduler);
    String setupcacommandsArr[] = {"mkdir -p /etc/grid-security/certificates",
				"mv /tmp/grid-security.conf.3c9073d6 /etc/grid-security/certificates/",
				"rm -rf /etc/grid-security/*.conf",
				"ln -s /etc/grid-security/certificates/grid-security.conf.3c9073d6 /etc/grid-security/grid-security.conf",
				"mv /tmp/3c9073d6.0 /etc/grid-security/certificates/",
				"mv /tmp/3c9073d6.signing_policy /etc/grid-security/certificates/",
				"mv /tmp/globus-host-ssl.conf.3c9073d6 /etc/grid-security/certificates/",
				"ln -s /etc/grid-security/certificates/globus-host-ssl.conf.3c9073d6 /etc/grid-security/globus-host-ssl.conf",
				"mv /tmp/globus-user-ssl.conf.3c9073d6 /etc/grid-security/certificates/",
				"ln -s /etc/grid-security/certificates/globus-user-ssl.conf.3c9073d6 /etc/grid-security/globus-user-ssl.conf",
				"chmod 644 /etc/grid-security/certificates/*"};
    String reqhostcertcommandsArr[] = {"cd /etc/grid-security",
					"export GLOBUS_LOCATION=<GLOBUS_LOCATION>",
					"echo `hostname` | xargs <GLOBUS_LOCATION>/bin/grid-cert-request -force -host"};
    String installhostcertcommandsArr[] = {"mv -f /tmp/hostcert.pem /etc/grid-security/",
						"chmod 644 /etc/grid-security/hostcert.pem"};
    String containercertcommandsArr[] = {"cp -f /etc/grid-security/hostcert.pem /etc/grid-security/containercert.pem",
						"cp -f /etc/grid-security/hostkey.pem /etc/grid-security/containerkey.pem",
						"chmod 644 /etc/grid-security/containercert.pem",
						"chmod 400 /etc/grid-security/containerkey.pem",
						"chown <OWNER>:<OWNER> /etc/grid-security/container*.pem"};
    String gridmapEntry = "echo <MAPENTRY> >> /etc/grid-security/grid-mapfile";
    String updatemapfilecommandArr[] = new String[50];

    boolean hostReq = false;
    String hostNameHostReq = null;
	String host = request.getParameter("hostsList");
	
	

    if( null != actionTitle && null != engine ){
	// generate smartfrog command
	
	// get hosts
	String []hosts = host.split(",");//request.getParameterValues("selectedHosts2");
	
	// get attribute map from GUI, to overwrite 
	java.util.Map attrMap = new java.util.HashMap();

	java.util.Enumeration params = request.getParameterNames();
	while(params.hasMoreElements()){
	    String p = (String)params.nextElement();
	    String keyStr = "action.argument.name" ;
	    if( p.startsWith(keyStr) ){
		String valKey = "action.argument.value" +
			 p.substring(keyStr.length(), p.length());

		String attrName = request.getParameter(p);
		String attrValue = request.getParameter(valKey);
		
		Vector attrVec = null;
		boolean vec = false;
		if ((attrValue.startsWith("[")) && (attrValue.endsWith("]"))) {
			attrVec = new Vector();
			String v = attrValue.substring(1,attrValue.length()-1);
			String arr[] = v.split(",");
			for (int i=0; i<arr.length; i++) {
				attrVec.addElement(arr[i]);
			}
			vec = true;
		}
		if (attrName.equals("sfConfig:security:caHashkey")) {
			attrVec = new Vector();
			for (int i=0; i<setupcacommandsArr.length;i++) {
				attrVec.add(i,setupcacommandsArr[i].replaceAll("<HASHKEY>",attrValue));	
			}
			attrName = "sfConfig:security:setupCAInfoCommands";
			vec = true;
		}
	
		if (attrName.equals("sfConfig:security:hostCertRequest")) {
			hostReq = true;
			//hostNameHostReq = new String(attrValue);
		}
		if (attrName.equals("sfConfig:security:containerCertOwner")) {
			attrVec = new Vector();
			for (int i=0;i<containercertcommandsArr.length;i++) {
				attrVec.add(i,containercertcommandsArr[i].replaceAll("<OWNER>",attrValue));
			}
			attrName = "sfConfig:security:containerCertCommands";
			vec = true;
		}
		if (attrName.equals("sfConfig:UpdateGridMapFileComp:mapEntries")) {
			Enumeration e = attrVec.elements();
			int idx = 0;
			vec = true;
			while (e.hasMoreElements()) {
				String entry = (String)e.nextElement();
				idx = entry.lastIndexOf(" ");
				String mapEntry = "\\\\\"" + entry.substring(0,idx) + "\\\\\" " + 
							entry.substring(idx+1,entry.length());
				System.out.println("GridMap Entry : " + mapEntry);
				String mEntry = gridmapEntry.replaceAll("<MAPENTRY>", mapEntry);
				System.out.println("GridMap Cmd : " + mEntry);

				idx = attrVec.indexOf(entry);
				attrVec.remove(idx);
				attrVec.add(idx, mEntry);
			}
			attrName = "sfConfig:UpdateGridMapFileComp:addEntryCmd";
		}

		if (vec) {
			attrMap.put(attrName, attrVec);
		}
		else {
			if (attrValue.startsWith("LAZY HOST")) {
				Reference ref = Reference.fromString(attrValue);
				attrMap.put(attrName, ref);
			}
			else {
				attrMap.put(attrName, attrValue);
			}
		}
		//System.out.println("AttrName : " + attrName + " = " + attrValue);
	    }
	}
        
	Vector attrVec = null;
	if (hostReq) {
		attrVec = new Vector();
		for (int i=0; i<reqhostcertcommandsArr.length; i++) {
			String globusLoc = (String)attrMap.get("sfConfig:security:globusLocation");
			String cmd = reqhostcertcommandsArr[i].replaceAll("<GLOBUS_LOCATION>",globusLoc);
			//String cmdFinal = cmd.replaceAll("<HOSTNAME>",hostNameHostReq);
			attrVec.add(i,cmd);
		}
		String attrName = "sfConfig:security:reqHostCertCommands";
		attrMap.put(attrName, attrVec);

		attrVec = new Vector();
		for (int i=0;i<installhostcertcommandsArr.length;i++) {
			attrVec.add(i,installhostcertcommandsArr[i]);
		}
		attrName = "sfConfig:security:installHostCertCommands";
		attrMap.put(attrName, attrVec);
	}

	// add name of local server 
	String avalancheServer = request.getServerName();
	int  avalanchePort = request.getServerPort();
	attrMap.put(SFAdapter.AVALANCHE_SERVER, avalancheServer + ":" 
		+ avalanchePort);
	
	boolean submitStatus = true;
	
		try{
			String instanceName = actionTitle + "test";
			adapter.submitTOScheduler(moduleId, version, instanceName, actionTitle,
				 attrMap, hosts, type,date,repeatcount,jobname,groupName);//, new Integer(number).intValue());
			//Map retCodes = adapter.submit(moduleId, version, instanceName, actionTitle,
		//	     attrMap, hosts);
		
			submitStatus = true ;
			
		}catch(Exception t){ 
			submitStatus = false ;
			t.printStackTrace();
			session.setAttribute("message", "Sumbit failed : " +
				 t.getMessage());
		}

	
	// if ! submitStatus .. failed on the server itself. 
	
	if( !submitStatus ){
		// back to submit page 
		javax.servlet.RequestDispatcher dispatcher =
		    request.getRequestDispatcher("host_select.jsp?moduleId="
			+ moduleId + "&&version=" + version + "&&distroId=" 
			+ distroId + "&&action=" + actionTitle);
		
		dispatcher.forward(request, response);
	}else{
	    javax.servlet.RequestDispatcher dispatcher =
	    		request.getRequestDispatcher("log_view.jsp");
	    dispatcher.forward(request, response);
	}
    }
%>
