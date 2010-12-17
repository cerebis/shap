<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>
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
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript" charset="utf-8">
	var countUrl = "<c:url value='/app/bench/count_ajax'/>";
	var mainUrl = "<c:url value='/app/'/>";

	$(document).ready(function() {
		
		ajaxSessionItemCount();
	
		$('#ajaxdata').dataTable( {
			"bJQueryUI": true,
			"bProcessing": true,
			"bServerSide": true,
			"bFilter": false,
			"aaSorting": [[1,"asc"]],
			"sPaginationType": "full_numbers", 
			"sAjaxSource": "<c:url value='/app/browse/object/${sample.id}/ajax'/>",
			"fnServerData": function ( sSource, aoData, fnCallback ) {
				$.ajax( {
	    			"dataType": 'json',
					"type": "POST",
					"url": sSource,
					"data": aoData,
					"success": fnCallback
				} );
		   	},
			"fnRowCallback": function ( nRow, aData, iDisplayIndex ) {
				$("td:eq(0)", nRow).html("<a class='in_panel' href=<c:url value='/app/browse/object/'/>" + 
						aData[0] + ">" + aData[0] + "</a>")
				return nRow;
			}
		} );

		$('body').ajaxError(handleAjaxSessionTimeout);
		
	} );
</script>
</head>
<body>
<div id="app_container" class="clearfix">

	<div id="app_header">
		<span id="app_title">SHAP Browse</span>
		<span id="app_logout"><span class="black_italics">${user.username} : </span><a href="<c:url value='/logout'/>">Logout</a></span>
	</div>
	<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>
	
	<div id="app_nav"> 
		<ul>
			<li><a href="<c:url value='/app/'/>">Search</a></li>
			<li>Browse</li>
		</ul>
	</div>

	<div id="app_content" class="clearfix">

		<div id="app_session">
			<b>Session Store: </b><span id="app_session_count"></span>
			<button id="clearAll">Clear All</button>
		</div>
	
		<div id="app_browse_nav">
			<span><a class="in_panel" href="<c:url value='/app/browse/'/>">Projects</a></span>
			&rarr;
			<span><a class="in_panel" href="<c:url value='/app/browse/object/${project.id}'/>">${project.name}</a></span>
			&rarr;
			<span><i>${sample.name}</i></span>
		</div>

		<h3>Sample</h3>
		<div id="app_info">
			<table>
				<tr><td class="label">Sample Id</td><td>${sample.id}</td></tr>
				<tr><td class="label">Name</td><td>${sample.name}</td></tr>
				<tr><td class="label">Description</td><td>${sample.description}</td></tr>
				<tr><td class="label">Creation Date</td><td><fmt:formatDate value="${sample.creation}"/></td></tr>
			</table>
		</div>
		
		<div id="app_export">
			<ul>
				<c:url value='/app/export/object/${sample.id}/fasta' var="dnaURL">
					<c:param name="aa" value="false"/>
					<c:param name="seq" value="true"/>
				</c:url>
				<li><a href="${dnaURL}">Sequence DNA Fasta</a></li>
				<c:url value='/app/export/object/${sample.id}/fasta' var="featDnaURL">
					<c:param name="aa" value="false"/>
					<c:param name="seq" value="false"/>
				</c:url>
				<li><a href="${featDnaURL}">Feature DNA Fasta</a></li>
				<c:url value='/app/export/object/${sample.id}/fasta' var="proteinURL">
					<c:param name="aa" value="true"/>
					<c:param name="seq" value="false"/>
				</c:url>
				<li><a href="${proteinURL}">Feature Protein Fasta</a></li>
				<li><a href="<c:url value='/app/export/object/${sample.id}/table'/>">Annotation Table</a></li>
			</ul>
		</div>
		
		<div class="spacer"></div>
		
		<h3>Sequences contained in this sample</h3>
		<div class="dynamic">
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="ajaxdata">
				<thead><tr>
				<th>Id</th>
				<th>Name</th>
				<th>Description</th>
				<th>Coverage</th>
				<th>Taxonomy</th>
				<th>Length</th>
				<th>Features</th>
				</tr></thead>
			
				<tbody>
				<tr><td colspan="7" class="dataTables_empty">Loading data from server</td></tr>
				</tbody>
			
				<tfoot><tr>
				<th>Id</th>
				<th>Name</th>
				<th>Description</th>
				<th>Coverage</th>
				<th>Taxonomy</th>
				<th>Length</th>
				<th>Features</th>
				</tr></tfoot>
			</table>
		</div>

	</div> <!-- app_content -->

	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
