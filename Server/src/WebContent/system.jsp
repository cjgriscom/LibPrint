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

if (!WebInterface.validateJSPSession(session, errMsg, false, false, true)) {
	response.sendRedirect("index.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}

if ("POST".equalsIgnoreCase(request.getMethod()) && request.getParameter("request") != null) {

	final String req = request.getParameter("request");
	
	if (req.equals("configure")) {
		String domainCode = request.getParameter("domainCode");
		String maxQueueLength = request.getParameter("maxQueueLength");
		String maxDocsPerUser = request.getParameter("maxDocsPerUser");
		String sessionExpiration = request.getParameter("sessionExpiration");
		String docExpiration = request.getParameter("docExpiration");
		
		if (domainCode == null || maxQueueLength == null || maxDocsPerUser == null || sessionExpiration == null || docExpiration == null) {
			errMsg.set("Incomplete request.");
		} else {
			try {
				int mql = Integer.parseInt(maxQueueLength);
				int mdu = Integer.parseInt(maxDocsPerUser);
				long se = Long.parseLong(sessionExpiration);
				int de = Integer.parseInt(docExpiration);
				
				Database.setDomainCode(domainCode);
				Database.setMaxQueueLength(mql);
				Database.setMaxDocsPerUser(mdu);
				Database.setSessionExpirationTimeMillis(se);
				Database.setDocExpirationTimeMinutes(de);
				
			} catch (NumberFormatException e) {
				errMsg.set(e.getMessage());
			}
		}
	} else if (req.equals("logout")) {
		session.removeAttribute("user");
		response.sendRedirect("index.jsp");
		return;
	    
	}
	response.sendRedirect("system.jsp" + (errMsg.equals("") ? "" : "?error=" + Util.sanitizeURL(errMsg.get())));
	return;
}

String domainCode = Database.getDomainCode();
int maxQueueLength = Database.maxQueueLength();
int maxDocsPerUser = Database.getMaxDocsPerUser();
long sessionExpiration = Database.getSessionExpirationTimeMillis();
long docExpiration = Database.getDocExpirationTimeMinutes();


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
	
	<title>LibPrint - Edit System Settings</title>
</head>

<body>
	<div class = "front">
	<div class = "components">
	<br>
	<h1>LibPrint - Edit System Settings</h1>
    <br>
	<div class = "buttons">
	<div class="box" style="width:50%;float:left;" >
		<a href="users.jsp" class="button" style="width:32%;float:left">  Edit Users  </a>
		<a class="tinyspace">&nbsp;</a>
		<a href="printers.jsp" class="button" style="width:32%;float:left">  Edit Printers  </a>
		<a class="tinyspace">&nbsp;</a>
		<a href="index.jsp" class="button" style="width:32%;float:left">  Administration  </a>
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
	<div class="buttons" style="text-align:center;margin: 0 auto;">
	
	<form method="post">
		<input name="request" type="hidden" value="configure"/>
		<br><br>
		Domain Code: <input name="domainCode" placeholder="Domain Code" value="<% out.print(domainCode); %>" style="width:40%; height:30px;"/>
		<br><br>
		Max Queue Length: <input name="maxQueueLength" placeholder="Max Queue Length" value="<% out.print(maxQueueLength); %>" style="width:40%; height:30px;"/>
		<br><br>
		Max Docs Per User: <input name="maxDocsPerUser" placeholder="Max Docs Per User" value="<% out.print(maxDocsPerUser); %>" style="width:40%; height:30px;"/>
		<br><br>
		Session Expiration Time (ms): <input name="sessionExpiration" placeholder="Session Expiration Time (ms)" value="<% out.print(sessionExpiration); %>" style="width:40%; height:30px;"/>
		<br><br>
		Queue Item Expiration Time (minutes): <input name="docExpiration" placeholder="Queue Item Expiration Time (minutes)" value="<% out.print(docExpiration); %>" style="width:40%; height:30px;"/>
		<br><br>
		<button type="submit" style="width:40%; height:30px;">Configure</button>
	</form>
	</div>
	<br><br>
	<br><br>
	</div>
</div>
</body>

</html>
