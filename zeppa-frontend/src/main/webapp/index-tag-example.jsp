<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Index Tag</title>
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

	/**
	 * Format tag's text to be easily consumed by Zeppa:
	 * - Remove Spaces
	 * - Capitalize the first letter of a new word
	 * - TODO: maybe spell check?
	 * - TODO: deny special characters
	 */
	function formatTag(tagText) {
		// Initialize an empty array to push characters onto
		var formattedText = [];
		
		// Iterate backwards through the loop
		var isCaps = true;
		for(i = 0; i < tagText.length; i++){
			var ch = tagText[i];
			if(ch ==" "){
				// If this character is a space, capitalize the next
				isCaps = true;
			} else {
				// This character is NOT a space
				// If lowercase and should be caps, upper it
				if(isCaps){
					ch = ch.toUpperCase();
					isCaps = false;
				}
				// Push this character to string
				formattedText.push(ch);
			}
		}
		
		return formattedText.join("");
	}

	/**
	 * index an example tag
	 */
	function indexTag() {
		// Grab the text for this tag
		var txtRaw = document.getElementById("tagText").textContent;
		console.log("Input Text: %s", txtRaw);
		var txtClean = formatTag(txtRaw);
		console.log("Formatted Text: %s", txtClean);
		var data = {
				'tag-text' : txtClean
			};
		$.post(
				"/index-tag-example",
				data,
				function(resp) {
					console.log("success");
					console.log(resp);
					var message = txtClean;
					message+=" was indexed successfully";
					document.getElementById("successDiv").innerHTML = message;
					// TODO: print out an in depth report
				})
		.fail(
				function() {
					console.log("error");
					document.getElementById("errorDiv").innerHTML = "Failed to index tag";
				});

	}
</script>
</head>
<body>

	<br />
	<br />

	<h1>Indexing Tag Example</h1>

	<div id="errorDiv" style="color: red; font-size: 18px;"></div>
	<div id="successDiv" style="color: green; font-size: 18px;"></div>
	<br />

	<form action="javascript:indexTag()">
		<div>
			<input id="tagText" type="text" name="tagText" />
		</div>
		<br />
		<div>
			<input type="submit" value="Index Tag" />
		</div>

	</form>


</body>
</html>