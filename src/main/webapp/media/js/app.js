/*
 * General application methods
 */

/**
 * Handler for the possibility of AJAX requests after
 * session timeout.
 * 
 * This prevents redirections due to session timeout being 
 * taken as responses to AJAX requests.
 */
function handleAjaxSessionTimeout(event, request, options) {
	if (request.status == "601") {
		var msg = request.statusText;
		if (msg == null || msg.length == 0) {
			msg = "Server request failed";
		}
		window.location.href = sessionTimeoutRedirectUrl;
		alert("Problem: " + msg + "\n\n" +
		      "Cause: Your session may have timed out.");
	}
};
