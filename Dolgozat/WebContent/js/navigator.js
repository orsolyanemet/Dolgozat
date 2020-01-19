/**
 * Redirection of the log out button.
 * 
 * @author Nemet Orsolya
**/
function logout(urlDepth) {
	$.ajax({
		type : "POST",
		url : urlDepth + "logout",
		success : function(data) {
			window.location.replace(urlDepth + "login.jsp");
		}
	});
}