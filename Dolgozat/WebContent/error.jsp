<!--Nemet Orsolya -->
 <%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Page Not Found</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<link rel="stylesheet" type="text/css" href="styles/bootstrap.css">
<link rel="stylesheet" type="text/css" href="styles/error.css">
</head>
<body>
	<div class="container">
		<div>
			<h1>Error</h1>
		</div>
		<div class="warning">
			<h2>The page you are looking for might have been removed, had its
				name changed, is temporarily unavailable or something wrong happened.</h2>
		</div>
		<br>
		<input type="button" onclick="javascript:history.back()" value="Please go back to the previous page">
	</div>
</body>
</html>