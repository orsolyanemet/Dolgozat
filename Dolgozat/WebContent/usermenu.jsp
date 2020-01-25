<!--Nemet Orsolya-->
<nav class="navbar navbar-icon-top navbar-expand-sm navbar-dark bg-dark">
	<a class="navbar-brand" href="/">
		<div>
			<img src="logo.png" height="55" width="55">
		</div>
	</a>
	<ul class="navbar-nav mr-auto">
		<li class="nav-item"><a class="nav-link" href="userhome.jsp">
				<i class="fa fa-home"></i> Home
		</a></li>
		<li class="nav-item dropdown"><a class="nav-link dropdown-toggle"
			href="#" id="navbarDropdown" data-toggle="dropdown"> <i
				class="fas fa-tasks"> </i> Requests
		</a>
			<div class="dropdown-menu">
				<a class="dropdown-item" href="listuserrequest.jsp"><i
					class="fas fa-list"></i>List requests</a> <a class="dropdown-item"
					href="addrequest.jsp"><i class="fas fa-user-plus"></i>Add
					request</a> <a class="dropdown-item"
					href="deleteuserrequest.jsp"><i class="fas fa-user-minus"></i>Delete
					request</a>
			</div></li>
		<li class="nav-item"><a class="nav-link" href="roomschedule.jsp">
				<i class="far fa-calendar-alt"> </i> Room schedule
		</a></li>
	</ul>
	<ul class="navbar-nav ">
		<li class="nav-item" onclick="logout('')"><a class="nav-link"
			href="#logout"> <i class="fas fa-sign-out-alt"></i> Logout
		</a></li>
	</ul>
</nav>