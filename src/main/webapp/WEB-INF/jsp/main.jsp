<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/smoothness/jquery-ui-1.8.5.custom.css'/>";
	@import "<c:url value='/media/css/table_jui.css'/>";
	@import "<c:url value='/media/css/table.css'/>";
	@import "<c:url value='/media/css/pagination.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<style type="text/css">
#pagination {
	margin: 10px 0;
}
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.pagination.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript">
	/* Session objects disabled */
	/*
	var countUrl = "<c:url value='/app/bench/count_ajax'/>";
	var clearAllUrl = "<c:url value='/app/bench/clear_ajax'/>";
	*/
	var sessionTimeoutRedirectUrl = "<c:url value='/app/search'/>";

	/* Search result pagination */
	var maxItems = 10;
	var maxScore = 1.0;
	var queryUrl = "<c:url value='/app/search/query_json'/>";

	/**
	 * Fill the result table with rows from the response JSON object.
     *
	 * @param data - JSON responses object
	 **/
	function fillResultTable(data) {
		
		$("#resultTable").hide().empty();

		/* Set the maximum score if this is the first page */
		if (data.firstResult == 0) {
			$("#resultTable").data("page",0);
			maxScore = data.results[0].score;
		}
	
		for (var i=0; i<data.results.length; i++) {
			var an = $("<a/>");
			an.text(data.results[i].label + " " + data.results[i].id);
			an.attr("href","<c:url value='/app/browse/object/'/>" + data.results[i].id);
	
			var sbar = $("<div/>",{"class": "result_score"});
			sbar.width(Math.round(25 * data.results[i].score / maxScore));
			
			var score = $("<div/>",{"class": "result_background"});
			score.attr("title", "Score: " + data.results[i].score);
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
			
			$("#resultTable").append(row);
		}
	
		$("#resultTable").show();
	}

	/**
	 * Pre-submission preparation.
     *
	 * User visual cues for immediate submit of search query.
	 **/
	function prepareForSubmit(queryText) {
		$(".searchCount").html("");
		$("#flash").html('<img src="/shap/media/images/ajax-loader.gif" align="absmiddle">&nbsp;Loading Results...');
		$("#flash").show();
		$(".searchWord").html("'" + queryText + "'");
		$("#searchWord").show();
		$("#resultTable").hide().empty();
		$("#pagination").hide();
	}

	/**
	 * Post-submission.
     *
	 * Remove working indicators, etc from view.
	 **/
	function cleanUpSubmit() {
		$("#flash").hide();
	}

	/**
	 * Submit the search query to the server.
     *
	 * Pagination callback registered here.
	 **/
	function submitSearch() {
		var queryText = $("#queryText").val();
		if (queryText == "") {/*...*/}
		else {
			$.ajax({
				cache: false,
				dataType: "json",
				url: queryUrl,
				data: "queryText=" + queryText,
				beforeSend: function() {
					prepareForSubmit(queryText);
				},
				success: function(data) {
					$(".searchCount").html(" returned " + data.resultSize + " results");
					if (data.resultSize > 0) {
						fillResultTable(data);
						$("#pagination").show();
						$("#pagination").pagination(
							data.resultSize,
							{callback: function(pageIndex, jq) {
									if (pageIndex != $("#resultTable").data("page")) {
										$.ajax({
											cache: false,
											dataType: "json",
											url: queryUrl,
											data: "queryText=" + queryText + "&first=" + pageIndex*maxItems + "&max=" + maxItems,
											success: function(data) {fillResultTable(data);}
										});
										$("#resultTable").data("page",pageIndex);
									}
							}}
						);
					}
				},
				complete: cleanUpSubmit
			});
		}
		
		return false;
	}

	$(document).ready(function() {
		$('body').ajaxError(handleAjaxSessionTimeout);
		$("#queryText").focus();
		$(".search_button").click(submitSearch);
		$(".search_button").button();
		$(".header_action a").button();
	});
	
</script>
</head>
<body>
<div id="app_container" class="clearfix">

	<div id="app_header">
		<div class="header_title">SHAP <span class="header_comment">Search</span></div>
		<div class="header_action"><a href="<c:url value='/logout'/>"><em>[${user.username}]</em> logout</a></div>
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
				<input id="queryText" type="text" name="queryText" size="40" tabindex="1" value="${queryText}"/>
				<input type="submit" tabindex="2" value="Search" class="search_button"/>
				<div id="errors"></div>
			</form>
		</div>
		
		<div id="app_result">
			<div id="searchWord">Search for <span class="searchWord"></span><span class="searchCount"></span></div>
			<div id="flash"></div>
			<table id="resultTable" class="update"></table>
			<div id="pagination" class="pagination"></div>
		</div>

	</div> <!-- app_content -->
	
	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
