<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

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
	height: 200px;
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
</style> 

<t:ZeppaBase>
  <jsp:attribute name="title">
	  <h2>Dashboard</h2>
  </jsp:attribute>  
  
  <jsp:body>
    <div id="dashboardContent" style="overflow:auto">
    <div id="currentHeader" class="boxHeader"><h3>Current Events</h3></div>
	<div id="analyticsHeader" class="boxHeader"><h3>Analytics</h3></div>
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
		  <th class="analyticsHead">Popular Tags</th>
		  <th class="analyticsHead">Demographic Info</th>
		</tr>
	  </table>
	</div>
	<div id="pastHeader" class="boxHeader"><h3>Past Events</h3></div>
	<div id="billingHeader" class="boxHeader"><h3>Billing</h3></div>
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