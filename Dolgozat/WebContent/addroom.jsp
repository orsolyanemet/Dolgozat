<!--Nemet Orsolya-->
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
</head>
<body>
	<div id="includedMenubar"></div>
	<main class="my-form">
	<div class="cotainer">
		<div class="row justify-content-center">
			<div class="col-md-3">
				<div class="card">
					<div class="card-header bg-dark">Add room</div>
					<div class="card-body">
						<form name="my-form">
							<div class="form-group row">
								<label for="full_name"
									class="col-md-4 col-form-label text-md-right">Room name</label>
								<div class="col-md-7">
									<input type="text" id="roomname" class="form-control"
										name="roomname" required>
								</div>
							</div>
							<div class="form-group row">
								<label for="present_address"
									class="col-md-4 col-form-label text-md-right">Location</label>
								<div class="col-md-7">
									<textarea id="location" class="form-control" required></textarea>
								</div>
							</div>
							<div class="form-group row">
								<label for="full_name"
									class="col-md-4 col-form-label text-md-right">Room
									capacity</label>
								<div class="col-md-7">
									<select class="form-control" id="roomcapacity"
										name="roomcapacity" required>
										<option></option>
										<option>50</option>
										<option>100</option>
										<option>150</option>
										<option>200</option>
										<option>250</option>
										<option>300</option>
										<option>400</option>
										<option>500</option>
									</select>
								</div>
							</div>
							<div class="form-group row">
								<label for="present_address"
									class="col-md-4 col-form-label text-md-right">Room
									properties</label>
								<div class="col-md-7" required>
									<div class="form-check">
										<label class="form-check-label"> <input
											type="checkbox" class="form-check-input">RoomType 1
										</label>
									</div>
									<div class="form-check">
										<label class="form-check-label"> <input
											type="checkbox" class="form-check-input">RoomType 2
										</label>
									</div>
									<div class="form-check">
										<label class="form-check-label"> <input
											type="checkbox" class="form-check-input">RoomType 3
										</label>
									</div>
								</div>
							</div>
							<div class="col-md-7 offset-md-4">
								<button type="submit"
									class="btn float-right login_btn bg-dark font-weight-bold">
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