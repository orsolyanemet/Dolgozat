<!--Nemet Orsolya-->
<nav class="navbar navbar-icon-top navbar-expand-sm navbar-dark bg-dark">
	<a class="navbar-brand" href="/">
		<div>
			<img src="logo.png" height="55" width="55">
		</div>
	</a>
	<ul class="navbar-nav mr-auto">
		<li class="nav-item"><a class="nav-link" href="adminhome.jsp">
				<i class="fa fa-home"></i> Home
		</a></li>
		<li class="nav-item"><a class="nav-link" href="listrequests.jsp">
				<i class="fas fa-tasks"> </i> Requests
		</a></li>
		<li class="nav-item"><a class="nav-link" href="roomschedule.jsp">
				<i class="far fa-calendar-alt"> </i> Room schedule
		</a></li>
		<li class="nav-item dropdown"><a class="nav-link dropdown-toggle"
			href="#" id="navbarDropdown" data-toggle="dropdown"> <i
				class="fas fa-users-cog"> </i> Users
		</a>
			<div class="dropdown-menu">
				<a class="dropdown-item" href="listusers.jsp"><i
					class="fas fa-users"></i>List users</a> <a class="dropdown-item"
					href="adduser.jsp"><i class="fas fa-user-plus"></i>Add user</a> <a
					class="dropdown-item" href="edituser.jsp"><i
					class="fas fa-user-edit"></i>Edit user</a> <a class="dropdown-item"
					href="deleteuser.jsp"><i class="fas fa-user-minus"></i>Delete
					user</a>
			</div></li>
		<li class="nav-item dropdown"><a class="nav-link dropdown-toggle"
			href="#" id="navbarDropdown" data-toggle="dropdown"> <i
				class="fas fa-door-open"></i> Rooms
		</a>
			<div class="dropdown-menu">
				<a class="dropdown-item" href="listrooms.jsp"><i
					class="fas fa-list"></i>List rooms</a> <a class="dropdown-item"
					href="addroom.jsp"><i class="fas fa-plus-circle"></i>Add room</a> <a
					class="dropdown-item" href="editroom.jsp"><i
					class="fas fa-edit"></i>Edit room</a> <a class="dropdown-item"
					href="deleteroom.jsp"><i class="fas fa-trash"></i>Delete room</a>
			</div></li>
		<li class="nav-item dropdown"><a class="nav-link dropdown-toggle"
			href="#" id="navbarDropdown" data-toggle="dropdown"><i
				class="fas fa-clipboard-list"></i> Room attributes </a>
			<div class="dropdown-menu">
				<a class="dropdown-item" href="listroomtypes.jsp"><i
					class="fas fa-list"></i>List room attributes</a> <a
					class="dropdown-item" href="addroomtype.jsp"><i
					class="fas fa-plus-circle"></i>Add room attribute</a> <a
					class="dropdown-item" href="editroomtype.jsp"><i
					class="fas fa-edit"></i>Edit room attribute</a> <a
					class="dropdown-item" href="deleteroomtype.jsp"><i
					class="fas fa-trash"></i>Delete room attribute</a>
			</div></li>
	</ul>
	<ul class="navbar-nav ">
		<li class="nav-item" onclick="logout('')"><a class="nav-link"
			href="#logout"> <i class="fas fa-sign-out-alt"></i> Logout
		</a></li>
	</ul>
</nav>