<%@page import="edu.letu.libprint.Util"%>
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
	
	if (req.equals("login")) {

		final String username = request.getParameter("username");
		final char[] password = request.getParameter("password").toCharArray();
		
		if (username != null && password != null) {
			Database.accessUserList(new Consumer<UserList>() {public void accept(final UserList userList) {
				
				if (userList.userExists(username)) {
					if (userList.passwordMatches(username, password)) {
						valid.set(true);
					} else {
						errMsg.set("?error=The%20password%20does%20not%20match.");
					}
				} else {
					errMsg.set("?error=User%20not%20recognized.");
				}
				
				
			}}, false);
			
			if (valid.get()) 
				session.setAttribute("user", username);
			

			Util.wipe(password); // Zero out password
		}
		
		
	} else if (req.equals("logout")) {
		session.removeAttribute("user");
	    
	}
	response.sendRedirect(request.getRequestURI() + errMsg.get());
}

%> 
<!DOCTYPE html>
<html>

<% 

boolean loggedIn = session != null && session.getAttribute("user") != null;
if (!loggedIn) {
%>

<head>
	<meta http-equiv="Access-Control-Allow-Origin" content="*" />
	<link rel="stylesheet" type="text/css" href="mystyle.css">
	<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.2.min.js"></script>
	<script src="scripts.js"></script>
	<title>LibPrint Login</title>
</head>

<body onload="refreshTables()">
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
out.println("Logged in: " + session.getAttribute("user") + "  "); // TODO remove
%>


Placeholder for SGUI

<body>

	<form method="post">
	<input name="request" type="hidden" value="logout"/>
	<button type="submit">Log Out</button>
	</form>
</body>


<% } %>


</html>