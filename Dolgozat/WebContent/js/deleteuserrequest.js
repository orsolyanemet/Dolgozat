/**
 * @author Nemet Orsolya
 */
function deleteRequest(button,urlDepth){
	$.ajax({
		type : "POST",
		url : urlDepth + "deleteuserrequest.do",
		data: { idToDelete: document.getElementsByName(button.name)[0].name} ,
		dataType : "json",
		success : function(data) {
			var respons = data.respons;
			if (respons.localeCompare("OK") == 0) {
				window.location.replace(urlDepth + "deleteuserrequest.jsp");

			} else {
				window.location.replace(urlDepth + "error.jsp");
			}
		}
	});
}