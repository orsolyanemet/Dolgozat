<!--Nemet Orsolya -->
<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Page Not Found</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<link rel="stylesheet" type="text/css" href="styles/bootstrap.css">
<link rel="stylesheet" type="text/css" href="styles/error.css">
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-md-12 ">
				<div class="error-text">
					<h1 class="error">Error</h1>
					<h4>Oops! This page Could Not Be Found!</h4>
					<p>The page you are looking for might have been removed, had
						its name changed, is temporarily unavailable or something wrong
						happened.</p>
					<input type="button" onclick="javascript:history.back()"
						value="Please go back to the previous page">
				</div>
			</div>
		</div>
	</div>
</body>
</html>