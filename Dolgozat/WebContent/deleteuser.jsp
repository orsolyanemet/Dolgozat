<!--Nemet Orsolya-->
<%@page import="ro.edu.ubb.entity.User"%>
<%@page import="ro.edu.ubb.service.UserService"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler Delete User</title>
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
<script src="js/deleteuser.js"></script>
<script src="js/filter.js"></script>
</head>
<body>
	<div id="includedMenubar"></div>
	<label>Delete user</label>
	<div class="col-md-3">
		<form action="#" method="get">
			<div class="input-group">
				<input class="form-control" id="system-search" name="q"
					placeholder="Search for" required> <span
					class="input-group-btn">
				</span>
			</div>
		</form>
	</div>
	<table id="myTable"
		class="table table-list-search table-striped table-hover refresh-container pull-down">
		<%
			UserService userService = new UserService();
			request.getSession().setAttribute("users", userService.getAllUsers());
			List<User> users = (List<User>) request.getSession().getAttribute("users");
			if (users != null && !users.isEmpty()) {
		%>
		<thead class="hidden-xs">
			<tr>
				<td></td>
				<td>User name</td>
				<td>First name</td>
				<td>Last name</td>
				<td>Email</td>
			</tr>
		</thead>
		<tbody>
		<%
			for (User user : users) {
		%>
			<tr>
				<td><button id="button" onClick="deleteUser(this,'')"
						name=<%=user.getIdUser()%>>
						<i class="fa fa-trash"></i>
					</button></td>
				<td>
					<%
						out.print(user.getUsername());
					%>
				</td>
				<td>
					<%
						out.print(user.getFirstName());
					%>
				</td>
				<td>
					<%
						out.print(user.getLastName());
					%>
				</td>
				<td>
					<%
						out.print(user.getEmail());
					%>
				</td>
			</tr>
			<%
				}
				} else {
			%>
			<label class="errorlabel">There are no users registered.</label>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>