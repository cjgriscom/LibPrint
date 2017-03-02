<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Sample Index</title>
</head>
<body>
<h1>Website API Calls</h1><hr/>
<form method="get" action="RequestHandler">
	Lists waiting items in the print queue<br/>
	<input type="hidden" name="secToken" value="temp"/>
	Request: listQueue<input type="hidden" name="request" value="listQueue"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="get" action="RequestHandler">
	This will be used to configure printers and associate names like public "Black and White" to a system "HP Officejet m270"<br/>
	<input type="hidden" name="secToken" value="temp"/>
	Request: listSystemPrinters<input type="hidden" name="request" value="listSystemPrinters"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<h1>Client Application API Calls</h1><hr/>
<form method="post" action="RequestHandler">
	<input type="hidden" name="secToken" value="temp"/>
	request: getInformation<input type="hidden" name="request" value="getInformation"/><br/>
	username: <input name="username" value="chandlergriscom"/><br/>
	computer: <input name="computer" value="S1"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="post" action="RequestHandler" enctype="multipart/form-data">
	<input type="hidden" name="secToken" value="temp" />
	request: printPDF<input type="hidden" name="request" value="printPDF"/><br/>
	username: <input name="username" value="chandlergriscom"/><br/>
	computer: <input name="computer" value="S1"/><br/>
	printerName: <input name="printerName" value="Color"/><br/>
	file: <input type="file" name="file"/><br/>
	<input type="submit"/><br/>
</form><hr/>
</body>
</html>