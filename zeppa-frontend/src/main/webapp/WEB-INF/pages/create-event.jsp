<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>



<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="lib/js/moment.js"></script>
<script type="text/javascript" src="lib/js/transition.js"></script>
<script type="text/javascript" src="lib/js/collapse.js"></script>
<script type="text/javascript" src="lib/js/bootstrap.min.js"></script>
<script type="text/javascript" src="lib/js/bootstrap-datetimepicker.min.js"></script>
<link rel="stylesheet" href="lib/css/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="lib/css/bootstrap/css/bootstrap-theme.min.css" />
<link rel="stylesheet" href="lib/css/bootstrap-datetimepicker.min.css" />

<script type="text/javascript">
$(document).ready(function() {
	$('#startTimePicker').datetimepicker();
	$('#endTimePicker').datetimepicker();
	
	$("#button").click(function(){
		createEvent();
	});
});


function createEvent() {
	var title = $("#txtTitle").val();
	var start = $("#startTimePicker").data("DateTimePicker").date().toDate().getTime();
	var end = $("#endTimePicker").data("DateTimePicker").date().toDate().getTime();
	var address = $("#addressInput").val();
	var description = $("#txtDescription").val();
	
	
    var data = {'title': title, 'start': start, 'end': end,
    		'address': address, 'description':description };
    
    console.log("event Info: ", data);
    $.post( "/create-event", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	document.getElementById("successDiv").innerHTML = "Your event was added successfully";
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "We were unable to create your event";
    	});
}
</script>
<script type="text/javascript">
      // This sample uses the Place Autocomplete widget to allow the user to search
      // for and select a place. The sample then displays an info window containing
      // the place ID and other information about the place that the user has
      // selected.

      // This example requires the Places library. Include the libraries=places
      // parameter when you first load the API. For example:
      // <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

      function initMap() {
        var map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: -33.8688, lng: 151.2195},
          zoom: 13
        });

        var input = document.getElementById('addressInput');

        var autocomplete = new google.maps.places.Autocomplete(input);
        autocomplete.bindTo('bounds', map);

        map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

        var infowindow = new google.maps.InfoWindow();
        var marker = new google.maps.Marker({
          map: map
        });
        marker.addListener('click', function() {
          infowindow.open(map, marker);
        });

        autocomplete.addListener('place_changed', function() {
          infowindow.close();
          var place = autocomplete.getPlace();
          if (!place.geometry) {
            return;
          }

          if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
          } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17);
          }

          // Set the position of the marker using the place ID and location.
          marker.setPlace({
            placeId: place.place_id,
            location: place.geometry.location
          });
          marker.setVisible(true);

          infowindow.setContent('<div><strong>' + place.name + '</strong><br>' +
              'Place ID: ' + place.place_id + '<br>' +
              place.formatted_address);
          infowindow.open(map, marker);
        });
      }
    </script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAdzV3Wi4EVmmF34N-cwEEgic4lhj8AvCY&libraries=places&callback=initMap" async defer></script>
<style>
	.descriptionText
	{
	    width:75%;
	    height:75px;
	}
	.addressText{
		width:75%;
	}
	.picker{
		width:auto;
	}
	#map {
        height: 300px;
      }
    .controls {
      background-color: #fff;
      border-radius: 2px;
      border: 1px solid transparent;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
      box-sizing: border-box;
      font-family: Roboto;
      font-size: 15px;
      font-weight: 300;
      height: 29px;
      margin-left: 17px;
      margin-top: 10px;
      outline: none;
      padding: 0 11px 0 13px;
      text-overflow: ellipsis;
      width: 400px;
    }

    .controls:focus {
      border-color: #4d90fe;
    }
</style>
<t:ZeppaBase>
<jsp:body>
	<div>
		<div id="successDiv" style="color: green;"></div>
		<div id="errorDiv" style="color: red;"></div>
	    <table style="width:600px;">
	    	<tr>
	    		<td>Event Title</td>
	    	</tr>
	    	<tr>
	    		<td><input type="text" id="txtTitle" /></td>
	    	</tr>
	  	    <tr>
	    		<td>Start Time</td>
	    		<td>End Time</td>   		 
	    	</tr>
	    	 <tr>
	    		<td>
            		<input type='text' class="form-control picker" id='startTimePicker' />
        		</td>
	    		<td><input type='text' class="form-control picker" id='endTimePicker' /></td>
	    	</tr>
	    	<tr>
	    		<td>Address</td> 		 
	    	</tr>
	    	<tr>
	    		<td colspan="2">
	    			<input id="addressInput" class="controls" type="text" placeholder="Enter a location">
    				<div id="map"></div>
    			</td>   		 
	    	</tr>
 		  	<tr>
	    		<td>Description</td>    		 
	    	</tr>
	    	 <tr>
	    		<td colspan="2"><textarea class="descriptionText" id="txtDescription"></textarea></td>
	    	</tr>
	    	<tr><td style="padding:10px;"><input type="submit" id="button"/></td></tr> 
	    </table>
    </div>
</jsp:body>
</t:ZeppaBase>