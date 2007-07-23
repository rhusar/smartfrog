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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="org.smartfrog.avalanche.server.engines.sf.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>
<%@	page import="java.util.*"%>

<%@ include file="InitBeans.jsp" %>

<%

	String configFile = request.getParameter("configURL");

  	String errMsg = null; 
  	SystemSettings sett = avalancheFactory.getSettings();

  	if( null == sett ){
  		errMsg = "Error connecting to settings database" ;
  		throw new Exception ( "Error connecting to settings database" );
  	}
  	
  	SfConfigsType configs = sett.getSFConfigs();  
	SfDescriptionType desc = null;
	SfDescriptionType[] descs = configs.getSfDescriptionArray();
	for( int i=0;i<descs.length;i++){
		if( descs[i].getUrl().equals(configFile) ){
			desc = descs[i] ;
			break;
		}
	}

	String pageAction = request.getParameter("action");

	if( null != pageAction && pageAction.equals("set")){
		String []selectedArgs = request.getParameterValues("selectedArg");

  		java.util.Enumeration params = request.getParameterNames();

  		// delete old values from XML
		while(desc.sizeOfArgumentArray()>0){
			desc.removeArgument(0);
		}
		
  		while(params.hasMoreElements()){
  			String param = (String)params.nextElement();
  			String t = "argument.name.";
  			if( param.startsWith(t) ){
  				String argName = request.getParameter(param);
  				
  				String suf = param.substring(t.length(), param.length());
  				
  				String description= request.getParameter("argument.description." + suf );
  				String value = request.getParameter("argument.defaultValue." + suf);

				System.out.println("param : " + param + "value : " + argName);
				System.out.println("argument.description." + suf);
				System.out.println("argument.defaultValue." + suf);
  				
  				// set only if checked in the page
  				for( int k=0;k<selectedArgs.length;k++){
  	  				System.out.println("Selected Arg : "+selectedArgs[k]);
  	  				System.out.println("comparing Arg : "+argName);
  	  				
  	  				if( selectedArgs[k].equals(argName)){
  	    				SfDescriptionType.Argument arg = desc.addNewArgument();

  	  	  				System.out.println("Setting : " + argName);
  	  	  				arg.setName(argName);
  		  				arg.setValue(value);
  	  					arg.setDescription(description);
  	  				}
  				}
  			}
  		}
  		sett.setSfConfigs(configs);
		System.out.println("Setting : configs");
	}

	SfDescriptionType.Argument []args = desc.getArgumentArray();

	Vector v  = SFAdapter.getSFAttributes(configFile);
	Iterator itor = v.iterator();
%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="styles.csscss"/>
    <script type="text/javascript" src="utils.js"></script>
</head>

<body>
<script>
    setNextSubtitle("Set SmartFrog Attributes Page");
</script>

<center>

    <form method="post" action="SaveSFAction.jsp?pageAction=setActionArgs&&url=<%=configFile %>">
<table>
<tr>
	<th>Argument Name</th> <th>Description </th> <th>Default value</th> <th>Include</th>
</tr>
<%
	int i= 0;
	while(itor.hasNext()){

		String attrName = (String)itor.next();

		SfDescriptionType.Argument arg = null ;
		
		for( int j=0;j<args.length;j++){
			String argName = args[j].getName();
			if( null!= argName && argName.equals(attrName)){
				arg = args[j];
				break;
			}
		}
%>
<tr>
	<td>
		<input type="text" name="argument.name.<%=i%>" value="<%=attrName %>"></input>
	</td>
	<td>	
		<input type="text" name="argument.description.<%=i%>" value="<%=(null ==arg)?"": arg.getDescription() %>"></input>
	</td>
	<td>	
		<input type="text" name="argument.defaultValue.<%=i%>" value="<%=(null ==arg)?"": arg.getValue() %>"></input>
	</td>
	<td>	
<%
	if( null == arg) {
 %>			
		<input type="checkbox" name="selectedArg" value="<%=attrName%>"></input>
<%
	}else{
%>		
		<input type="checkbox" name="selectedArg" value="<%=attrName%>" checked></input>
<%
	}
%>
	</td>
	
</tr>		
	
<%
	i++;
	}
%>
</table>
<input type="submit" name="save" value="Save Changes"></input>
</form>
</center>
</body>
</html>
