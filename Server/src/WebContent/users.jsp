<%@page import="java.util.Arrays"%>
<%@page import="edu.letu.libprint.PrintDispatch"%>
<%@page import="edu.letu.libprint.ClientInterface"%>
<%@page import="edu.letu.libprint.Util"%>
<%@page import="edu.letu.libprint.WebInterface"%>
<%@page import="edu.letu.libprint.db.UserList"%>
<%@page import="edu.letu.libprint.db.UserList.AccessLevel"%>
<%@page import="java.util.function.Consumer"%>
<%@page import="com.quirkygaming.propertylib.MutableProperty"%>
<%@page import="edu.letu.libprint.db.Database"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<% 

final MutableProperty<String> errMsg = MutableProperty.newProperty("");
final MutableProperty<Boolean> valid = MutableProperty.newProperty(false);

if (!WebInterface.validateJSPSession(session, errMsg, true, false)) {
	response.sendRedirect("index.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}

final String currentUser = WebInterface.getCurrentUser(session);

if ("POST".equalsIgnoreCase(request.getMethod()) && request.getParameter("request") != null) {

	final String req = request.getParameter("request");
	
	if (req.startsWith("deleteUser")) {
		final String username = req.substring("deleteUser".length());
		Database.accessUserList(new Consumer<UserList>(){
			public void accept(UserList ul) {
				if (ul.userExists(username)) {
					if (WebInterface.canModifyAccessLevel(ul, currentUser, ul.getAccessLevel(username))) {
						ul.removeUser(username);
					} else {
						errMsg.set("You do not have permission to delete " + ul.getAccessLevel(username) + " users.");
					}
				} else {
					errMsg.set("The user could not be found.");
				}
			}}, true);
	} else if (req.equals("addUser")) {
		Database.accessUserList(new Consumer<UserList>(){
			public void accept(UserList ul) {
				final String username = request.getParameter("username");
				final String password = request.getParameter("password");
				final String accessPolicy = request.getParameter("accessPolicy");
				if (username == null || password == null || accessPolicy == null || password.isEmpty()) {
					errMsg.set("Request incomplete.");
				} else if (ul.userExists(username)) { // Printer exists
					errMsg.set("That user already exists.");
				} else {
					try {
						AccessLevel ap = AccessLevel.valueOf(accessPolicy);
						if (WebInterface.canModifyAccessLevel(ul, currentUser, ap)) {
							ul.addUser(username, password.toCharArray());
							ul.setAccessPolicies(username, ap);
						} else {
							errMsg.set("You do not have permission to add " + ap + " users.");
						}
					} catch (IllegalArgumentException e) {
						errMsg.set("Access level " + accessPolicy + " does not exist.");
					}
				}
			}}, true);
		
	} else if (req.equals("logout")) {
		session.removeAttribute("user");
		response.sendRedirect("index.jsp");
		return;
	    
	}
	response.sendRedirect("users.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
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
	
	<title>LibPrint - Edit Users</title>
</head>

<body>
	<div class = "front">
	<div class = "components">
	<br>
	<h1>LibPrint - Edit Users</h1>
    <br>
	<div class = "buttons">
	<div class="box" style="width:50%;float:left;" >
		<a href="index.jsp" class="button" style="width:48%;float:left">  Administration  </a>
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
	
	<form method="post">
	<table id="printers" class="responstable">
	<tr>
		<th>Username</th>
		<th>Password</th>
		<th>Access Policy</th>
		<th>Action</th>
	</tr>
	<% 
		final StringBuilder usersOut = new StringBuilder();
		Database.accessUserList(new Consumer<UserList>(){
			public void accept(UserList ul) {
				for (String username : ul.getUsernames()) {
					boolean disabled = currentUser.equals(username) || !WebInterface.canModifyAccessLevel(ul, currentUser, ul.getAccessLevel(username));
					if (disabled) {
						usersOut.append("<tr>");
						usersOut.append("<td>"+username+"</td>");
						usersOut.append("<td>Change Password</td>");
						usersOut.append("<td>"+ul.getAccessLevel(username)+"</td>");
						usersOut.append("<td> </td>");
						usersOut.append("</tr>");
					} else {
						usersOut.append("<tr>");
						usersOut.append("<td>"+username+"</td>");
						usersOut.append("<td>Change Password</td>");
						usersOut.append("<td>"+ul.getAccessLevel(username)+"</td>");
						usersOut.append("<td><button type=\"submit\" class=\"button\" name=\"request\" value=\"deleteUser"+username+"\">Delete</button></td>");
						usersOut.append("</tr>");
					}
					
				}
				
				usersOut.append("<tr>");
				usersOut.append("<td><input name=\"username\" placeholder=\"New Username\"/></td>");
				usersOut.append("<td><input name=\"password\" placeholder=\"Temporary Password\"/></td>");
				usersOut.append("<td><select name=\"accessPolicy\">"+WebInterface.getAccessPolicyOptionList(ul, currentUser)+"</select></td>");
				usersOut.append("<td><button type=\"submit\" class=\"button\" name=\"request\" value=\"addUser\">Add New</button></td>");
				usersOut.append("</tr>");
			}
		}, false);
		
		out.println(usersOut);
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
