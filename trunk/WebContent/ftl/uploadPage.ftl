<html>
<head>
<meta charset="UTF-8">
<title>Upload page</title>
</head>
<body>
<h2>Upload your price-list file:</h2>

</br> <#if badFormatMessage??>${badFormatMessage}</#if>
</br>
</br>Web-shop name:
<select name="webshopname" form="uploadform">
  <#list shopList as shop>
 	<option value=${shop.dbId}>${shop.name}</option>
  </#list>
</select>
</br>
<form action="http://localhost:8080/affiliatenetwork/download" enctype="multipart/form-data" method="post" id="uploadform">
	<p>
		Choose file to upload (only .zip and .csv accepted):<br>
		<input type="file" name="datafile" size="40">
	</p>
	<div>
		<input type="submit" value="Send">
	</div>
</form>

	
</body>
</html>