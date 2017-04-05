<%@page import="java.util.Arrays"%>
<%@page import="edu.letu.libprint.PrintDispatch"%>
<%@page import="edu.letu.libprint.ClientInterface"%>
<%@page import="edu.letu.libprint.Util"%>
<%@page import="edu.letu.libprint.WebInterface"%>
<%@page import="edu.letu.libprint.db.PrinterList"%>
<%@page import="java.util.function.Consumer"%>
<%@page import="com.quirkygaming.propertylib.MutableProperty"%>
<%@page import="edu.letu.libprint.db.Database"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<% 

final MutableProperty<String> errMsg = MutableProperty.newProperty("");
final MutableProperty<Boolean> valid = MutableProperty.newProperty(false);

if (!WebInterface.validateJSPSession(session, errMsg, false, true)) {
	response.sendRedirect("index.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}

if ("POST".equalsIgnoreCase(request.getMethod()) && request.getParameter("request") != null) {

	final String req = request.getParameter("request");
	
	if (req.startsWith("deletePrinter")) {
		final String code = req.substring("deletePrinter".length());
		Database.accessPrinterList(new Consumer<PrinterList>(){
			public void accept(PrinterList pl) {
				String printerName = pl.getPrinterByID(code);
				if (printerName == null) {
					errMsg.set("The printer could not be found.  Try refreshing the page.");
				} else {
					pl.removePrinter(printerName);
				}
			}}, true);
	} else if (req.equals("addPrinter")) {
		Database.accessPrinterList(new Consumer<PrinterList>(){
			public void accept(PrinterList pl) {
				final String printerName = request.getParameter("printerName");
				final String systemName = request.getParameter("systemName");
				final String studentPrice = request.getParameter("studentPrice");
				final String patronPrice = request.getParameter("patronPrice");
				if (printerName == null || systemName == null || studentPrice == null || patronPrice == null) {
					errMsg.set("Request incomplete.");
				} else if (pl.printerExists(printerName)) { // Printer exists
					errMsg.set("That printer already exists.");
				} else if (!Arrays.asList(PrintDispatch.listSystemPrinters()).contains(systemName)) { // Printer exists
					errMsg.set("The system printer specified does not exist.");
				} else {
					try {
						double studentPriceDouble = Double.parseDouble(studentPrice);
						double patronPriceDouble = Double.parseDouble(patronPrice);
						pl.addPrinter(printerName, systemName, true, patronPriceDouble, studentPriceDouble);
					} catch (NumberFormatException e) {
						errMsg.set("Please enter the price per page in the following format: x.xx");
					}
				}
			}}, true);
		
	} else if (req.equals("logout")) {
		session.removeAttribute("user");
		response.sendRedirect("index.jsp");
		return;
	    
	}
	response.sendRedirect("printers.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}
%>

<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="mystyle.css">
	
	<%if (request.getParameter("error") != null) {%>
		<script>
			alert("<%out.print(request.getParameter("error"));%>");
		</script>
	<%}%>
	
	<title>LibPrint - Edit Printers</title>
</head>

<body>
	<div class = "front">
	<div class = "components">
	<br>
	<h1>LibPrint - Edit Printers</h1>
    <br>
	<div class = "buttons">
	<div class="box" style="width:50%;float:left;" >
		<a href="users.jsp" class="button" style="width:32%;float:left">  Edit Users  </a>
		<a class="tinyspace">&nbsp;</a>
		<a href="index.jsp" class="button" style="width:32%;float:left">  Administration  </a>
		<a class="tinyspace">&nbsp;</a>
		<a href="system.jsp" class="button" style="width:32%;float:left">  Edit Server Settings  </a>
		<a class="tinyspace">&nbsp;</a>
	
	</div>
	<a class="tinyspace"> </a>
	<div class="box" style="width:49%;">
	<form method="post">
 		<input name="request" type="hidden" value="logout"/>
 		
 		<button type="submit" class="button">  Logout: <%out.println(session.getAttribute("user"));%>  </button>
 	</form>
		 
		
	</div>
	</div>
	<br><br><br>
	<div id="ap">
	
	<form method="post">
	<table id="printers" class="responstable">
	<tr>
		<th>Printer Name</th>
		<th>Actual Name</th>
		<th>Student Price Per Page</th>
		<th>Public Patron Price</th>
		<th>Action</th>
	</tr>
	<% 
		final StringBuilder printersOut = new StringBuilder();
		Database.accessPrinterList(new Consumer<PrinterList>(){
			public void accept(PrinterList pl) {
				for (String printer : pl.getPrinterNames()) {
					//String active = pl.isActive(printer);
					
					printersOut.append("<tr>");
					printersOut.append("<td>"+printer+"</td>");
					printersOut.append("<td>"+pl.getWindowsPrinterName(printer)+"</td>");
					printersOut.append("<td>"+Util.formatPrice(pl.getStudentPrice(printer))+"</td>");
					printersOut.append("<td>"+Util.formatPrice(pl.getPatronPrice(printer))+"</td>");
					printersOut.append("<td><button type=\"submit\" class=\"button\" name=\"request\" value=\"deletePrinter"+pl.getPrinterID(printer)+"\">Delete</button></td>");
					printersOut.append("</tr>");
				}
			}
		}, false);
		
		printersOut.append("<tr>");
		printersOut.append("<td><input name=\"printerName\" placeholder=\"New Printer Name\"/></td>");
		printersOut.append("<td><select name=\"systemName\">"+WebInterface.getSystemPrintersOptionList()+"</select></td>");
		printersOut.append("<td><input name=\"studentPrice\" placeholder=\"Student Price\"/></td>");
		printersOut.append("<td><input name=\"patronPrice\" placeholder=\"Patron Price\"/></td>");
		printersOut.append("<td><button type=\"submit\" class=\"button\" name=\"request\" value=\"addPrinter\">Add New</button></td>");
		printersOut.append("</tr>");
		
		out.println(printersOut);
		%>
    </table>
    </form>
	</div>
	<br><br>
	<br><br>
	</div>
</div>
</body>

</html>
