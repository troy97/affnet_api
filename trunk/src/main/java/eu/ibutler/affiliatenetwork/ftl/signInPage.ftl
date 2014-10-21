<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign In</title>

    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/styles.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
  
    <div class="container">
		<div class="row">
			<div class="col-sm-6 col-md-4 col-md-offset-4">
				<h1 class="text-center login-title">
				 <#if wrongCredentials??><font color="#d9534f">${wrongCredentials}</font><#else>Sign in to continue to Affiliate Network</#if>
				</h1>
				<div class="account-wall">
					<img class="profile-img" src="https://lh3.googleusercontent.com/-PYRBt5l1mpA/AAAAAAAAAAI/AAAAAAAAABY/Acc8QUErJ5E/photo.jpg"
					alt="i-Butler">
					<form class="form-signin" action="${checkSignIn}" method="POST">
						<input type="email" class="form-control" placeholder="Email" required autofocus name="${email}">
						<input type="password" class="form-control" placeholder="Password" required name="${password}">
						<button class="btn btn-lg btn-primary btn-block" type="submit">
						Sign in</button>
						<label class="checkbox pull-left">Not registered yet?</label>
						<a href="${signUpPage}" class="pull-right need-help">${signUpInvitation} </a><span class="clearfix"></span>
					</form>
				</div>
				<!-- <a href="#" class="text-center new-account">Create an account </a> -->
			</div>
		</div>
	</div>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
  </body>
</html>