<html>
<head>
<meta charset="UTF-8">

    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

<title>login</title>
</head>
<body>
<h2>Welcome</h2>
<h3>Please login before using our service</h3>

    <#if wrongLoginPassword??>${wrongLoginPassword}<#else> </#if>
	<form name="input" action="http://localhost:8080/affiliatenetwork/checkLogin" method="get">
		</br>Username: <input type="text" name="login">
		</br>Password: <input type="text" name="password"> <input type="submit" value="Submit">
	</form>
</body>
</html>