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
		// Responsive is commented out because it prevents graphs from showing correctly when tab is changed
		//responsive: true,
		animationEasing: "easeOutQuart"
	};
	
	function createGraphs() {
		// Get the context of the canvas element we want to select
		var genderContext = document.getElementById("gender").getContext("2d");
		var ageContext = document.getElementById("age").getContext("2d");
		var daysContext = document.getElementById("dayOfWeek").getContext("2d");
		var eventsContext = document.getElementById("popularEvents").getContext("2d");
		var tagsContext = document.getElementById("popularTags").getContext("2d");
		var tagsWatchedContext = document.getElementById("watchedTags").getContext("2d");
		if(${genderData} != "none") {
			var genderChart = new Chart(genderContext).Doughnut(${genderData}, options);
		} else {
			genderContext.font = "12px Arial";
			genderContext.fillText("There is no event data for this vendor", 10,50);
		}
		if(${ageData}.labels.length > 0) {
			var ageChart = new Chart(ageContext).Bar(${ageData}, options);
		} else {
			ageContext.font = "12px Arial";
			ageContext.fillText("There is no event data for this vendor", 10,50);
		}
		if(${popDays}.labels.length > 0) {
			var popDaysChart = new Chart(daysContext).Bar(${popDays}, options);
		} else {
			daysContext.font = "12px Arial";
			daysContext.fillText("There are no events for this vendor", 10,50);
		}
		if(${popEvents}.labels.length > 0) {
			var popEventsChart = new Chart(eventsContext).Bar(${popEvents}, options);
		} else {
			eventsContext.font = "12px Arial";
			eventsContext.fillText("There are no events for this vendor", 10,50);
		}
		if(${tagData}.labels.length > 0) {
			var tagChart = new Chart(tagsContext).Bar(${tagData}, options);
		} else {
			tagsContext.font = "12px Arial";
			tagsContext.fillText("There are no common tags", 10,50);
		}
		if(${watchedTagData}.labels.length > 0) {
			var watchedChart = new Chart(tagsWatchedContext).Bar(${watchedTagData}, options);
		} else {
			tagsWatchedContext.font = "12px Arial";
			tagsWatchedContext.fillText("No users have watched an event for this vendor",10,50);
		}
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
					<canvas id="gender" width="300" height="300"></canvas>
					<div class="event-desc">Total Attendee Gender Breakdown</div>
				</div>
				<div class="column-center">
					<canvas id="age" width="300" height="300"></canvas>
					<div class="event-desc">Total Attendee Age Breakdown</div>
				</div>
			</div>
 		</div>
 		
 		<div class="analyticsTab" id="popularDaysTab">
 			<p><h3>Event Day-of-Week Data</h3></p>
 			<div class="container">
 				<div class="column-center">
 					<canvas id="dayOfWeek" width="300" height="300"></canvas>
					<div class="event-desc">Total Attendee Day-of-Week Breakdown</div>
				</div>
 			</div>
 		</div>
 		<div class="analyticsTab" id="popularEventsTab">
 			<p><h3>Popular Event Data</h3></p>
 			<div class="container">
 				<div class="column-center">
 					<canvas id="popularEvents" width="300" height="300"></canvas>
					<div class="event-desc">Most popular events</div>
				</div>
 			</div>
 		</div>
 		<div class="analyticsTab" id="tagsTab">
 			<p><h3>Popular Tags Data</h3></p>
 			<div class="container">
 				<div class="column-left">
	 				<canvas id="popularTags" width="300" height="300"></canvas>
					<div class="event-desc">Popular Tags Among People who Joined Events</div>
				</div>
				<div class="column-center">
					<canvas id="watchedTags" width="300" height="300"></canvas>
					<div class="event-desc">Popular Tags Among People who Watched Events</div>
				</div>
 			</div>
 		</div>
		
	</jsp:body>
</t:ZeppaBase>