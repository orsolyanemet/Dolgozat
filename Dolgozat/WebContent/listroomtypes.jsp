<!--Nemet Orsolya-->
<%@page import="ro.edu.ubb.entity.RoomType"%>
<%@page import="ro.edu.ubb.service.RoomTypeService"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler List Room Attributes</title>
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
<script src="js/filter.js"></script>
</head>
<body>
	<div id="includedMenubar"></div>
	<label>List of the room attributes</label>
	<div class="divrefresh">
		<button onClick="window.location.reload();">Refresh</button>
	</div>
	<div class="col-md-3">
		<form action="#" method="get">
			<div class="input-group">
				<input class="form-control" id="system-search" name="q"
					placeholder="Search for" required> <span
					class="input-group-btn">
					<button type="submit" class="btn btn-default">
						<i class="glyphicon glyphicon-search"></i>
					</button>
				</span>
			</div>
		</form>
	</div>
	<table id="myTable"
		class="table table-list-search table-striped table-hover refresh-container pull-down">
		<%
			RoomTypeService roomTypeService = new RoomTypeService();
			request.getSession().setAttribute("roomTypes", roomTypeService.getAllRoomTypes());
			List<RoomType> roomTypes = (List<RoomType>) request.getSession().getAttribute("roomTypes");
			if (roomTypes != null && !roomTypes.isEmpty()) {
		%>
		<thead class="hidden-xs">
			<tr>
				<td>Room attribute</td>
				<td>Equivalent to</td>
			</tr>
		</thead>
		<tbody>
		<%
			for (RoomType roomType : roomTypes) {
		%>
			<tr>
				<td>
					<%
						out.print(roomType.getRoomTypeName());
					%>
				</td>
				<td>
					<%
						if (!roomType.getEquivalentTo().isEmpty()) {
									out.print(roomType.getEquivalentTo().get(0).getRoomTypeName());
									for (int i = 1; i < roomType.getEquivalentTo().size(); i++) {
										out.print(", " + roomType.getEquivalentTo().get(i).getRoomTypeName());
									}
								}
					%>
				</td>
			</tr>
			<%
				}
				} else {
			%>
			<label class="errorlabel">There are no room attributes found.</label>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>