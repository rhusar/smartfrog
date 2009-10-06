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
		 alert('Response received from server:\n'+http_request.responseText);
		 // result = http_request.responseText;
			 // Turn < and > into &lt; and &gt; (case matters)
			 //	    result = result.replace(/\<([^!])/g, '&lt;$1');
	//	    result = result.replace(/([^-])\>/g, '$1&gt;');			
	    	  //  document.getElementById('serverresponse').innerHTML = result;    
		 var xmlDoc = http_request.responseXML.documentElement;
		 var XMLpart = http_request.responseXML.getElementsByTagName("resource")[0]; 
	//	 alert("There are" + XMLpart.length + "elements");
		 //Update the HTML
		 updateHTML(XMLpart); 

		    //document.getElementById('serverresponse').innerHTML = XMLPart;    
		    //document.write("Ritu" + XMLpart);
		    //  doRedirect(result);
         } else {
            alert('There was a problem with the request.' +http_request.responseText +' '+http_request.status);
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
        //The node valuse will give actual data
	    var nodes=profileXML.getElementsByTagName("subresource");
//	alert(nodes.length);
	    var profileText = profileXML.childNodes[0].nodeValue;
//	var attr = profileXML.getAttribute("name");
//	alert(attr);
	//	alert(profileXML.childNodes[0].getAttribute("name"));	
//	//alert(profileXML.childNodes[1].nodeName);
		var out = "<html> <table border='0' cellpadding='0' cellspacing='0' class='resourceTable' id='resourceTable' width='100%'>";
//		out = out + "<caption bgcolor='darkblue'><h2><center>Resources for <a href=\"javascript:document.write('length=='" + len+ ")\">" +  profileXML.getAttribute("name") + "</a> " + profileXML.getAttribute("type") + "</center></h2></caption></table>";
out = out + "<caption bgcolor='darkblue'><h2><center>Resources for <a href=\"javascript:getTheForm('allHosts','<%=avalancheServer%>','<%=avalanchePort%>','<%=remoteLoadServer%>','" + profileXML.getAttribute("href") + "')\">" +  profileXML.getAttribute("name") + "</a> " + profileXML.getAttribute("type") + "</center></h2></caption></table>";
//	out = out + "<caption bgcolor='darkblue'><h2><center>Resources for <a href=" + profileXML.getAttribute("href") + ">" +  profileXML.getAttribute("name") + "</a> " + profileXML.getAttribute("type") + "</center></h2></caption></table>";
        out = out + " <table align='center' border='0' cellpadding='0' cellspacing='0' class='subresourceTable' id='subresourceTable' width='80%'>";
	
//alert("Out==" + out);	
	if (nodes.length > 0) {
		out = out + "<tr class='captionRow' bgcolor='lightblue'>  <td width='15%'>Resource Name</td>  <td width='15%'>Type</td> <td>Class</td> <td>Link</td> </tr>";
	    for (i=0; i< nodes.length; i++)
	    { 
		    //alert("<br>"+nodes.item(i).getAttribute("href")+"<br>");
	    
	    	out = out + "<tr bgcolor='lightyellow'>";
	    	out = out + "<td><a href=\"javascript:getTheForm('allHosts','<%=avalancheServer%>','<%=avalanchePort%>','<%=remoteLoadServer%>', '" +nodes.item(i).getAttribute("href")+ "')\">"  + nodes.item(i).getAttribute("name") + "</a></td>";
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
	    	out = out + "<td>" + profileXML.getAttribute("name") + "</a></td>";
	    	out = out + "<td>"+  profileXML.getAttribute("type"); + "</td>";
	    	out = out + "<td>" + profileXML.getAttribute("class"); + "</td>";
	    	out = out + "<td>" + profileXML.getAttribute("href"); + "</td>";
	    	out = out + "<td>" +  profileXML.childNodes[0].nodeValue + "</td>";
		out = out + "</tr>";
		out = out + "</table></html>";
	}

//Create the Text Node with the data received
      //  var profileBody = document.createTextNode(profileText);
      
	
    //	var profileBody = "<a href="+nodes.item(i).getAttribute("href")+">test</a>";              
	    //document.getElementById('serverresponse').innerHTML = profileBody;   
	    document.getElementById('serverresponse').innerHTML = out;   
	    //Get the reference of the DIV in the HTML DOM by passing the ID
/*	var profileSection = document.getElementById("serverresponse");
           
        //Check if the TextNode already exist
        if(profileSection.childNodes[0])
        {
            //If yes then replace the existing node with the new one
            profileSection.replaceChild(profileBody, profileSection.childNodes[0]);
        }
        else
        {
            //If not then append the new Text node
            profileSection.appendChild(profileBody);
        }*/       
    } 


  function doRedirect(server)
  {
	//  var src = document.getElementById(srcId);
	 // alert("In redirect " + server);
	 window.location.replace( "host_manage.jsp?server=" + server);
  }

   function postTheForm() {
      var poststr = document.myform.xmldata.value ;
	   alert('Sending XML to server:\n'+poststr);
      makePOSTRequest('POST',document.myform.endpointURL.value , poststr);
   }
   
   function getTheForm(srcId, server, port, servername, test) {
	   var src = document.getElementById(srcId);
	 //  alert("url=="+ src);
	  alert("server=="+ server);
	  alert("test=="+ test);
	 // doRedirect(server);
	   for (var i = 0; i < src.options.length; i++){
			if (src.options[i].selected){
				alert("URL=="+ src.options[i].text);
				//var url = "http://" + server + ":" + port + "/" + servername + "/rest/"+ src.options[i].text + "/3800";
				var url = "http://" + server + ":" + port + "/" + servername + "/rest/"+ "localhost" + "/3800/sfDefault";
			}
	   }
	   if (test)
		   url = test;
      var getStr = encodeURI(document.myform.xmldata.value) ;
	   alert('Sending XML to server:\n'+getStr);
      //makePOSTRequest('GET',document.myform.endpointURL.value , getStr);
      makePOSTRequest('GET',url , getStr);
   }
   
   function putTheForm() {
      var poststr = document.myform.xmldata.value ;
	   alert('Sending XML to server:\n'+poststr);
      makePOSTRequest('PUT',document.myform.endpointURL.value , poststr);
   }
   
   function deleteTheForm() {
      var getStr = encodeURI(document.myform.xmldata.value) ;
	   alert('Sending XML to server:\n'+getStr);
      makePOSTRequest('DELETE',document.myform.endpointURL.value , getStr);
   }
    -->
</script>






<form action="javascript:get(document.getElementById('myform'));" name="myform" id="myform">
<br><br><br></br></br></br>  
<table width="100%"  border="0" cellpadding="0" cellspacing="0">
 
    <tr>
    	<td><b>Select host to manage</b></td>
    	<td colspan="3">
		 <select name="allHosts">
	<%
		for( int i=0;i<hosts.length;i++){
	%>
		<option<%=((host != null) && host.equals(hosts[i]))?" selected":""%>><%=hosts[i]%></option>
	<% 
		}
	%>
      		</select>
      </td>
    </tr>
  </table>
<br><br><br></br></br></br> 


   <table> <tr>
      <td><input type="button" name="selectbutton" value="SELECT HOST TO MANAGE" onclick="doRedirect(document.getElementById('allHosts').options[ document.getElementById('allHosts').selectedIndex].text)"></td>
    </tr>
  </table>
  </form>

<%@ include file="footer.inc.jsp" %>

