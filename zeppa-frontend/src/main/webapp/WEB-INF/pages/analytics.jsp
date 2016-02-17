<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->
<script src="../js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#demographicsTab").click(function(){
			alert("asfasF");
		});
	});
</script>
<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Analytics</h2>
	</jsp:attribute>
	<jsp:attribute name="footer">
	  <p id="copyright">Copyright 1927, Future Bits When There Be Bits Inc.</p>
	</jsp:attribute>
	
	
	
	<jsp:body>
		<div id="filters">Filters</div>
		<div id="tabNavBar"></div>
		<div class="analticsTab" id="demographicsTab">click here</div>
		<div class="analticsTab" id="popularDaysTab"></div>
		<div class="analticsTab" id="popularEventsTab"></div>
	</jsp:body>
</t:ZeppaBase>