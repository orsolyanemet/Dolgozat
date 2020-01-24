<!--Nemet Orsolya-->
<%@page import="ro.edu.ubb.entity.Room"%>
<%@page import="ro.edu.ubb.service.RoomService"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler List Rooms</title>
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
<link rel="stylesheet" type="text/css" href="styles/list.css">
<link rel="icon" type="image/png" href="logo.png" sizes="55x55">
<script src="js/adminmenu.js"></script>
<script src="js/navigator.js"></script>
</head>
<body>
	<div id="includedMenubar"></div>
	<label>List of the rooms</label>
	<button onClick="window.location.reload();">Refresh</button>
	<table id="myTable"
		class="table table-striped table-hover refresh-container pull-down">
		<%
			RoomService roomService = new RoomService();
			request.getSession().setAttribute("rooms", roomService.getAllRooms());
			List<Room> rooms = (List<Room>) request.getSession().getAttribute("rooms");
			if (rooms != null && !rooms.isEmpty()) {
		%>
		<thead class="hidden-xs">
			<tr>
				<td>Room name</td>
				<td>Location</td>
				<td>Room attributes</td>
			</tr>
		</thead>
		<%
			for (Room room : rooms) {
		%>
		<tbody>
			<tr>
				<td>
					<%
						out.print(room.getRoomName());
					%>
				</td>
				<td>
					<%
						out.print(room.getLocation());
					%>
				</td>
				<td>
					<%
						if (!room.getRoomTypeList().isEmpty()) {
									out.print(room.getRoomTypeList().get(0).getRoomTypeName());
									for (int i = 1; i < room.getRoomTypeList().size(); i++) {
										out.print(", " + room.getRoomTypeList().get(i).getRoomTypeName());
									}
								}
					%>
				</td>
			</tr>
			<%
				}
				} else {
			%>
			<label class="errorlabel">There are no rooms found.</label>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>