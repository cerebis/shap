<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Find Sequences Result</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/demo_page.css'/>";
	@import "<c:url value='/media/css/demo_table.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#found').dataTable( {
		"sPaginationType": "full_numbers" } );
} );
</script>
</head>
<body id="dt_example">
	<div id="container">
		<div class="full_width big"><i>Search Results</i></div>
		<h1>Located the following sequences</h1>
		<div class="demo">
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="found">
				<thead><tr>
				<th>Id</th>
				<th>Name</th>
				<th>Description</th>
				<th>Taxonomy</th>
				<th>Coverage</th>
				</tr></thead>
			
				<tbody>
				<c:choose>
				<c:when test="${fn:length(sequences) == 0}">
					<tr><td colspan="5" class="dataTables_empty">No results were found</td></tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${sequences}" var="seq" varStatus="status">
					<c:url var="entityURL" value="/app/browse/project/${seq.sample.project.id}/sample/${seq.sample.id}/sequence/${seq.id}"/>
					<c:choose>
					<c:when test="${status.count % 2 == 0}"><tr class="even"></c:when>
					<c:otherwise><tr class="odd"></c:otherwise>
					</c:choose>
					<td><a href="${entityURL}">${seq.id}</a></td>
					<td>${seq.name}</td>
					<td>${seq.description}</td>
					<td>${fn:toLowerCase(seq.taxonomy)}</td>
					<td><fmt:formatNumber value="${seq.coverage}" maxFractionDigits="1"/></td>
					</tr>
					</c:forEach>
				</c:otherwise>
				</c:choose>
				</tbody>
			
				<tfoot><tr>
				<th>Id</th>
				<th>Name</th>
				<th>Description</th>
				<th>Taxonomy</th>
				<th>Coverage</th>
				</tr></tfoot>
			</table>
		</div>
	</div>
</body>
</html>
