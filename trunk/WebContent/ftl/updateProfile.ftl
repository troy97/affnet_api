<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Update profile</title>

    <!-- Bootstrap core CSS -->
    <link href="../../dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="starter-template.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="../../assets/js/ie-emulation-modes-warning.js"></script>

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand">Affiliate Network</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
		    <li><a>${name}</a></li>
		    <li><a href="${cabinetPage}">Personal cabinet</a></li>
		    <li><a href="${logoutPage}">Log Out</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>

    <div class="container">
	<h2>Enter Your new personal data</h2>
	<h4>don't modify those fields, that You don't want to change</h4>
   	<#if wrongData??><font color="red">${wrongData}</font></#if>
	<form name="input" class="form-horizontal" action="${checkUpdate}" method="POST">
		</br>
			<div class="form-group">
			<div class="col-xs-3">
			 <label for="id1">Shop name</label>
			 <input type="text" class="form-control" id="id1" placeholder="${shopObject.name}" name="${shopName}">
			</div>
			</div>
			
			<div class="form-group">
			<div class="col-xs-3">
			 <label for="id2">Shop url</label>
			 <input type="text" class="form-control" id="id2" placeholder="${shopObject.url}" name="${shopUrl}">
			</div>
			</div>
			
			</br>
			
			<div class="form-group">	
			<div class="col-xs-3">
			 <label for="id4">E-mail</label>
			 <input type="text" class="form-control" id="id4" placeholder="${userObject.email}" name="${email}">
			</div> 
			</div> 
			<div class="form-group">	
			<div class="col-xs-3">
			 <label for="id5">Password</label>
			 <input type="text" class="form-control" id="id5" placeholder="************" name="${password}">
			</div> 
			</div> 
			<div class="form-group">	
			<div class="col-xs-3">
			 <label for="id6">First name</label>
			 <input type="text" class="form-control" id="id6" placeholder="<#if userObject.firstName??>${userObject.firstName}<#else>empty</#if>" name="${firstName}">
			</div> 
			</div> 
			<div class="form-group">	
			<div class="col-xs-3">
			 <label for="id7">Last name</label>
			 <input type="text" class="form-control" id="id7" placeholder="<#if userObject.lastName??>${userObject.lastName}<#else>empty</#if>" name="${lastName}">
			</div> 
			</div> 
		</br><button class="btn btn-lg btn-primary" type="submit"><span class="glyphicon glyphicon-ok"></span> Update</button>
	</form>
    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="../../dist/js/bootstrap.min.js"></script>
  </body>
</html>
