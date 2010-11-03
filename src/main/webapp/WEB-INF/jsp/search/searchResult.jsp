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
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript" charset="utf-8">
	var aSelectedId = <c:out value="${sessionScope['workbenchItems']}"/>;
	var clearAllUrl = "<c:url value='/app/bench/clear_ajax'/>";
	var addIdUrl = "<c:url value='/app/bench/add_ajax'/>";
	var delIdUrl = "<c:url value='/app/bench/del_ajax'/>";
	var countUrl = "<c:url value='/app/bench/count_ajax'/>";
	var mainUrl = "<c:url value='/app/'/>";
	
	$(document).ready(function() {

		jQuery.each(aSelectedId, function(idx, val) {
			aSelectedId[idx] = String(val);
		});
	
		oResultTable = $('#table_results').dataTable( {
			"bJQueryUI": true,
			"bFilter": false,
			"bSort": false,		
			"sPaginationType": "full_numbers",
			"fnRowCallback": function( nRow, aData, iDisplayIndex ) {
				if ( jQuery.inArray(aData[0], aSelectedId) != -1 ) {
					$(nRow).addClass('row_selected');
					$("input[type='checkbox'][value=" + aData[0] + "]").attr("checked", true);
				}
				return nRow;
			},
			"aoColumns": [
				{"bVisible": 0 }, /* ID column */
				null,
		  		null,
		  		null,
				null]
		});
	
	
		$("#selectAll").click(selectAllRows);
		$("#selectNone").click(deselectAllRows);
		$("#clearAll").click(clearAll);
		ajaxSessionItemCount();
	
		/* remove preivous event handlers */
		$('#table_results tbody tr').die('click');
		
		/* add new event handler */
		$('#table_results tbody tr').live('click', function (event) {
			var aData = oResultTable.fnGetData( this );
			var iId = aData[0];
		 	if ( jQuery.inArray(iId, aSelectedId) == -1 ) {
			 	selectRow(this,iId);
				ajaxAddIds(iId);
			}
			else {
				deselectRow(this,iId);
				ajaxDelIds(iId);
			}
		});
		
		$('body').ajaxError(handleAjaxSessionTimeout);
	});
</script>
</head>
<body>
<div id="app_container" class="clearfix">

	<div id="app_header">
		<span id="app_title">SHAP Search</span>
		<span id="app_logout"><a href="<c:url value='/logout'/>">Logout</a></span>
	</div>
	<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>
	
	<div id="app_nav"> 
		<ul>
			<li>Search</li>
			<li><a href="<c:url value='/app/browse'/>">Browse</a></li>
			<li><a href="<c:url value='/app/fetch'/>">Fetch</a></li>
			<li><a href="<c:url value='/app/workbench'/>">Workbench</a></li>
		</ul>
	</div>
	
	<div id="app_content" class="clearfix">

		<div id="app_session">
			<b>Session Store: </b><span id="app_session_count"></span>
			<button id="clearAll">Clear All</button>
		</div>

		<button id="selectAll">All</button>
		<button id="selectNone">None</button>
		<table cellpadding="0" cellspacing="0" border="0" class="display ex_highlight" id="table_results">
			<thead><tr>
			<th>id</th>
			<th></th>
			<th></th>
			<th>Class</th>
			<th>Details</th>
			</tr></thead>
			<tbody>
				<c:forEach items="${results}" var="item">
				<tr>
					<td>${item.id}</td>
					<td><input type="checkbox" name="itemIds" value="${item.id}"/></td>
					<td><a class="in_panel" href="<c:url value='/app/browse/object/${item.id}'/>">View</a></td>
					<td>${item.label}</td>
					<td>${item.detail}</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<div>
		<span>Total results: ${resultSize}</span>
		<c:if test="${maxResults < resultSize}"><span id="app_warning">, only displaying first ${maxResults}</span></c:if>
		</div>
		
	</div> <!-- app_content -->

	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
