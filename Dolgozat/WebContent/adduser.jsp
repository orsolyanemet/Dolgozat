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
<script src="js/adduser.js"></script>
</head>
<body>
	<div id="includedMenubar"></div>
	<main class="my-form">
	<div class="cotainer">
		<div class="row justify-content-center">
			<div class="col-md-4">
				<div class="card">
					<div class="card-header bg-dark">Add user</div>
					<div class="card-body">
						<form name="adduserform" method="POST">
							<div class="form-group row">
								<label for="full_name"
									class="col-md-4 col-form-label text-md-left">First
									name</label>
								<div class="col-md-6">
									<input type="text" id="firstName" class="form-control"
										name="firstName" onKeyUp="validateFirstName()" required>
								</div>
							</div>
							<div class="d-flex justify-content-center ">
								<label class="d-flex justify-content-center"><span
									id="errorFirstName" class="important"></span></label>
							</div>
							<div class="form-group row">
								<label for="full_name"
									class="col-md-4 col-form-label text-md-left">Last name</label>
								<div class="col-md-6">
									<input type="text" id="lastName" class="form-control"
										name="lastName" onKeyUp="validateLastName()" required>
								</div>
							</div>
							<div class="d-flex justify-content-center ">
								<label class="d-flex justify-content-center"><span
									id="errorLastName" class="important"></span></label>
							</div>
							<div class="form-group row">
								<label for="email_address"
									class="col-md-4 col-form-label text-md-left">E-Mail
									Address</label>
								<div class="col-md-6">
									<input type="text" id="email" class="form-control"
										name="email" onKeyUp="validateEmail()" required>
								</div>
							</div>
							<div class="d-flex justify-content-center ">
								<label class="d-flex justify-content-center"><span
									id="errorEmail" class="important"></span></label>
							</div>
							<div class="form-group row">
								<label for="user_name"
									class="col-md-4 col-form-label text-md-left">Username</label>
								<div class="col-md-6">
									<input type="text" id="username" class="form-control"
										name="username" onKeyUp="validateUserName()" required>
								</div>
							</div>
							<div class="d-flex justify-content-center ">
								<label class="d-flex justify-content-center"><span
									id="errorUserName" class="important"></span></label>
							</div>
							<div class="form-group row">
								<label for="present_address"
									class="col-md-4 col-form-label text-md-left">Password</label>
								<div class="col-md-6">
									<input type="password" id="pdUser" class="form-control" onKeyUp="validatePassword()"
										required>
								</div>
							</div>
							<div class="d-flex justify-content-center ">
								<label class="d-flex justify-content-center"><span
									id="errorPassword" class="important"></span></label>
							</div>
							<div class="form-group row">
								<label for="present_address"
									class="col-md-4 col-form-label text-md-left">Confirm Password</label>
								<div class="col-md-6">
									<input type="password" id="pdUserConfirm" class="form-control" onKeyUp="validatePasswordConfirm()"
										required>
								</div>
							</div>
							<div class="d-flex justify-content-center ">
								<label class="d-flex justify-content-center"><span
									id="errorPasswordConfirm" class="important"></span></label>
							</div>
							<div class="col-md-7 offset-md-4">
								<button type="submit"
									class="btn float-right login_btn bg-dark font-weight-bold" onclick="submitButtonClicked()">
									Register</button>
							</div>
							<br> <br> <label id="responseMessage" class="d-flex justify-content-center important"><span
							></span><br></label>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	</main>
</body>
</html>