<%-- /**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

For more information: www.smartfrog.org
*/ --%>

<%@ page language="java" %>
<%@ include file="header.inc.jsp" %>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.host.*"%>

<%
    String url = null;
    String host = null;
    HostManager hostManager = factory.getHostManager();
    String []hosts = hostManager.listHosts();
    String avalancheServer = request.getServerName();
    int  avalanchePort = request.getServerPort();
    String remoteLoadServer = factory.getAttribute(AvalancheFactory.AVALANCHE_SERVER_NAME);
   String server = request.getParameter("server");
%>
<script type="text/javascript" language="javascript">
 <!--
    setNextSubtitle("Host Management");
    
    
    var http_request = false;
    
    function makePOSTRequest(method,url, parameters) {
      http_request = false;
      if (window.XMLHttpRequest) { // Mozilla, Safari,...
         http_request = new XMLHttpRequest();
         if (http_request.overrideMimeType) {
         	// set type accordingly to anticipated content type
            http_request.overrideMimeType('text/xml');
            //http_request.overrideMimeType('text/html');
         }
      } else if (window.ActiveXObject) { // IE
         try {
            http_request = new ActiveXObject("Msxml2.XMLHTTP");
         } catch (e) {
            try {
               http_request = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {}
         }
      }
      if (!http_request) {
         alert('Cannot create XMLHTTP instance');
         return false;
      }
      
      http_request.onreadystatechange = alertContents;
     // http_request.open(method, url, true);
      if(method=='GET'){
			http_request.open(method, url+parameters, true);
			http_request.setRequestHeader("Content-type", "text/xml");
			http_request.setRequestHeader("Content-length", parameters.length);
			http_request.setRequestHeader("Connection", "close");
			http_request.send(null);
		}
	   if(method=='POST')  {
		   http_request.open(method, url, true);
			http_request.setRequestHeader("Content-type", "text/xml");
			http_request.setRequestHeader("Content-length", parameters.length);
			http_request.setRequestHeader("Connection", "close");
			http_request.send(parameters);
		  }
	   if(method=='PUT')  {
			http_request.open(method, url, true);
			http_request.setRequestHeader("Content-type", "text/xml");
			http_request.setRequestHeader("Content-length", parameters.length);
			http_request.setRequestHeader("Connection", "close");
			http_request.send(parameters);
		  }		
	  
      if(method=='DELETE'){
			http_request.open(method, url+parameters, true);
			http_request.setRequestHeader("Content-type", "text/xml");
			http_request.setRequestHeader("Content-length", parameters.length);
			http_request.setRequestHeader("Connection", "close");
			http_request.send(null);
		}		    
   }

   function alertContents() {
      if (http_request.readyState == 4) {
         if (http_request.status == 200 || http_request.status==201) {
		 //Update the HTML
		 updateHTML(http_request.responseXML);
         } else {
			document.getElementById('serverresponse').innerHTML = http_request.responseText;
         }
      }
   }

/**
    * This function parses the XML and updates the 
    * HTML DOM by creating a new text node is not present
    * or replacing the existing text node.
    */
    function updateHTML(profileXML)
    {
	    var resource = profileXML.getElementsByTagName("resource")[0];
	    var response =  profileXML.getElementsByTagName("response")[0];
       
	    //The node valuse will give actual data
	if (resource !=  null) {
	    var nodes=resource.getElementsByTagName("subresource");
	    var out = "<html> <table border='0' cellpadding='0' cellspacing='0' class='resourceTable' id='resourceTable' width='100%'>";
	    out = out + "<caption bgcolor='darkblue'><h2><center>Resources for <a href=\"javascript:getTheForm('" + resource.getAttribute("href") + "')\">" +  resource.getAttribute("name") + "</a> " + resource.getAttribute("type") + "</center></h2></caption></table>";

           out = out + " <table align='center' border='0' cellpadding='0' cellspacing='0' class='subresourceTable' id='subresourceTable' width='80%'>";
	
	 if (nodes.length > 0) {
		out = out + "<tr class='captionRow' bgcolor='lightblue'>  <td width='15%'>Resource Name</td>  <td width='15%'>Type</td> <td>Class</td> <td>Link</td> </tr>";
	    for (i=0; i< nodes.length; i++)
	    { 
	    
	    	out = out + "<tr bgcolor='lightyellow'>";
	    	out = out + "<td><a href=\"javascript:getTheForm('" +nodes.item(i).getAttribute("href")+ "')\">"  + nodes.item(i).getAttribute("name") + "</a></td>";
	    	//out = out + "<td><a href=" + nodes.item(i).getAttribute("href")  + ">" + nodes.item(i).getAttribute("name") + "</a></td>";
	    	out = out + "<td>"+ nodes.item(i).getAttribute("type") + "</td>";
	    	out = out + "<td>" + nodes.item(i).getAttribute("class") + "</td>";
	    	out = out + "<td>" + nodes.item(i).getAttribute("href") + "</td>";
		out = out + "</tr>";
	    }
	    	out = out + "</table></html>";
	} else {
			out = out + "<tr class='captionRow' bgcolor='lightblue'>  <td>Resource Name</td>  <td>Type</td> <td>Class</td> <td>Link</td> <td>Value</td>	</tr>";
		out = out + "<tr bgcolor='lightyellow'>";
	    	out = out + "<td>" + resource.getAttribute("name") + "</a></td>";
	    	out = out + "<td>"+   resource.getAttribute("type"); + "</td>";
	    	out = out + "<td>" +  resource.getAttribute("class"); + "</td>";
	    	out = out + "<td>" +  resource.getAttribute("href"); + "</td>";
	    	out = out + "<td>" +   resource.childNodes[0].nodeValue + "</td>";
		out = out + "</tr>";
		out = out + "</table></html>";
	}
	    document.getElementById('serverresponse').innerHTML = out; 
	} else if (response != null) {
		var code= response.getElementsByTagName("code")[0];
		var message=response.getElementsByTagName("message")[0];
		var out = "<html><p> <p> Status:  " + code.childNodes[0].nodeValue + "</p>"; 
		out = out + "Message:  " + message.childNodes[0].nodeValue + "</p>";
		out = out + "</html>";
		document.getElementById('serverresponse').innerHTML = out; 
	}	
      
    } 

   function postTheForm(url) {
      var poststr = document.myform.xmldata.value ;
	   alert('Sending XML to server:\n'+poststr);
      //makePOSTRequest('POST',document.myform.endpointURL.value , poststr);
      makePOSTRequest('POST',url , poststr);
   }

   function getTheForm(url) {
      var getStr = encodeURI(document.myform.xmldata.value) ;
	 //  alert('Sending XML to server:\n'+getStr);
      //makePOSTRequest('GET',document.myform.endpointURL.value , getStr);
      makePOSTRequest('GET',url , getStr);
   }
   
   function putTheForm(url) {
      var poststr = document.myform.xmldata.value ;
	   alert('Sending XML to server:\n'+poststr);
      //makePOSTRequest('PUT',document.myform.endpointURL.value , poststr);
      makePOSTRequest('PUT',url , poststr);
   }
   
   function deleteTheForm(url) {
      var getStr = encodeURI(document.myform.xmldata.value) ;
	 //  alert('Sending XML to server:\n'+getStr);
      //makePOSTRequest('DELETE',document.myform.endpointURL.value , getStr);
      makePOSTRequest('DELETE',url , getStr);
   }
    -->
</script>





<p>The form below can be used to send arbitary content to a URL using HTTP GET/POST/PUT/DELETE operations.<br>
  </p>
<form action="javascript:get(document.getElementById('myform'));" name="myform" id="myform">
  <table width="100%"  border="0" cellpadding="0" cellspacing="0">
    
    <tr>
      <td><b>Host</b> </td>
      <td colspan="3"><input name="host" type="text" value='<%=server%>' size="100" disabled="true"></td>
    </tr>
	<%
   if (server != null)
    url= "http://" + avalancheServer + ":" + avalanchePort + "/" + remoteLoadServer + "/rest/" + server + "/3800";
    else 
	url = null;    
    
    %>
    <tr>
      <td><b>Endpoint URL</b></td>
      <% if (url == null) {
	%>
      <td colspan="3"><input name="endpointURL" type="text" value="" size="100"></td>
      <%
      } else {
      %>
      <td colspan="3"><input name="endpointURL" type="text" value='<%=url%>' size="100"></td>
      <% }
      %>
    </tr>
    
    <tr>
      <td><b>XML to send</b> </td>
      <td colspan="3"><textarea name="xmldata" cols="100" rows="20"></textarea></td>
    </tr>
  </table>
   <table> <tr>
      <td><input type="button" name="postbutton" value="SENT Via POST" onclick="javascript:postTheForm(document.myform.endpointURL.value);"></td>
      <td><input type="button" name="getbutton" value="SEND via GET"  onclick="javascript:getTheForm(document.myform.endpointURL.value);">	  </td>
      <td><input type="button" name="putbutton" value="SEND Via PUT" onclick="javascript:putTheForm(document.myform.endpointURL.value);"></td>
      <td><input type="button" name="deletebutton" value="SEND Via DELETE" onclick="javascript:deleteTheForm(document.myform.endpointURL.value);"></td>
    </tr>
  </table>
</form>

<h3><br><br>
	
Server-Response:<br>
</h3>
<hr>
<span name="serverresponse" id="serverresponse"></span>

<hr>


<%@ include file="footer.inc.jsp" %>

