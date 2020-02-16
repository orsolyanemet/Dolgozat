<!--Nemet Orsolya-->
<%@page import="ro.edu.ubb.entity.RoomType"%>
<%@page import="ro.edu.ubb.service.RoomTypeService"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler Add Room</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://use.fontawesome.com/releases/v5.3.1/css/all.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css" href="styles/navbar.css">
<link rel="stylesheet" type="text/css" href="styles/form.css">
<link rel="icon" type="image/png" href="logo.png" sizes="55x55">
<script src="js/adminmenu.js"></script>
<script src="js/navigator.js"></script>
<script src="js/addroom.js"></script>
</head>
<body>
	<div id="includedMenubar"></div>
	<main class="addroomform">
	<div class="cotainer">
		<div class="row justify-content-center">
			<div class="col-md-3">
				<div class="card">
					<div class="card-header bg-dark">Add room</div>
					<div class="card-body">
						<form name="addroomform">
							<div class="form-group row">
								<label for="roomName"
									class="col-md-4 col-form-label text-md-right">Room name</label>
								<div class="col-md-7">
									<input type="text" id="roomName" class="form-control"
										name="roomName" required>
								</div>
							</div>
							<div class="form-group row">
								<label for="location"
									class="col-md-4 col-form-label text-md-right">Location</label>
								<div class="col-md-7">
									<textarea id="location" class="form-control" required></textarea>
								</div>
							</div>
							<div class="form-group row">
								<label for="present_address"
									class="col-md-4 col-form-label text-md-right">Room
									attributes</label>
								<div class="col-md-7">
									<%
			RoomTypeService roomTypeService = new RoomTypeService();
			request.getSession().setAttribute("roomTypes", roomTypeService.getAllRoomTypes());
			List<RoomType> roomTypes = (List<RoomType>) request.getSession().getAttribute("roomTypes");
			if (roomTypes != null && !roomTypes.isEmpty()) {
			for (RoomType roomType : roomTypes) {
		%>
									<div class="form-check">
										<label class="form-check-label"> <input
											type="checkbox" class="form-check-input" value="<%out.print(roomType.getRoomTypeName());%>">
											<%out.print(roomType.getRoomTypeName());%>
										</label>
									</div>
									<%}} %>
								</div>
							</div>
							<div class="col-md-7 offset-md-4">
								<button type="submit"
									class="btn float-right login_btn bg-dark font-weight-bold" onclick="submitButtonClicked()">
									Add room</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	</main>
</body>
</html>