<!--Nemet Orsolya-->
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>Room Scheduler Login</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://use.fontawesome.com/releases/v5.3.1/css/all.css">
<link rel="stylesheet" type="text/css" href="styles/login.css">
<link rel="icon" type="image/png" href="logo.png" sizes="55x55">
</head>
<body>
	<div class="container">
		<div class="d-flex justify-content-center h-100">
			<div class="card">
				<div class="card-header">
					<h3>Sign In</h3>
				</div>
				<div class="card-body">
					<form action="login.do" method="POST">
						<div class="input-group form-group">
							<div class="input-group-prepend">
								<span class="input-group-text"><i class="fas fa-user"></i></span>
							</div>
							<input id="username" name="username" type="text" class="form-control"
								placeholder="username" required>
						</div>
						<div class="input-group form-group">
							<div class="input-group-prepend">
								<span class="input-group-text"><i class="fas fa-key"></i></span>
							</div>
							<input id="pdUser" name="pdUser" type="password" class="form-control"
								placeholder="password">
						</div>
						<div class="form-group">
							<input id="log" type="submit" name="login" value="Login"
								class="btn float-right login_btn" required>
						</div>
						<br> <br> <label class="d-flex justify-content-center"><span
							id="msg3" class="important">${msgIncorrectData}</span><br></label>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>