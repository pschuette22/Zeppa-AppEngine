<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="lib/js/TableSorterJs/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="lib/js/TableSorterJs/jquery.tablesorter.widgets.min.js"></script>
<link rel="stylesheet" type="text/css" href="lib/css/TableSorterCSS/theme.default.css">


<script type="text/javascript">
var tagQueue = [];
$(document).ready(function() {
	parseEvents('${allEvents}');
	
	$("body").on("click", ".event", function(){
		$(this).toggleClass("active");
	});
	$('#eventsTable').tablesorter({

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

		// The key used to select more than one column for multi-column
		// sorting.
		sortMultiSortKey: "shiftKey",

		// key used to remove sorting on a column
		sortResetKey: 'ctrlKey',

		// false for German "1.234.567,89" or French "1 234 567,89"
		usNumberFormat: true,

		// If true, parsing of all table cell data will be delayed
		// until the user initializes a sort
		delayInit: false,

		// if true, server-side sorting should be performed because
		// client-side sorting will be disabled, but the ui and events
		// will still be used.
		serverSideSorting: false,

		// default setting to trigger a resort after an "update",
		// "addRows", "updateCell", etc has completed
		resort: true,

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

		// ignore case while sorting
		ignoreCase: true,

		// forces the user to have this/these column(s) sorted first
		sortForce: null,
		// initial sort order of the columns, example sortList: [[0,0],[1,0]],
		// [[columnIndex, sortDirection], ... ]
		sortList: [ [2,1],[0,0],[1,0] ],
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

		// third click on the header will reset column to default - unsorted
		sortReset: false,

		// restart sort to "sortInitialOrder" when clicking on previously
		// unsorted columns
		sortRestart: false,

		// sort empty cell to bottom, top, none, zero, emptyMax, emptyMin
		emptyTo: "bottom",

		// sort strings in numerical column as max, min, top, bottom, zero
		stringTo: "max",

		// extract text from the table
		textExtraction: {
			0: function (node, table) {
				// this is how it is done by default
				return $(node).attr(table.config.textAttribute) ||
					node.textContent ||
					node.innerText ||
					$(node).text() ||
					'';
			},
			1: function (node) {
				return $(node).text();
			}
		},

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
function parseEvents(eventsString){
	var events = jQuery.parseJSON(eventsString);
	for (var i = 0; i < events.length; i++){
		var id = events[i].id;
		var title = events[i].title;
		var description = events[i].description;
		var location = events[i].displayLocation;
		
		var start = new Date(events[i].start);
		var date = start.getDate();
	    var month = start.getMonth() + 1; //Months are zero based
	    var year = start.getFullYear();
	    var hour = start.getHours();
	    var minutes = start.getMinutes();
	    if (minutes < 10){
	    	minutes = "0"+minutes
	    }
	    var ampm = "AM";
	    if (hour>11){
	    	hour = hour-12;
	    	ampm = "PM";
	    }
	    if (hour == 0 ){
	    	hour = 12;
	    }
	    var startTimeString = month+"/"+date+"/"+year+"\t"+hour+":"+minutes+" "+ampm;
		$("#eventsTable tbody").append("<tr class='eventRow' data-eventid='"+id+"'><td>"+title+"</td><td><div class='descriptionDiv'>"+description+"</div></td><td>"+startTimeString+"</td><td><div class='addressDiv'>"+location+"</div></td><td><a href='/individual-event?event-id="+id+"'>More info</a></td></tr>");

	}
}

</script>
<style>
	#eventsTable{
		width:100%;
		border-collapse: separate;
	}
	#eventsTable td { 
		padding: 5px; 
	}
	#eventsTable tr:nth-child(odd) {
    	background-color: #EEE;
	}
	.descriptionDiv{
		width:400px;
		overflow:hidden;
		text-overflow: ellipsis;
		max-height:60px;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}
	.addressDiv{
		width:300px;
		max-height:60px;
		overflow:hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}
	
	.headerRow td{
		font-weight:bold;
		font-size:15pt;
	}
	#newEventDiv{
		margin-top:25px;
	}	
</style>
<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Events</h2>
	</jsp:attribute>

<jsp:body>
	<div>
	    <table id="eventsTable">
	    	<thead>
		    	<tr class="headerRow">
		    		<th>Event Title</th>
		    		<th>Description</th>
		    		<th>Start Time</th>
		    		<th colspan="1">Location</th>
		    		<th></th>
		    	</tr>
	    	</thead>
	    	<tbody>
	    	</tbody>
	    </table>
    </div>
    <div id="newEventDiv">
	<input type="button" value="Create Event" onclick="location.href='/create-event';"/>
	</div>
</jsp:body>
</t:ZeppaBase>