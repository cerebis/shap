var oResultTable;

/* 
 * Application variables needed for some event handlers.
 *
 * You must initialize these in your views or globally here.
 */
/*
var aSelectedId = <c:out value="${sessionScope['workbenchItems']}"/>;
var clearAllUrl = "<c:url value='/app/bench/clear_ajax'/>";
var addIdUrl = "<c:url value='/app/bench/add_ajax'/>";
var delIdUrl = "<c:url value='/app/bench/del_ajax'/>";
var countUrl = "<c:url value='/app/bench/count_ajax'/>";
var mainUrl = "<c:url value='/app/'/>";
*/

function ajaxAddIds(aIds) {
	$.get(addIdUrl, {"itemIds": aIds.toString()}, writeCount);
};

function ajaxDelIds(aIds) {
	$.get(delIdUrl, {"itemIds": aIds.toString()}, writeCount);
};

function writeCount(data) {
	$("#app_session_count").html(data + "  items")
};

function ajaxSessionItemCount() {
	$.post(countUrl, writeCount);
};

function selectRow(row,iId) {
	var isSelected = false;
	if (jQuery.inArray(iId,aSelectedId) == -1) {
		aSelectedId[aSelectedId.length++] = iId;
		$("input[type='checkbox'][value=" + iId + "]").attr("checked",true);
		$(row).toggleClass('row_selected');
		isSelected = true;
	}
	return isSelected;
};

function selectAllRows() {
	var toSelect = [];
	$("#table_results tbody tr").each(function (id) {
		var aData = oResultTable.fnGetData(this);
		if (selectRow(this,aData[0])) {
			toSelect[toSelect.length++] = aData[0];
		}
	});
	if (toSelect.length > 0) {
		ajaxAddIds(toSelect);
	}
};
		
function deselectRow(row,iId) {
	var isRemoved = false;
	if (jQuery.inArray(iId,aSelectedId) != -1) {
		aSelectedId = jQuery.grep(aSelectedId, function(value) {return value != iId;});
		$("input[type='checkbox'][value=" + iId + "]").attr("checked",false);
		isRemoved = true;
		$(row).toggleClass('row_selected');
	}
	return isRemoved;
};

function deselectAllRows() {
	var toRemove = [];
	$("#table_results tbody tr").each(function (id) {
		var aData = oResultTable.fnGetData(this);
		if (deselectRow(this,aData[0])) {
			toRemove[toRemove.length++] = aData[0];
		}
	});
	if (toRemove.length > 0) {
		ajaxDelIds(toRemove);
	}
};

function clearAll() {
	$.get(clearAllUrl,writeCount);
	deselectAllRows();
};

function handleAjaxSessionTimeout(event, request, options) {
	if (request.status == "601") {
		var msg = request.statusText;
		if (msg == null || msg.length == 0) {
			msg = "Server request failed";
		}
		window.location.href = mainUrl;
		alert("Problem: " + msg + "\n\n" +
		      "Cause: Your session may have timed out.");
	}
};
