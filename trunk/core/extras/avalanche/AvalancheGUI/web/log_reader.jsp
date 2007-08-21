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
<%@ include file="header.inc.jsp" %>
<%@	page import="java.io.*"%>
<%@	page import="org.smartfrog.avalanche.util.*"%>

<%
	String myURI = request.getRequestURI() + "?" + request.getQueryString();
  	String errMsg = null; 
  	String filePath = request.getParameter("filePath");
  	
  	// use only if startLine is null;
  	String maxLinesStr = request.getParameter("maxLines");

	File file = new File(filePath);
	if( !file.exists() ){
		errMsg = "Error! log file doesn't exist: " + filePath;
	}else if( !file.canRead() ){
		errMsg = "Error! No read permission for log file: " + filePath ;
	}
	
	long fileSize = file.length();
	String readAll = request.getParameter("readAll");
	StringBuffer buf = null;
	
	if( null != readAll) {
		buf = DiskUtils.readFile(file);
	}else{
	
		int maxLines = 100; // default value
		
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

<script language="JavaScript" type="text/javascript">

    function submit(formId, target) {
        var form = document.getElementById(formId);
        form.action = target;
        form.submit();
    }

    setNextSubtitle("Log Reader Page");
    oneVoiceWritePageMenu("LogReader", "footer",
            "Refresh", "<%= myURI%>",
            "Whole File (<%=fileSize %> bytes)", "log_reader.jsp?filePath=<%=filePath%>&readAll",
            "Max 200 lines", "log_reader.jsp?filePath=<%=filePath%>&maxLines=200"
     );
</script>

<br/>

<textarea readonly cols="auto" rows="auto" style="width:100%;height:90%"><%=buf.toString()%></textarea>


<%@ include file="footer.inc.jsp" %>
