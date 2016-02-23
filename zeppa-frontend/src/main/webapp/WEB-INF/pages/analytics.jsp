<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="../js/jquery-2.1.4.min.js"></script>
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
</style>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Analytics</h2>
	</jsp:attribute>
	<jsp:attribute name="footer">
	  <p id="copyright">Copyright 1927, Future Bits When There Be Bits Inc.</p>
	</jsp:attribute>
	
	<jsp:body>
		<div id="filters">Filters</div>
		<div id="tabNavBar">
			<span class="tabNav active" data-tab="demographicsTab">Demographics</span><!--
			--><span class="tabNav" data-tab="popularDaysTab">Popular Days</span><!--
			--><span class="tabNav" data-tab="popularEventsTab">Popular Events</span><!--
			--><span class="tabNav" data-tab="tagsTab">Tags</span>
		</div>
		
		<div class="analyticsTab active" id="demographicsTab">Demographics Tab</div>
		<div class="analyticsTab" id="popularDaysTab">popularDaysTab</div>
		<div class="analyticsTab" id="popularEventsTab">popularEventsTab</div>
		<div class="analyticsTab" id="tagsTab">tagsTab</div>
	</jsp:body>
</t:ZeppaBase>