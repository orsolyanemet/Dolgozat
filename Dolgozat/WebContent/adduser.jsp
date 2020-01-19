<!--Nemet Orsolya-->
<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler Add User</title>
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
</head>
<body>
	<div id="includedMenubar"></div>
	<main class="my-form">
	<div class="cotainer">
		<div class="row justify-content-center">
			<div class="col-md-3">
				<div class="card">
					<div class="card-header bg-dark">Add user</div>
					<div class="card-body">
						<form name="my-form">
							<div class="form-group row">
								<label for="full_name"
									class="col-md-4 col-form-label text-md-right">First
									name</label>
								<div class="col-md-7">
									<input type="text" id="firstname" class="form-control"
										name="firstname" required>
								</div>
							</div>
							<div class="form-group row">
								<label for="full_name"
									class="col-md-4 col-form-label text-md-right">Last name</label>
								<div class="col-md-7">
									<input type="text" id="lastname" class="form-control"
										name="lastname" required>
								</div>
							</div>
							<div class="form-group row">
								<label for="email_address"
									class="col-md-4 col-form-label text-md-right">E-Mail
									Address</label>
								<div class="col-md-7">
									<input type="text" id="emailaddress" class="form-control"
										name="emailaddress" required>
								</div>
							</div>
							<div class="form-group row">
								<label for="user_name"
									class="col-md-4 col-form-label text-md-right">Username</label>
								<div class="col-md-7">
									<input type="text" id="username" class="form-control"
										name="username" required>
								</div>
							</div>
							<div class="form-group row">
								<label for="present_address"
									class="col-md-4 col-form-label text-md-right">Password</label>
								<div class="col-md-7">
									<input type="password" id="pdUser" class="form-control"
										required>
								</div>
							</div>
							<div class="col-md-7 offset-md-4">
								<button type="submit"
									class="btn float-right login_btn bg-dark font-weight-bold">
									Register</button>
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