<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/smoothness/jquery-ui-1.8.5.custom.css'/>";
	@import "<c:url value='/media/css/demo_table_jui.css'/>";
	@import "<c:url value='/media/css/demo_table.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<style type="text/css">
#insert_search {
	width: 80%;
	padding: 0;
	padding-left: 20px;
}

#insert_search td {
	padding: 5px;
}

table td.result_detail {
	width: 100%;
}

table td:first-child {
	vertical-align: top;
}

.result_background {
	border: 1px solid black;
	background-color: #FF9F87;
	float: right;
	width: 25px;
	margin-top: 5px;
}

#searchWord {
	display: none;
}

.searchWord {
	font-style: italic;
}
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript">
	/* Session objects disabled */
	/*
	var countUrl = "<c:url value='/app/bench/count_ajax'/>";
	var clearAllUrl = "<c:url value='/app/bench/clear_ajax'/>";
	*/
	var sessionTimeoutRedirectUrl = "<c:url value='/app/search'/>";

	$(document).ready(function() {
	
		$('body').ajaxError(handleAjaxSessionTimeout);

		$("#queryText").focus();
		
		/* Search Tab validation and submission */
		/*$("#searchQuery").validate({
			rules: {
				queryText: {
					required: true,
					remote: "<c:url value='/app/search/validate_ajax'/>"
					}
				},
			messages: {
				queryText: {
					required: "Please enter a search query",
					remote: "Queries must be longer than 1 character and cannot begin with *"
					}
				},
			errorLabelContainer: "#errors",
			submitHandler: function(form) {
				$.post("<c:url value='/app/search/query_json'/>",
					$("#searchQuery").serialize(),
					function(data) {$("#app_result").html(data);}
				);
				}
		});*/

		$(".search_button").click(function() {
			var queryText = $("#queryText").val();
			var dataString = "queryText=" + queryText;
			if (queryText == "") {}
			else {
				$.ajax( {
					"dataType": "json",
					"type": "GET",
					"url": "<c:url value='/app/search/query_json'/>",
					"data": dataString,
					"cache" : false,
					"beforeSend": function(html) {
						document.getElementById("insert_search").innerHTML = "";
						$(".searchCount").html("");
						$("#flash").show();
						$("#searchWord").show();
						$(".searchWord").html("'" + queryText + "'");
						$("#flash").html('<img src="/shap/media/images/ajax-loader.gif" align="absmiddle">&nbsp;Loading Results...');
					},
					"complete": function(html) {
						$("#flash").hide();
					},
					"success": function(data) {
						$(".searchCount").html(" returned " + data.resultSize + " results");
						if (data.results.length > 0) {
							var maxScore = data.results[0].score;
							for (var i=0; i<data.results.length; i++) {
								var an = $("<a/>");
								an.text(data.results[i].label + " " + data.results[i].id);
								an.attr("href","<c:url value='/app/browse/object/'/>" + data.results[i].id);
			
								var sbar = $("<div/>",{"class": "result_score"});
								sbar.attr("title", "Score: " + data.results[i].score);
								sbar.width(Math.round(25 * data.results[i].score / maxScore));
								
								var score = $("<div/>",{"class": "result_background"});
								score.append(sbar);

								var head = $("<div/>", {"class": "result_heading"});
								head.append(an);
														
								var detail = $("<div/>", {"class": "result_detail"});
								detail.html(data.results[i].detail);

								var td_detail = $("<td/>", {"class": "result_detail"});
								td_detail.append(head);
								td_detail.append(detail);
								
								var td_score = $("<td/>");
								td_score.append(score);

								var row = $("<tr/>", {"class": "result_row"});
								row.append(td_score);
								row.append(td_detail);
								$("#insert_search").append(row);
							}
							$("#insert_search").show();
						}
					}
				} );
			}
			return false;
		});

	});

	
</script>
</head>
<body>
<div id="app_container" class="clearfix">

	<div id="app_header">
		<div class="header_title">SHAP <span class="header_comment">search</span></div>
		<div class="header_action">${user.username} <a href="<c:url value='/logout'/>">logout</a></div>
	</div>
	
	<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>
	
	<div id="app_nav"> 
		<ul>
			<li>Search</li>
			<li><a href="<c:url value='/app/browse'/>">Browse</a></li>
		</ul>
	</div>
	
	<div id="app_content" class="clearfix">
		<div id="app_form">
			<h3>Search the system</h3>
			<form id="searchQuery">
				<input id="queryText" name="queryText" size="40" tabindex="1" value="${queryText}"/>
				<input type="submit" tabindex="2" value="Search" class="search_button"/>
				<div id="errors"></div>
			</form>
		</div>
		
		<div id="app_result">
			<div id="searchWord">Search for <span class="searchWord"></span><span class="searchCount"></span></div>
			<div id="flash"></div>
			<table id="insert_search" class="update"></table>
		</div>

	</div> <!-- app_content -->
	
	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
