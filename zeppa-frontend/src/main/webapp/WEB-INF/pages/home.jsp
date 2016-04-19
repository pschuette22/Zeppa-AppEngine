<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script src="../js/Chart.js/Chart.js"></script>
<script type="text/javascript" src="lib/js/TableSorterJs/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="lib/js/TableSorterJs/jquery.tablesorter.widgets.min.js"></script>
<link rel="stylesheet" type="text/css" href="lib/css/TableSorterCSS/theme.default.css">

<script type="text/javascript">
	$(document).ready(function(){
		parseEvents('${upcomingEvents}', '#upcomingEventTable tbody');
		parseEvents('${pastEvents}', '#pastEventTable tbody');
		createGraphs();
		addEventButtons();
		//TABLE SORTER FUNCTIONS
		$('#upcomingEventTable, #pastEventTable').tablesorter({

			// *** APPEARANCE ***
			// Add a theme - 'blackice', 'blue', 'dark', 'default', 'dropbox',
			// 'green', 'grey' or 'ice' stylesheets have all been loaded
			// to use 'bootstrap' or 'jui', you'll need to include "uitheme"
			// in the widgets option - To modify the class names, extend from
			// themes variable. Look for "$.extend($.tablesorter.themes.jui"
			// at the bottom of this window
			// this option only adds a table class name "tablesorter-{theme}"
			theme: 'default',

			// fix the column widths
			widthFixed: false,

			// Show an indeterminate timer icon in the header when the table
			// is sorted or filtered
			showProcessing: false,

			// header layout template (HTML ok); {content} = innerHTML,
			// {icon} = <i/> (class from cssIcon)
			headerTemplate: '{content}{icon}',

			// return the modified template string
			onRenderTemplate: null, // function(index, template){ return template; },

			// called after each header cell is rendered, use index to target the column
			// customize header HTML
			onRenderHeader: function (index) {
				// the span wrapper is added by default
				//Add code here if you want to add classes to rows
				//$(this).find('div.tablesorter-header-inner').addClass('roundedCorners');
			},

			// *** FUNCTIONALITY ***
			// prevent text selection in header
			cancelSelection: true,

			// add tabindex to header for keyboard accessibility
			tabIndex: true,

			// other options: "ddmmyyyy" & "yyyymmdd"
			dateFormat: "mmddyyyy",

			

			// *** SORT OPTIONS ***
			// These are detected by default,
			// but you can change or disable them
			// these can also be set using data-attributes or class names
			headers: {
				// set "sorter : false" (no quotes) to disable the column
				0: { sorter: "text" },
				1: { sorter: "text" },
				4: { sorter: false}
			},

			
			// initial sort order of the columns, example sortList: [[0,0],[1,0]],
			// [[columnIndex, sortDirection], ... ]
			sortList: [ [1,1],[0,0]],
			// default sort that is added to the end of the users sort
			// selection.
			sortAppend: null,

			// when sorting two rows with exactly the same content,
			// the original sort order is maintained
			sortStable: false,

			// starting sort direction "asc" or "desc"
			sortInitialOrder: "asc",

			// Replace equivalent character (accented characters) to allow
			// for alphanumeric sorting
			sortLocaleCompare: false,

			

			// data-attribute that contains alternate cell text
			// (used in default textExtraction function)
			textAttribute: 'data-text',

			// use custom text sorter
			// function(a,b){ return a.sort(b); } // basic sort
			textSorter: null,

			// choose overall numeric sorter
			// function(a, b, direction, maxColumnValue)
			numberSorter: null,

			// *** WIDGETS ***
			// apply widgets on tablesorter initialization
			initWidgets: true,

			// table class name template to match to include a widget
			widgetClass: 'widget-{name}',

			// include zebra and any other widgets, options:
			// 'columns', 'filter', 'stickyHeaders' & 'resizable'
			// 'uitheme' is another widget, but requires loading
			// a different skin and a jQuery UI theme.
			widgets: ['zebra', 'columns'],

			widgetOptions: {

				// zebra widget: adding zebra striping, using content and
				// default styles - the ui css removes the background
				// from default even and odd class names included for this
				// demo to allow switching themes
				// [ "even", "odd" ]
				zebra: [
					"ui-widget-content even",
					"ui-state-default odd"
				],

				// columns widget: change the default column class names
				// primary is the 1st column sorted, secondary is the 2nd, etc
				columns: [
					"primary",
					"secondary",
					"tertiary"
				],

				// columns widget: If true, the class names from the columns
				// option will also be added to the table tfoot.
				columns_tfoot: true,

				// columns widget: If true, the class names from the columns
				// option will also be added to the table thead.
				columns_thead: true,

				// filter widget: If there are child rows in the table (rows with
				// class name from "cssChildRow" option) and this option is true
				// and a match is found anywhere in the child row, then it will make
				// that row visible; default is false
				filter_childRows: false,

				// filter widget: If true, a filter will be added to the top of
				// each table column.
				filter_columnFilters: true,

				// filter widget: css class name added to the filter cell
				// (string or array)
				filter_cellFilter: '',

				// filter widget: css class name added to the filter row & each
				// input in the row (tablesorter-filter is ALWAYS added)
				filter_cssFilter: '',

				// filter widget: add a default column filter type
				// "~{query}" to make fuzzy searches default;
				// "{q1} AND {q2}" to make all searches use a logical AND.
				filter_defaultFilter: {},

				// filter widget: filters to exclude, per column
				filter_excludeFilter: {},

				// filter widget: jQuery selector string (or jQuery object)
				// of external filters
				filter_external: '',

				// filter widget: class added to filtered rows;
				// needed by pager plugin
				filter_filteredRow: 'filtered',

				// filter widget: add custom filter elements to the filter row
				filter_formatter: null,

				// filter widget: Customize the filter widget by adding a select
				// dropdown with content, custom options or custom filter functions
				// see http://goo.gl/HQQLW for more details
				filter_functions: null,

				// filter widget: hide filter row when table is empty
				filter_hideEmpty: true,

				// filter widget: Set this option to true to hide the filter row
				// initially. The rows is revealed by hovering over the filter
				// row or giving any filter input/select focus.
				filter_hideFilters: false,

				// filter widget: Set this option to false to keep the searches
				// case sensitive
				filter_ignoreCase: true,

				// filter widget: if true, search column content while the user
				// types (with a delay)
				filter_liveSearch: true,

				// filter widget: a header with a select dropdown & this class name
				// will only show available (visible) options within the drop down
				filter_onlyAvail: 'filter-onlyAvail',

				// filter widget: default placeholder text
				// (overridden by any header "data-placeholder" setting)
				filter_placeholder: { search : '', select : '' },

				// filter widget: jQuery selector string of an element used to
				// reset the filters.
				filter_reset: null,

				// filter widget: Use the $.tablesorter.storage utility to save
				// the most recent filters
				filter_saveFilters: false,

				// filter widget: Delay in milliseconds before the filter widget
				// starts searching; This option prevents searching for every character
				// while typing and should make searching large tables faster.
				filter_searchDelay: 300,

				// filter widget: allow searching through already filtered rows in
				// special circumstances; will speed up searching in large tables if true
				filter_searchFiltered: true,

				// filter widget: include a function to return an array of values to be
				// added to the column filter select
				filter_selectSource: null,

				// filter widget: Set this option to true if filtering is performed on
				// the server-side.
				filter_serversideFiltering: false,

				// filter widget: Set this option to true to use the filter to find
				// text from the start of the column. So typing in "a" will find
				// "albert" but not "frank", both have a's; default is false
				filter_startsWith: false,

				// filter widget: If true, ALL filter searches will only use parsed
				// data. To only use parsed data in specific columns, set this option
				// to false and add class name "filter-parsed" to the header
				filter_useParsedData: false,

				// filter widget: data attribute in the header cell that contains
				// the default filter value
				filter_defaultAttrib: 'data-value',

				// filter widget: filter_selectSource array text left of the separator
				// is added to the option value, right into the option text
				filter_selectSourceSeparator: '|',

				// Resizable widget: If this option is set to false, resized column
				// widths will not be saved. Previous saved values will be restored
				// on page reload
				resizable: true,

				// Resizable widget: If this option is set to true, a resizing anchor
				// will be included in the last column of the table
				resizable_addLastColumn: false,

				// Resizable widget: Set this option to the starting & reset header widths
				resizable_widths: [],

				// Resizable widget: Set this option to throttle the resizable events
				// set to true (5ms) or any number 0-10 range
				resizable_throttle: false,

				// saveSort widget: If this option is set to false, new sorts will
				// not be saved. Any previous saved sort will be restored on page
				// reload.
				saveSort: true,

				// stickyHeaders widget: extra class name added to the sticky header row
				stickyHeaders: '',

				// jQuery selector or object to attach sticky header to
				stickyHeaders_attachTo: null,

				// jQuery selector or object to monitor horizontal scroll position
				// (defaults: xScroll > attachTo > window)
				stickyHeaders_xScroll: null,

				// jQuery selector or object to monitor vertical scroll position
				// (defaults: yScroll > attachTo > window)
				stickyHeaders_yScroll: null,

				// number or jquery selector targeting the position:fixed element
				stickyHeaders_offset: 0,

				// scroll table top into view after filtering
				stickyHeaders_filteredToTop: true,

				// added to table ID, if it exists
				stickyHeaders_cloneId: '-sticky',

				// trigger "resize" event on headers
				stickyHeaders_addResizeEvent: true,

				// if false and a caption exist, it won't be included in the
				// sticky header
				stickyHeaders_includeCaption: true,

				// The zIndex of the stickyHeaders, allows the user to adjust this
				// to their needs
				stickyHeaders_zIndex : 2

			},

			// *** CALLBACKS ***
			// function called after tablesorter has completed initialization
			initialized: null, // function (table) {}

			// *** extra css class names
			tableClass: '',
			cssAsc: '',
			cssDesc: '',
			cssNone: '',
			cssHeader: '',
			cssHeaderRow: '',
			// processing icon applied to header during sort/filter
			cssProcessing: '',

			// class name indiciating that a row is to be attached to the its parent
			cssChildRow: 'tablesorter-childRow',
			// if this class does not exist, the {icon} will not be added from
			// the headerTemplate
			cssIcon: 'tablesorter-icon',
			// class name added to the icon when there is no column sort
			cssIconNone: '',
			// class name added to the icon when the column has an ascending sort
			cssIconAsc: '',
			// class name added to the icon when the column has a descending sort
			cssIconDesc: '',
			// don't sort tbody with this class name
			// (only one class name allowed here!)
			cssInfoBlock: 'tablesorter-infoOnly',
			// class name added to table header which allows clicks to bubble up
			cssAllowClicks: 'tablesorter-allowClicks',
			// header row to ignore; cells within this row will not be added
			// to table.config.$headers
			cssIgnoreRow: 'tablesorter-ignoreRow',

			// *** SELECTORS ***
			// jQuery selectors used to find the header cells.
			selectorHeaders: '> thead th, > thead td',

			// jQuery selector of content within selectorHeaders
			// that is clickable to trigger a sort.
			selectorSort: "th, td",

			// rows with this class name will be removed automatically
			// before updating the table cache - used by "update",
			// "addRows" and "appendCache"
			selectorRemove: ".remove-me",

			// *** DEBUGING ***
			// send messages to console
			debug: false

		});
	});
	
	// Override default Chart.js options here
	var options = {
		//responsive: true,
		animationEasing: "easeOutQuart"
	};
	
	function createGraphs() {
		// Get the context of the canvas element we want to select
		var ageContext = document.getElementById("age").getContext("2d");
		var tagsContext = document.getElementById("popularTags").getContext("2d");
		var daysContext = document.getElementById("popularDays").getContext("2d");
		var ageChart = new Chart(ageContext).Bar(${ageData}, options);
		var tagChart = new Chart(tagsContext).Bar(${tagData}, options);
		var dayChart = new Chart(daysContext).Bar(${popDaysData}, options);
	}
	
	function getDateString(start) {
		var date = start.getDate();
		var month = start.getMonth() + 1; //Months are zero based
	    var year = start.getFullYear();
	    var hour = start.getHours();
	    var minutes = start.getMinutes();
	    if (minutes < 10){
	    	minutes = "0"+minutes;
	    }
	    var ampm = "AM";
	    if (hour>11){
	    	hour = hour-12;
	    	ampm = "PM";
	    }
	    if (hour == 0 ){
	    	hour = 12;
	    }
	    var dateString = month+"/"+date+"/"+year+"\t"+hour+":"+minutes+" "+ampm;
	    return dateString;
	}
	
	// Parse the json array of upcoming events and generate the table rows for them
	function parseEvents(eventsString, tableId) {
		var events = jQuery.parseJSON(eventsString);
		for (var i = 0; i < events.length; i++){
			var id = events[i].id;
			var title = events[i].title;
			var description = events[i].description;
			var location = events[i].displayLocation;
			var count = events[i].joinedCount;
			
			var start = new Date(events[i].start);
			var startString = getDateString(start);
		    
			$(tableId).append("<tr class='tableRow' data-eventid='"+id+"'><td class=\"eventCell\">"+title+"</td><td class=\"eventCell\">"+startString+"</td><td class='eventCell'>"+location+"</td><td class=\"eventCell\">"+count+"</td></tr>");
		}
	}
	
	function addEventButtons() {
		var upcomingCount = $('#upcomingEventTable tr').length;
		var pastCount = $('#pastEventTable tr').length;
		// Fill out the table to 5 rows so that the button is at the bottom
		for(var i=0; i < (6 - upcomingCount); i++) {
			$("#upcomingEventTable tbody").append("<tr class=\"tableRow\" style=\"height:50px\"><td></td><td></td><td></td><td></td></tr>");
		}
		for(var i=0; i < (6 - pastCount); i++) {
			$("#pastEventTable tbody").append("<tr class=\"tableRow\" style=\"height:50px\"></tr>");
		}
		$("#upcomingEventTable tbody").append("<tr class=\"tableRow\"><td align=\"center\" colspan=\"4\"><form action=\"/events\"><input style=\"margin: 5px;\" type=\"submit\" value=\"View All Events\"></form></td></tr>");
		$("#pastEventTable tbody").append("<tr class=\"tableRow\"><td align=\"center\" colspan=\"4\"><form action=\"/events\"><input style=\"margin: 5px;\" type=\"submit\" value=\"View All Events\"></form></td></tr>");
	}
	
	
	// Extend the themes to change any of the default class names
	// this example modifies the jQuery UI theme class names
	$.extend($.tablesorter.themes.jui, {
		/* change default jQuery uitheme icons - find the full list of icons
		here: http://jqueryui.com/themeroller/
		(hover over them for their name)
		*/
		// table classes
		table: 'ui-widget ui-widget-content ui-corner-all',
		caption: 'ui-widget-content',
		// *** header class names ***
		// header classes
		header: 'ui-widget-header ui-corner-all ui-state-default',
		sortNone: '',
		sortAsc: '',
		sortDesc: '',
		// applied when column is sorted
		active: 'ui-state-active',
		// hover class
		hover: 'ui-state-hover',
		// *** icon class names ***
		// icon class added to the <i> in the header
		icons: 'ui-icon',
		// class name added to icon when column is not sorted
		iconSortNone: 'ui-icon-carat-2-n-s',
		// class name added to icon when column has ascending sort
		iconSortAsc: 'ui-icon-carat-1-n',
		// class name added to icon when column has descending sort
		iconSortDesc: 'ui-icon-carat-1-s',
		filterRow: '',
		footerRow: '',
		footerCells: '',
		// even row zebra striping
		even: 'ui-widget-content',
		// odd row zebra striping
		odd: 'ui-state-default'
	});
</script>

<style>
  table {
	border-collapse: collapse;
  }
  .boxHeader {
    float:left;
	width:45%;
	margin: 0% 1.66% 0% 1.66%;
	text-decoration: underline;
  }
  .square {
    float:left;
    position: relative;
    width: 45%;
	height: 350px;
    padding-bottom : 1%; /* = width for a 1:1 aspect ratio */
    margin: 0 1.66% 2% 1.66%;
    overflow:hidden;
	border: 1px solid black;
	box-shadow: 5px 5px 10px 2px #aaaaaa;
  }
  .long{
  	width: 93.32%;
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
	
	height:50px;
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
	    width:32%;
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
	    <div id="currentHeader" class="boxHeader"><h3 class="header-small">Upcoming Events</h3></div>
		<div id="pastHeader" class="boxHeader"><h3 class="header-small">Past Events</h3></div>
	    <div id="currentSquare" class="square">
		  <table id="upcomingEventTable" style="width: 100%; border-collapse: collapse;">
		   <thead>
			    <tr class="tableRow">
				  <th class="eventHead">Title</th>
				  <th class="eventHead">Date</th>
				  <th class="eventHead">Location</th>
				  <th class="eventHead">Attendees</th>
				</tr>
		  </thead>
		  <tbody>
		  </tbody>
		  </table>
		</div>
	    <div id="pastSquare" class="square">
		  <table id="pastEventTable" style="width: 100%">
		  	<thead>
			    <tr class="tableRow">
				  <th class="eventHead">Title</th>
				  <th class="eventHead">Date</th>
				  <th class="eventHead">Location</th>
				  <th class="eventHead">Attendees</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		  </table>
		</div>
		
		<div id="analyticsHeader" class="boxHeader long"><h3 class="header-small">Analytics</h3></div>
		<div id="analyticsSquare" class="square long">
		  <table style="width: 100%">
		    <tr class="tableRow">
			  <th class="analyticsHead"><h5 class="section-head">Demographic Info</h5></th>
			  <th class="analyticsHead"><h5 class="section-head">Popular Tags</h5></th>
			  <th class="analyticsHead"><h5 class="section-head">Popular Days</h5></th>
			</tr>
			<tr class="tableRow">
				<td align="center">
		  			<canvas id="age" height="269" width="269"></canvas>
			  	</td>
			  	<td align="center">
		  			<canvas id="popularTags" height="264" width="264"></canvas>
			  	</td>
			  	<td align="center">
		  			<canvas id="popularDays" height="269" width="269"></canvas>
			  	</td>
			</tr>
			<tr class="tableRow">
				<td align="center" colspan="3">
					<form action="/analytics">
    					<input type="submit" value="View All Analytics">
					</form>
				</td>
			</tr>
		  </table>
		</div>
	</div>
  </jsp:body>


</t:ZeppaBase>