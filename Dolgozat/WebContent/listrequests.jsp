<!--Nemet Orsolya-->
<%@page import="ro.edu.ubb.entity.Request"%>
<%@page import="ro.edu.ubb.service.RequestService"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler List Requests</title>
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
	<label>List of the requests</label>
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
			RequestService requestService = new RequestService();
			request.getSession().setAttribute("requests",
					requestService.getAllRequests());
			List<Request> requests = (List<Request>) request.getSession().getAttribute("requests");
			if (requests != null && !requests.isEmpty()) {
		%>
		<thead class="hidden-xs">
			<tr>
				<td>Event name</td>
				<td>From time</td>
				<td>To time</td>
				<td>Duration</td>
				<td>Room</td>
				<td>Requested by</td>
			</tr>
		</thead>
		<tbody>
		<%
			for (Request req : requests) {
		%>
			<tr>
				<td>
					<%
						out.print(req.getReservationName());
					%>
				</td>
				<td>
					<%
						out.print(req.getFromTime());
					%>
				</td>
				<td>
					<%
						out.print(req.getToTime());
					%>
				</td>
				<td>
					<%
						out.print(req.getDuration());
					%>
				</td>
				<td>
					<%
						out.print(req.getRoom().getRoomName());
					%>
				</td>
				<td>
					<%
						out.print(req.getUser().getFirstName()+" "+req.getUser().getLastName());
					%>
				</td>
			</tr>
			<%
				}
				} else {
			%>
			<label class="errorlabel">There are no requests registered.</label>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>