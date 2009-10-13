<%@ include file="/html/mombasa-portlet/init.jsp" %>
<tiles:useAttribute id="content" name="content" classname="java.lang.String" ignore="true"/>

<tiles:useAttribute id="title" name="title" classname="java.lang.String" ignore="true"/>

<h2><%= title %>
</h2>
<logic:messagesPresent>
  <span class="portlet-msg-error">
  <html:errors/>
  </span>
</logic:messagesPresent>

<div>
<jsp:include page='<%= content %>' flush="true"/>
</div>


<div>
<jsp:include page="/html/mombasa-portlet/cluster/cluster_nav.jsp" flush="true"/>
</div>
