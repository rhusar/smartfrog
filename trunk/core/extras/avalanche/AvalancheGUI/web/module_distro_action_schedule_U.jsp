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
<%@ page language="java" contentType="text/html" %>
<%@ include file="header.inc.jsp"%>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*"%>
<%@ page import="org.smartfrog.sfcore.reference.*"%>
<%@ page import="java.util.*"%>
<%! String []hosts ; %>
<%
    
    String moduleId = request.getParameter("moduleId");
    String version = request.getParameter("version");
    
    String actionTitle =  request.getParameter("title");
    System.out.println("**********************Scheduler TEST : " + moduleId + " " + version + " " + actionTitle); 
    String engine = request.getParameter("engine");
    String distroId = request.getParameter("distroId");
    String number = request.getParameter("number");
	hosts = request.getParameterValues("selectedHosts2");
	String str = "";
	for (int i = 0; i< hosts.length;i++){
		if(str.equals(""))
			str = str + hosts[i];
		else
			str = str + ","+hosts[i];
	}
%>
 <script language="javascript" type="text/javascript" src="onevoice/js/datetimepicker.js">
 </script>
<script language="JavaScript" type="text/javascript">

function diableType(){
	document.scheduleJob.sch.value = 7;
	document.scheduleJob.sch.disabled = true;
}
function enableType(){
	document.scheduleJob.sch.value = 1;
	document.scheduleJob.sch.disabled = false;
}
function dateSelect(){
	if(document.scheduleJob.sch.value== '7'  ){
		alert("Click Calendar Icon to select date");
		
	}
	else{ 
		alert("Select Starting date to start schedule");
		
	}
}

function selectAll(src)
{
    if ( src.options.length == 0 ){
	alert("You must select one or more target nodes before " +
		"executing this action.");
	return false;
    }
    for (var i = 0; i < src.options.length; i++){
	    src.options[i].selected = true ;
    }
	//alert(document.getElementById('selectedHosts2').length);
    return true;
}


function validate(){
	if(document.scheduleJob.jobName.value == ""){
		alert("Enter a job name");
		return;
	}
	if(document.scheduleJob.groupName.value == ""){
		alert("Enter a group name");
		return;
	}
	if(document.scheduleJob.date.value == ""){
		alert("Enter Date to schedule");
		return;
	}
	var dateString = document.scheduleJob.date.value;
	var exp = formDateString(dateString,document.scheduleJob.sch.value);
	document.scheduleJob.date1.value = exp;
	document.scheduleJob.submit();
		
}
function formDateString(dateString,schValue){
	//alert(schValue);
	var expression = "";
	var day = dateString.charAt(0)+dateString.charAt(1);
	var mon = dateString.charAt(3)+dateString.charAt(4)+dateString.charAt(5);
	var year = dateString.charAt(7)+dateString.charAt(8)+dateString.charAt(9)+dateString.charAt(10);
	var hh= dateString.charAt(12)+dateString.charAt(13);
	var mm= dateString.charAt(15)+dateString.charAt(16);
	var ss= dateString.charAt(18)+dateString.charAt(19);
	if(schValue == 7  ){
		return dateString
	}
	else{ 
	
		if(schValue== '1'){
			//"0 0 12 * * ?"
			expression = "0"+" "+mm+" "+hh+" * * ?";
		}
		if(schValue== '2'){
			//0 15 10 ? * MON-FRI
			expression = ss+" "+mm+" "+hh+" "+"? * MON-FRI";
		}
		if(schValue== '3'){
			expression = ss+" "+mm+" "+hh+" "+day+"/7 * ?";
			
		}
		if(schValue== '4'){
			//0 15 10 15 * ?"
			expression = ss+" "+mm+" "+hh+" "+day+" * ?";
		}
		alert(expression);
		return expression;
	}
	


	/*if(dateString.length < 20  ){
		alert("Invalid date format. Format should be dd-Mon-yyyy hh:mm:ss");
	} else{if((dateString.charAt(2) != '-')|| (dateString.charAt(6) != '-') || (dateString.charAt(11) != ' ' )|| (dateString.charAt(14) != ':') ||(dateString.charAt(17) != ':') )
		alert("Invalid date format. Format should be dd-Mon-yyyy hh:mm:ss");
	}

	var day = dateString.charAt(0)+dateString.charAt(1);
		var mon = dateString.charAt(3)+dateString.charAt(4)+dateString.charAt(5);
		var year = dateString.charAt(7)+dateString.charAt(8)+dateString.charAt(9)+dateString.charAt(10);
		var hh= dateString.charAt(12)+dateString.charAt(13);
		var mm= dateString.charAt(15)+dateString.charAt(16);
		var ss= dateString.charAt(18)+dateString.charAt(19);

		alert(day );
	
	 var days = new Array('01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30','31');
	 var months = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
	 var years = new Array('0','1','2','3','4','5','6','7','8','9');
	 var hours = new Array('00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24');
	 var mins = new Array('00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','27','28','29','30','31','32','33','34','35','36','37','38','39','40','41','42','43','44','45','46','47','48','49','50','51','52','53','54','55','56','57','58','59','60');
	 var found = false;
	 for (int i = 0;i<days.lenght;i++ )
	 {	if(day == days[i]){
				found = true;
				break;
			}
	 }
	 if(found == false){
		 alert("Invalid Day entered.");
		 return;
	 }else{
		 found = false;
	 }

	 for (int i = 0;i<months.lenght;i++ )
	 {		if(mon == months[i]){
				found = true;
				break;
			}
	 }
	 if(found == false){
		 alert("Invalid month entered.");
		 return;
	 }else{
		 found = false;
	 }
	 for (int i = 0;i<hours.lenght;i++ )
	 {		if(hh == hours[i]){
				found = true;
				break;
			}
	 }
	 if(found == false){
		 alert("Invalid hour entered.");
		 return;
	 }else{
		 found = false;
	 }
	 for (int i = 0;i<mins.lenght;i++ )
	 {		if(mm == mins[i]){
				found = true;
				break;
			}
	 }
	 if(found == false){
		 alert("Invalid minutes entered.");
		 return;
	 }else{
		 found = false;
	 }
	 for (int i = 0;i<mins.lenght;i++ )
	 {		if(ss == mins[i]){
				found = true;
				break;
			}
	 }
	 if(found == false){
		 alert("Invalid seconds entered.");
		 return;
	 }else{
		 found = false;
	 }
	 int z = 0;
	 for (int i = 0;i<year.lenght;i++ )
	 {	for(int j=0;j<years.length;j++)	{
		 if(year[i] == years[j]){
				z= z+1;
				break;
			}
		}
	 }
	 if(z != year.lenght){
		 alert("Invalid year entered.");
		 return;
	 }else{
		 found = false;
	 }*/
	
}

</script>

<form method="post" name = "scheduleJob" id="scheduleJob"
	action="module_distro_action_schedule.jsp?title=<%=actionTitle%>&&engine=<%=engine%>&&moduleId=<%=moduleId%>&&version=<%=version%>&&distroId=<%=distroId%>&&hostsList=<%=str%>"
	>

 <div align="center">
<center>    
<br/>
<br/>
<table id="hostListTable" class="dataTable" 
    style="width: 400px; border-collapse: collapse;">
    <caption>Schedule Details:</caption>
    <tbody>
	
	<tr>
		<td class="medium" align="right">Job Name : </td>
	    <td class="medium"><input type="text" name="jobName" size="25" id="jobName" /></td>

	</tr>  	
	<tr>
		<td class="medium" align="right">Group Name : </td>
	    <td class="medium"><input type="text" name="groupName" size="25" id="groupName" /></td>

	</tr>  
	<tr>
		<td class="medium" align="right">Type : </td>
		<td class="medium">
	    <input type="radio" name="type" value="Job" checked onclick = "javascript:diableType()"> Job
			<br>
		<input type="radio" name="type" value="Trigger" onclick = "javascript:enableType()"> Trigger </td>


	</tr>
	<tr>
		<td class="medium" align="right">Type of schedule : </td>
	    <td class="medium"><select name="sch" onChange="dateSelect()">
							<option value="7">Only once</option>
							<option value="1">Daily</option>
							<option value="2">Monday to Friday</option>
							<option value="3">Once in a week</option>
							<option value="4">Once a month</option>							
						  </select> </td>

	</tr>
	<tr>
		<td class="medium" align="right">Schedule Date and Time : </td>

	    <td class="medium"><input type="Text" name="date" id="date" maxlength="25" size="25" readonly='true'><a href="javascript:NewCal('date','ddmmmyyyy',true,24);"> <img src="onevoice/images/cal.gif" width="14" height="14" border="0" alt="Pick a date"></a></td>

	</tr>  
	<tr>
		<td class="medium" align="right">Repeat times on failure </td>
		<td><select name="repeat" >
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>	
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							
						</select></td>
	</tr>
	<select style="display:none" size="0" id='selectedHosts2' 
		name="selectedHosts2" 
		multiple>
    </select>
	<input style="display:none" name="date1" />
    </tbody>
</table>


 <div class="bWrapperUp" style="margin-top:10px;"><div><div>
    <input class="hpButton"type="button" name="button" 
	value="Schedule" onclick = "validate()" ></input></div></div>
  </div>
  <br/>
   <br/>
</div>
</center>

	<script language="JavaScript" type="text/javascript">

 document.scheduleJob.sch.disabled = true;
 
 
</script>
</form>



<%@ include file="footer.inc.jsp" %>
