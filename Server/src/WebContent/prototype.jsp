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
	Lists waiting items in the print queue.  Use IDs to sort or submit prints<br/>
	GET<br/>
	request=listQueue<input type="hidden" name="request" value="listQueue"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="get" action="RequestHandler">
	Lists accepted or rejected items in the print queue. Sort by ID.<br/>
	GET<br/>
	request=listHistory<input type="hidden" name="request" value="listHistory"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="post" action="RequestHandler">
	Send a queue item to the printer. It will be moved to history.<br/>
	POST<br/>
	request=acceptPrint<input type="hidden" name="request" value="acceptPrint"/><br/>
	ID=<input name="ID" /><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="post" action="RequestHandler">
	Cancel a queue item. It will be moved to history.<br/>
	POST<br/>
	request=rejectPrint<input type="hidden" name="request" value="rejectPrint"/><br/>
	ID=<input name="ID" /><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="get" action="RequestHandler">
	This will be used to configure printers and associate names like public "Black and White" to a system "HP Officejet m270"<br/>
	GET<br/>
	request=listSystemPrinters<input type="hidden" name="request" value="listSystemPrinters"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<h1>Client Application API Calls</h1><hr/>
<form method="post" action="RequestHandler">
	GET<br/>
	request=getInformation<input type="hidden" name="request" value="getInformation"/><br/>
	secToken=<input name="secToken" value="SxY-ildir7TC0y-64Yk-ny86ITr6LkaXXhdxzK0z-qs" /><br/>
	username=<input name="username" value="chandlergriscom"/><br/>
	computer=<input name="computer" value="Prototype Servlet"/><br/>
	<input type="submit"/><br/>
</form><hr/>
<form method="post" action="RequestHandler" enctype="multipart/form-data">
	POST<br/>
	request=printPDF<input type="hidden" name="request" value="printPDF"/><br/>
	secToken=<input name="secToken" value="SxY-ildir7TC0y-64Yk-ny86ITr6LkaXXhdxzK0z-qs" /><br/>
	username=<input name="username" value="chandlergriscom"/><br/>
	computer=<input name="computer" value="Prototype Servlet"/><br/>
	printerName=<input name="printerName" value="Color"/><br/>
	file=<input type="file" name="file"/><br/>
	<input type="submit"/><br/>
</form><hr/>
</body>
</html>