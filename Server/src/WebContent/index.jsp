<%@page import="java.util.Enumeration"%>
<%@page import="edu.letu.libprint.Util"%>
<%@page import="edu.letu.libprint.WebInterface"%>
<%@page import="edu.letu.libprint.db.PrinterList"%>
<%@page import="edu.letu.libprint.db.UserList"%>
<%@page import="java.util.function.Consumer"%>
<%@page import="com.quirkygaming.propertylib.MutableProperty"%>
<%@page import="edu.letu.libprint.db.Database"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<% 

if ("POST".equalsIgnoreCase(request.getMethod()) && request.getParameter("request") != null) {

	final MutableProperty<String> errMsg = MutableProperty.newProperty("");
	final MutableProperty<Boolean> valid = MutableProperty.newProperty(false);
	final String req = request.getParameter("request");
	
	if (req.equals("configure")) {
		if (Database.isDomainCodeSet()) {
			errMsg.set("The server is already configured.");
		} else {
			final String username = request.getParameter("setusername");
			final String password = request.getParameter("setpassword");
			final String password2 = request.getParameter("setpassword2");
			final String domainCode = request.getParameter("domainCode");
			if (username != null && password != null && password2 != null && domainCode != null) {
				if (username.isEmpty() || password.isEmpty() || password2.isEmpty() || domainCode.isEmpty()) {
					errMsg.set("Fill in all fields before submitting.");
				} else if (!password.equals(password2)) {
					errMsg.set("The passwords do not match.");
				} else {
					Database.accessUserList(new Consumer<UserList>() {public void accept(final UserList userList) {
						userList.clear();
						userList.addAdmin(username, password.toCharArray());
					}}, true);

					Database.setDomainCode(domainCode);
				}
			}
		}
	} else if (req.equals("login")) {

		final String username = request.getParameter("username");
		final char[] password = request.getParameter("password").toCharArray();
		
		if (username != null && password != null) {
			Database.accessUserList(new Consumer<UserList>() {public void accept(final UserList userList) {
				
				if (userList.userExists(username)) {
					if (userList.passwordMatches(username, password)) {
						valid.set(true);
					} else {
						errMsg.set("The password does not match.");
					}
				} else {
					errMsg.set("User not recognized.");
				}
				
				
			}}, false);
			
			if (valid.get()) 
				session.setAttribute("user", username);
			

			Util.wipe(password); // Zero out password
		}
		
		
	} else if (req.equals("logout")) {
		session.removeAttribute("user");
		response.sendRedirect("index.jsp");
		return;
	    
	} else if (req.equals("editPrinters")) {
		if (WebInterface.validateJSPSession(request.getSession(), errMsg, false, false)) {
			
			Database.accessPrinterList(new Consumer<PrinterList>(){public void accept(PrinterList pl) {
				Enumeration<String> i = request.getParameterNames();
				while (i.hasMoreElements()) {
					String next = i.nextElement();
					if (next.startsWith("printer")) {
						String code = next.replaceFirst("printer", "");
						String printerName = pl.getPrinterByID(code);
						if (printerName != null) pl.setActive(printerName, request.getParameter(next).equals("active"));
					}
				}
			}}, true);
			
		}
	}
	response.sendRedirect("index.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}

%> 


<!DOCTYPE html>
<html>

<% 

if (!Database.isDomainCodeSet()) {
	// Domain code is not set; display initial configuration form
	
%>
<head>
	<link rel="stylesheet" type="text/css" href="mystyle.css">
	<title>LibPrint Initial Configuration</title>
</head>

<body>
	<div class = "front">
	<div class = "components">
	<br>
	<h1>Configure LibPrint</h1>
    <br>
    
	<div class="buttons" style="text-align:center;margin: 0 auto;">
	<span style="color:red;">
    <%    
    if (request.getParameter("error") != null) out.println(request.getParameter("error"));
    else out.println("Declare the administrator user and domain code. "
    		+"The domain code should be a long, unique, private passphrase "
    		+"that uniquely identifies this LibPrint network. "
    		+"Identical domain codes must be declared in each client installation as well.");
    
    %>
    </span>
	<form method="post">
	<input name="request" type="hidden" value="configure"/>
	<br><br>
	<input name="domainCode" placeholder="Domain Code" style="width:40%; height:30px;"/>
	<br><br>
	<input name="setusername" placeholder="Administrator Username" style="width:40%; height:30px;"/>
	<br><br>
	<input name="setpassword" type="password" placeholder="Administrator Password" style="width:40%; height:30px;"/>
	<br><br>
	<input name="setpassword2" type="password" placeholder="Confirm Password" style="width:40%; height:30px;"/>
	<br><br>
	<button type="submit" style="width:40%; height:30px;">Log In</button>
	</form>
	</div>
	<br><br><br><br><br><br><br><br><br>
	</div>
	</div>
</body>

<%
	
} else if (!WebInterface.sessionValid(session)) {
%>

<head>
	<link rel="stylesheet" type="text/css" href="mystyle.css">
	<title>LibPrint Login</title>
</head>

<body>
	<div class = "front">
	<div class = "components">
	<br>
	<h1>LibPrint Login</h1>
    <br>
    
	<div class="buttons" style="text-align:center;margin: 0 auto;">
	<span style="color:red;">
    <%    
    if (request.getParameter("error") != null) out.println(request.getParameter("error"));
    
    %>
    </span>
	<form method="post">
	<input name="request" type="hidden" value="login"/>
	<input name="username" placeholder="Username" style="width:40%; height:30px;"/>
	<br><br>
	<input name="password" type="password" placeholder="Password" style="width:40%; height:30px;"/>
	<br><br>
	<button type="submit" style="width:40%; height:30px;">Log In</button>
	</form>
	</div>
	<br><br><br><br><br><br><br><br><br>
	</div>
	</div>
</body>


<% } else { 
%>

<head>
	<link rel="stylesheet" type="text/css" href="mystyle.css">
	<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.2.min.js"></script>
	<script src="scripts.js"></script>

	<%if (request.getParameter("error") != null) {%>
		<script>
			alert("<%out.print(request.getParameter("error"));%>");
		</script>
	<%}%>

	<title>LibPrint Administration Page</title>
</head>

<body onload="refreshTables()">
	<div class = "front">
	<div class = "components">
	<br>
	<h1>LibPrint Administration Page</h1>
    <br>
	<div class = "buttons">
	<div class="box" style="width:50%;float:left;" >
		<a href="https://en.wikipedia.org/wiki/User_(computing)" class="button" style="width:48%;float:left">  Edit Users  </a>
		<a class="tinyspace"> </a>
		<a href="printers.jsp" class="button" style="width:48%">  Edit Printers  </a>
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
	
<div class="box" style="width:50%;" >
<form method="post"><input name="request" type="hidden" value="editPrinters"/>
	<table>
		<% 
		final StringBuilder printersOut = new StringBuilder();
		Database.accessPrinterList(new Consumer<PrinterList>(){
			public void accept(PrinterList pl) {
				int radioId = 0;
				for (String printer : pl.getPrinterNames()) {
					boolean active = pl.isActive(printer);
					String activeChecked = active ? " checked" : "";
					String maintenanceChecked = !active ? " checked" : "";
					String tdStyle = active ? " style='background-color: LightGreen;'" : " style='background-color: LightCoral;'";
					
					printersOut.append("<tr>");
					printersOut.append("<td"+tdStyle+">"+printer+"</td>");
					printersOut.append(
						  "<td"+tdStyle+"><input id=\"rb"+radioId+"\" type=\"radio\" onChange=\"this.form.submit();\""
						+ " name=\"printer"+pl.getPrinterID(printer)+"\" value=\"active\""+activeChecked+">"
						+ "<label for=\"rb"+radioId+"\">Active</label></td>");
					radioId++;
					printersOut.append(
							  "<td"+tdStyle+"><input id=\"rb"+radioId+"\" type=\"radio\" onChange=\"this.form.submit();\""
							+ " name=\"printer"+pl.getPrinterID(printer)+"\" value=\"maintenance\""+maintenanceChecked+">"
							+ "<label for=\"rb"+radioId+"\">Maintenance</label></td>");
						radioId++;
					
					printersOut.append("</tr>");
				}
			}
		}, false);
		out.println(printersOut);
		%>
	</table>
</form>
</div>
</div>
		
	<br><br><br><br>
	<a id="refresh" class="button" >Refresh Print Queue</a> 
	<br><br><br>
	<div>
	<table id="queue" class="responstable">
    </table>
	
	<br><br><br>
	
	<table id="hist" class="responstable">
	</table>
	</div>
	<br><br><br>
	   
   </div>
   <br><br>
   </div>
</body>


<% } %>

</html>
