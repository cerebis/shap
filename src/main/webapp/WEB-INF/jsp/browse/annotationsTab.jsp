<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/smoothness/jquery-ui-1.8.5.custom.css'/>";
	@import "<c:url value='/media/css/table_jui.css'/>";
	@import "<c:url value='/media/css/table.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript" charset="utf-8">
	/*var countUrl = "<c:url value='/app/bench/count_ajax'/>";*/
	var sessionTimeoutRedirectUrl = "<c:url value='/app/'/>";

	$(document).ready(function() {
	
		/*ajaxSessionItemCount();*/
	
		$('#ajaxdata').dataTable( {
			"bJQueryUI": true,
			"bProcessing": true,
			"bServerSide": true,
			"bAutoWidth": false,
			"bFilter": false,
			"aaSorting": [[3,"asc"]],
			"aoColumnDefs": [
  				{"sClass": "text_right", "aTargets": [0,2,3] },
  			],
  			"aoColumns": [
				{"sName": "accession"},
				{"sName": "description"},
				{"sName": "confidence"},
				{"sName": "annotator"}
  			],
			"sPaginationType": "full_numbers", 
			"sAjaxSource": "<c:url value='/app/browse/object/${feature.id}/ajax'/>",
			"fnServerData": function ( sSource, aoData, fnCallback ) {
				$.ajax( {
	    			"dataType": 'json',
					"type": "POST",
					"url": sSource,
					"data": aoData,
					"success": fnCallback
				} );
		   	}
		} );
	
		$('body').ajaxError(handleAjaxSessionTimeout);
		
	} );
</script>
</head>
<body>
<div id="app_container" class="clearfix">

	<jsp:include page="browse_header.jsp"/>
	
	<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>

	<div id="app_nav"> 
		<ul>
			<li><a href="<c:url value='/app/'/>">Search</a></li>
			<li>Browse</li>
		</ul>
	</div>

	<div id="app_content" class="clearfix">

		<!-- 
		<div id="app_session">
			<b>Session Store: </b><span id="app_session_count"></span>
			<button id="clearAll">Clear All</button>
		</div>
		-->

		<div id="app_browse_nav">
			<span><a class="in_panel" href="<c:url value='/app/browse/'/>">Projects</a></span>
			&rarr;
			<span><a class="in_panel" href="<c:url value='/app/browse/object/${project.id}'/>">${project.name}</a></span>
			&rarr;
			<span><a class="in_panel" href="<c:url value='/app/browse/object/${sample.id}'/>">${sample.name}</a></span>
			&rarr;
			<span><a class="in_panel" href="<c:url value='/app/browse/object/${sequence.id}'/>">${sequence.name}</a></span>
			&rarr;
			<span><i>${feature.id}</i></span>
		</div>

		<h3>Feature</h3>
		<div id="app_info">
			<table>
				<tr><td class="label">Feature Id</td><td>${feature.id}</td></tr>
				<tr><td class="label">Sequence Id</td><td>${sequence.id}</td></tr>
				<tr><td class="label">Type</td><td>${feature.type}</td></tr>
				<tr><td class="label">Detector</td><td>${feature.detector.name}</td></tr>
				<tr><td class="label">Confidence</td><td><fmt:formatNumber value="${feature.confidence}" pattern="0.000E0"/></td></tr>
				<tr><td class="label">Location</td><td>${feature.location}</td></tr>
				<tr><td class="label">Partial</td><td>${feature.partial}</td></tr>
			</table>
		</div>
		<div id="app_export">
			<ul>
				<c:url value='/app/export/object/${feature.id}/fasta' var="dnaURL">
					<c:param name="seq" value="false"/>
					<c:param name="aa" value="false"/>
				</c:url>
				<li><a href="${dnaURL}">DNA Fasta</a></li>
				<c:if test="${feature.type == 'OpenReadingFrame'}">
					<c:url value='/app/export/object/${feature.id}/fasta' var="proteinURL">
						<c:param name="seq" value="false"/>
						<c:param name="aa" value="true"/>
					</c:url>
					<li><a href="${proteinURL}">Protein Fasta</a></li>
				</c:if>
			</ul>
		</div>
		
		<div class="spacer"></div>
		
		<h3>Annotations recorded for this feature</h3>
		<div class="dynamic">
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="ajaxdata">
				<thead><tr>
				<th>accession</th>
				<th>description</th>
				<th>confidence</th>
				<th>annotator</th>
				</tr></thead>
			
				<tbody>
				<tr><td colspan="4" class="dataTables_empty">Loading data from server</td></tr>
				</tbody>
			
				<tfoot><tr>
				<th>accession</th>
				<th>description</th>
				<th>confidence</th>
				<th>annotator</th>
				</tr></tfoot>
			</table>
		</div>

	</div> <!-- app_content -->

	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
