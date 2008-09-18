<% /**
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
*/ %>
<%@ page language="java" %>
<%@ page import="java.io.*"%>
<%@ page import="org.smartfrog.avalanche.util.*"%>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*"%>
<%@ include file="header.inc.jsp" %>

<%
	String myURI = request.getRequestURI() + "?" + request.getQueryString();
  	String errMsg = null; 
  	String fileName = request.getParameter("fileName");
  	String host = request.getParameter("host");
  	String reportPath = request.getParameter("reportPath");
		
  	// use only if startLine is null;
  	String maxLinesStr = request.getParameter("maxLines");
	SFAdapter adapter = new SFAdapter(factory);
	String sysLogs = request.getParameter("sysLogs");
	if(sysLogs !=null)
		adapter.getHostReport(host, fileName, reportPath,true);
	else
		adapter.getHostReport(host, fileName, reportPath,false);
	String homeDir = factory.getAvalancheHome();
        String logsDir = homeDir + File.separator + "logs";
	String filePath = logsDir + File.separator + fileName;
	if(reportPath == null) {	
		File file = null;
		if(null != sysLogs){
			file = new File(filePath+".out.out");
		}
		else{
			file = new File(filePath+".out");
		}
	long fileSize = 0;
	if( !file.exists() ){
		if(null != sysLogs){
			errMsg = "System logs file doesn't exist " ;
		} else {
			errMsg = "Log file doesn't exist " ;
		}
		
	}else if( !file.canRead() ){
		errMsg = " No read permission for log file "  ;
	}else{
		fileSize = file.length();
	}
	
	
%>
<form id='readerform' name='readerform' method='post'>
<script language="JavaScript" type="text/javascript">

    function submit(formId, target) {
        var form = document.getElementById(formId);
        form.action = target;
        form.submit();
    }
	function sub(target) {
        document.readerform.action = target;
        document.readerform.submit();
    }

    setNextSubtitle("Log Reader Page");
    oneVoiceWritePageMenu("LogReader", "",
           "Refresh","javascript:sub('<%= myURI%>')",
		"Whole File (<%=fileSize %> bytes)", "javascript:sub('log_reader.jsp?fileName=<%=fileName%>&host=<%=host%>&readAll')",
		"Max 200 lines", "javascript:sub('log_reader.jsp?fileName=<%=fileName%>&host=<%=host%>&maxLines=200')",
		"System Logs", "javascript:sub('log_reader.jsp?fileName=<%=fileName%>&host=<%=host%>&sysLogs')"
 );
</script>
<%
	if( null != errMsg ){
%>

<font color="red" size=5>
	<%=errMsg%>
</font><br>
<%
} else { 
%>
	
<%
	
	String readAll = request.getParameter("readAll");
	StringBuffer buf = null;
	
	if( null != readAll) {
		buf = DiskUtils.readFile(file);
		
	}else if(null != sysLogs){
			buf = DiskUtils.readFile(file);
	}else {	
		int maxLines = 1000; // default value
		
		if( maxLinesStr != null ){
			try{
				maxLines = Integer.parseInt(maxLinesStr);
			}catch(Exception e){
				// nothing
			}
		}
		
		buf = DiskUtils.tail(file, maxLines);
	} 
%>
<br/>

<textarea readonly cols="auto" rows="10" style="width:100%;height:100%"><%=buf.toString()%></textarea>
<%}%>
<%
}else {
	String reportFile = filePath + File.separator + "index.html";
%>
<h2><a href=<%=reportFile%> TARGET="_blank"> Open Report</a></h2>
<%
	}
%>
</form>
<%@ include file="footer.inc.jsp" %>