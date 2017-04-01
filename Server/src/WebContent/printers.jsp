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
			}}, false);
		
	} else if (req.equals("logout")) {
		session.removeAttribute("user");
		response.sendRedirect("index.jsp");
		return;
	    
	}
	response.sendRedirect("printers.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}

if (!WebInterface.validateJSPSession(session, errMsg, false, true)) {
	response.sendRedirect("index.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
} else { 
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
		<a href="https://en.wikipedia.org/wiki/User_(computing)" class="button" style="width:48%;float:left">  Edit Users  </a>
		<a class="tinyspace"> </a>
		<a href="index.jsp" class="button" style="width:48%">  Administration  </a>
		<a class="tinyspace"> </a>
	
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
		<th>Student Price</th>
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
					printersOut.append("<td><button type=\"submit\" class=\"button\" name=\"request\" value=\"deletePrinter"+printer.hashCode()+"\">Delete</button></td>");
					printersOut.append("</tr>");
				}
			}
		}, false);
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


<% } %>
