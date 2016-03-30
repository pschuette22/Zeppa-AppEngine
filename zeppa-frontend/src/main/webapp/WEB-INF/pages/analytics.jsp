<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
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
		createGraphs();
	});
	
	// Override default Chart.js options here
	var options = {
		responsive: true,
		animationEasing: "easeOutQuart"
	};
	
	function createGraphs() {
		// Get the context of the canvas element we want to select
		var ctx1 = document.getElementById("event1").getContext("2d");
		var ctx2 = document.getElementById("event2").getContext("2d");
		var ctx3 = document.getElementById("dayOfWeek").getContext("2d");
		//var ctx3 = document.getElementById("event3").getContext("2d");
		// ${genderData} accesses gender data attribute set by the Analytics Servlet
		var chart1 = new Chart(ctx1).Doughnut(${genderData}, options);
		var chart2 = new Chart(ctx2).Bar(${ageData}, options);
		var chart3 = new Chart(ctx3).Bar(${popDays}, options);
		//var chart3 = new Chart(ctx3).Doughnut(${genderData}, options);
	}
	
</script>

<style>
	#tabNavBar{
 		width:100%;
 	}
 	.tabNav{
 		color: rgb(31,169,255);
 		width: 25%;
 		display:inline-block;
     	border-top-right-radius: 1em;
     	border-top-left-radius: 1em;
     	text-align:center;
 	}
 	.tabNav.active{
 		background-color: rgb(31,169,255);
 		color:#FFF
 	}
 	.tabNav:hover{
 		background-color: rgb(31,169,255);
 		color:#FFF;
 		cursor:pointer;
 	}
 	.analyticsTab{
 		display:none;
 	}
 	.analyticsTab.active{
 		display:block;
 	}
 	
	.container {
		overflow: auto;
	}	
	.column-left { 
		float: left; 
		width: 33%;
		padding: 25px;
	}
	.column-right { 
		float: right; 
		width: 33%;
		padding: 25px;
	}
	.column-center { 
		display: inline-block; 
		width: 33%; 
		padding: 25px;
	}
	.event-desc {
		text-align: center;
		padding: 10px;
	}
</style>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Analytics</h2>
	</jsp:attribute>
	
	<jsp:body>
		<!-- <div id="filters">Filters</div> -->
 		<div id="tabNavBar">
 			<!-- These comments are required to fix html bug which moves tab down to next line -->
 			<span class="tabNav active" data-tab="demographicsTab">Demographics</span><!--
 			--><span class="tabNav" data-tab="popularDaysTab">Popular Days</span><!--
 			--><span class="tabNav" data-tab="popularEventsTab">Popular Events</span><!--
 			--><span class="tabNav" data-tab="tagsTab">Tags</span>
 		</div>
 		
 		<div class="analyticsTab active" id="demographicsTab">
 			<p><h3>Event Demographics Data</h3></p>
	
			<div class="container">
				<div class="column-left">
					<canvas id="event1" width="150" height="150"></canvas>
					<div class="event-desc">Total Attendee Gender Breakdown</div>
				</div>
				<div class="column-center">
					<canvas id="event2" width="150" height="150"></canvas>
					<div class="event-desc">Total Attendee Age Breakdown</div>
				</div>
<!-- 				<div class="column-right"> -->
<!-- 					<canvas id="event3" width="150" height="150"></canvas> -->
<!-- 					<div class="event-desc">Event Name 3</div> -->
<!-- 				</div> -->
			</div>
 		</div>
 		
 		<div class="analyticsTab" id="popularDaysTab">popularDaysTab
 			<p><h3>Event Day-of-Week Data</h3></p>
 			<div class="container">
 				<canvas id="dayOfWeek" width="400" height="400"></canvas>
				<div class="event-desc">Total Attendee Day-of-Week Breakdown</div>
 			</div>
 		</div>
 		<div class="analyticsTab" id="popularEventsTab">popularEventsTab</div>
 		<div class="analyticsTab" id="tagsTab">tagsTab</div>
		
	</jsp:body>
</t:ZeppaBase>