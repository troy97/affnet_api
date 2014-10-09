<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Files</title>

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
    <style>
		table {
		    width:100%;
		}
		table, th, td {
		    border: 1px solid black;
		    border-collapse: collapse;
		}
		th, td {
		    padding: 5px;
		    text-align: left;
		}
		table#t01 tr:nth-child(even) {
		    background-color: #eee;
		}
		table#t01 tr:nth-child(odd) {
		   background-color:#fff;
		}
		table#t01 th	{
		    background-color: black;
		    color: white;
		}
	</style>
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
          <a class="navbar-brand" >Affiliate Network</a>
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
	<h2>Last 10 files You have uploaded:</h2>
	</br>
	
	<table id="t01">
	  <tr>
    	<th>Name</th>
    	<th>Products count</th>
    	<th>Upload time</th>
    	<th>Size in bytes</th>
    	<th>Validity</th>
    	<th>Activity</th>
    	<th>Upload count</th>
    	<th>Validation Message</th>
      </tr>
	<#if fileList??>
		<#list fileList as file>
			<tr>
				<td>${file.name}</td>
				<td>${file.productsCount}</td>
				<td>${file.uploadTime?number_to_datetime}</td>
				<td>${file.size}</td>
				<td>${file.valid?string("OK", "invalid")}</td>
				<td>${file.active?string("Active", "inactive")}</td>
				<td>unknown</td>
				<td>empty</td>
			</tr>
		</#list>
	</#if>
	</table>

    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="../../dist/js/bootstrap.min.js"></script>
  </body>
</html>
