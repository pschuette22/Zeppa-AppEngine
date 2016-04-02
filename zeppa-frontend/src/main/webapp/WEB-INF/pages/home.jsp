<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script src="../js/Chart.js/Chart.js"></script>
<script type="text/javascript">
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
		var ageContext = document.getElementById("age").getContext("2d");
		var tagsContext = document.getElementById("popularTags").getContext("2d");
		var ageChart = new Chart(ageContext).Bar(${ageData}, options);
		var tagChart = new Chart(tagsContext).Bar(${tagData}, options);
	}
</script>

<style>
  .boxHeader {
    float:left;
	width:45%;
	margin: 0% 1.66% 0% 1.66%;
	text-decoration: underline;
  }
  .square{
    float:left;
    position: relative;
    width: 45%;
	height: 350px;
    padding-bottom : 1%; /* = width for a 1:1 aspect ratio */
    margin: 0 1.66% 2% 1.66%;
    overflow:hidden;
	border: 1px solid black
  }
  .eventHead{
    width:23%;
	text-align:center;
	text-decoration: underline;
	padding-bottom: 10px;
	padding-top: 3px;
  }
  .eventCell{
    width:23%;
	text-align:center;
  }
  .tableRow{
    width:99%;
  }
  .billingHead{
    width:18%;
	text-align:center;
	text-decoration: underline;
	padding-bottom: 10px;
	padding-top: 3px;
  }
  .billingCell{
    width:18%;
	text-align:center;
  }
  .analyticsHead{
	    width:48%;
		text-align:center;
		text-decoration: underline;
		padding-bottom: 10px;
		padding-top: 3px;
  }
  .container {
		overflow: auto;
  }
  .column-center { 
		display: inline-block; 
		width: 15%; 
		padding: 25px;
  }
  .header-small {
		margin: 0 0 1em 0;
  }
  input[type="submit"] {
    padding: 0.5em 0.5em !important;
    font-size: 0.85em !important;
  }
  .section-head {
  	margin: 0 0 0 0;
  }
</style> 

<t:ZeppaBase>
  <jsp:attribute name="title">
	  <h2 class="header-small">Dashboard</h2>
  </jsp:attribute>  
  
  <jsp:body>
    <div id="dashboardContent" style="overflow:auto">
	    <div id="currentHeader" class="boxHeader"><h3 class="header-small">Current Events</h3></div>
		<div id="analyticsHeader" class="boxHeader"><h3 class="header-small">Analytics</h3></div>
	    <div id="currentSquare" class="square">
		  <table style="width: 100%">
		    <tr class="tableRow">
			  <th class="eventHead">Title</th>
			  <th class="eventHead">Date</th>
			  <th class="eventHead">Location</th>
			  <th class="eventHead">Attendees</th>
			</tr>
			<tr class="tableRow">
			  <td class="eventCell">Happy Hour</td>
			  <td class="eventCell">2/23/2016</td>
			  <td class="eventCell">Center City</td>
			  <td class="eventCell">38</td>
		  </table>
		</div>
		<div id="analyticsSquare" class="square">
		  <table width="100%">
		    <tr class="tableRow">
			  <th class="analyticsHead"><h5 class="section-head">Popular Tags</h5></th>
			  <th class="analyticsHead"><h5 class="section-head">Demographic Info</h5></th>
			</tr>
			<tr class="tableRow">
				<td align="center">
		  			<canvas id="age" width="150" height="150"></canvas>
			  	</td>
			  	<td align="center">
		  			<canvas id="popularTags" width="150" height="150"></canvas>
			  	</td>
			</tr>
			<tr class="tableRow">
				<td align="center" colspan="2">
					<form action="/analytics">
    					<input type="submit" value="View All Analytics">
					</form>
				</td>
			</tr>
		  </table>
		</div>
		<div id="pastHeader" class="boxHeader"><h3 class="header-small">Past Events</h3></div>
		<div id="billingHeader" class="boxHeader"><h3 class="header-small">Billing</h3></div>
	    <div id="pastSquare" class="square">
		  <table style="width: 100%">
		    <tr class="tableRow">
			  <th class="eventHead">Title</th>
			  <th class="eventHead">Date</th>
			  <th class="eventHead">Location</th>
			  <th class="eventHead">Attendees</th>
			</tr>
			<tr class="tableRow">
			  <td class="eventCell">Event 1</td>
			  <td class="eventCell">2/23/2016</td>
			  <td class="eventCell">Location 1</td>
			  <td class="eventCell">18</td>
		  </table>
		</div>
		<div id="billingSquare" class="square">
		  <table style="width: 100%">
		    <tr class="tableRow">
			  <th class="billingHead">Event Title</th>
			  <th class="billingHead">Date</th>
			  <th class="billingHead">Shares</th>
			  <th class="billingHead">Attendees</th>
			  <th class="billingHead">Cost</th>
			</tr>
			<tr class="tableRow">
			  <td class="billingCell">Oktoberfest</td>
			  <td class="billingCell">2/23/2016</td>
			  <td class="billingCell">20</td>
			  <td class="billingCell">100</td>
			  <td class="billingCell">$25</td>
		  </table>
		</div>
	</div>
  </jsp:body>


</t:ZeppaBase>