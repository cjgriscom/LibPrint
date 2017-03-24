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
	
	if (req.equals("configure")) {
		if (Database.isDomainCodeSet()) {
			errMsg.set("?error=The%20server%20is%20already%20configured.");
		} else {
			final String username = request.getParameter("setusername");
			final String password = request.getParameter("setpassword");
			final String password2 = request.getParameter("setpassword2");
			final String domainCode = request.getParameter("domainCode");
			if (username != null && password != null && password2 != null && domainCode != null) {
				if (username.isEmpty() || password.isEmpty() || password2.isEmpty() || domainCode.isEmpty()) {
					errMsg.set("?error=Fill%20in%20all%20fields%20before%20submitting.");
				} else if (!password.equals(password2)) {
					errMsg.set("?error=The%20passwords%20do%20not%20match..");
				} else {
					Database.accessUserList(new Consumer<UserList>() {public void accept(final UserList userList) {
						userList.clear();
						userList.addAdmin(username, password.toCharArray());
					}}, false);

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
	
} else if (!loggedIn) {
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
	<meta http-equiv="Access-Control-Allow-Origin" content="*" />
	<link rel="stylesheet" type="text/css" href="mystyle.css">
	<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.2.min.js"></script>
	<script src="scripts.js"></script>
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
		<a href="http://www.bestbuy.com/site/office-electronics/printers/abcat0511001.c?id=abcat0511001" class="button" style="width:48%">  Edit Printers  </a>
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
	Color &emsp; &emsp; &emsp; &emsp;
	<input id="rb1" type="radio" name="group1" value="G1A" checked> 
	<label for="rb1">Active</label>
	<input id="rb2" type="radio" name="group1" value="G1M"> 
	<label for="rb2">Maintenance</label>
	<br>
	Black and White &nbsp;
	<input id="rb3" type="radio" name="group2" value="G2A" checked> 
	<label for="rb3">Active</label>
	<input id="rb4" type="radio" name="group2" value="G2M"> 
	<label for="rb4">Maintenance</label>
</div>
<a class="tinyspace"> </a>
<div class="box" style="width:49%;">
<a href="http://www.logout.com/" class="button">  Update </a>
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
