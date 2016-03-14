<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->
<script src="../js/jquery-2.1.4.min.js"></script>
<script src="../js/Chart.js/Chart.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".tabNav").click(function(){
			//Styling stuff (changing colors and hiding/showing)
			$(".analyticsTab").removeClass("active");
			var tabID = $(this).data("tab");
			$("#"+tabID).addClass("active");
			$(".tabNav").removeClass("active");
			$(this).addClass("active");
		});
	});
	
	$(document).ready(function(){
		createGraph();
	});
	
	var options = {
		responsive: true
	};
	
	function createGraph() {
		// Get the context of the canvas element we want to select
		var ctx = document.getElementById("fireGraph").getContext("2d");
		// ${data} accesses data attribute set by the Analytics Servlet
		var myNewChart = new Chart(ctx).Doughnut(${data}, options);
	}
	
</script>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Analytics</h2>
	</jsp:attribute>
	
	<jsp:body>
		<p><h3>"Fire" graph</h3></p>
	
		<div>
			<canvas id="fireGraph" width="150" height="150"></canvas>
		</div>
		
		<div id="filters">Filters</div>
		<div id="tabNavBar"></div>
		<div class="analticsTab" id="demographicsTab">click here</div>
		<div class="analticsTab" id="popularDaysTab"></div>
		<div class="analticsTab" id="popularEventsTab"></div>
	</jsp:body>
</t:ZeppaBase>