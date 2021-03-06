package edu.letu.libprint;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RequestHandler
 */
@WebServlet("/RequestHandler")
@MultipartConfig
public class RequestHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RequestHandler() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestName;
		PrintWriter out = null;

		if ((requestName = (String) request.getParameter("request")) != null) {
			if (requestName.equals("getInformation")) {
				response.setContentType("text/plain;charset=UTF-8");
				out = response.getWriter();
				ClientInterface.handleGetInformation(request, out);
			} else {
				// JSON responses
				response.setContentType("application/json;charset=UTF-8");
				out = response.getWriter();
				if (requestName.equals("listQueue")) {
					if (WebInterface.validateSession(request.getSession(), out, false, false)) 
						WebInterface.handleListQueue(request, out);
				} else if (requestName.equals("listHistory")) {
					if (WebInterface.validateSession(request.getSession(), out, false, false)) 
						WebInterface.handleListHistory(request, out);
				} else if (requestName.equals("listSystemPrinters")) {
					if (WebInterface.validateSession(request.getSession(), out, false, true)) 
						WebInterface.handleListSystemPrinters(request, out);
				} else {
					out.println("{}");
				}
				
			}
		} else {
			response.setContentType("text/plain;charset=UTF-8");
			out = response.getWriter();
			out.println("Invalid request");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestName;
		PrintWriter out = null;

		if ((requestName = (String) request.getParameter("request")) != null) {
			if (requestName.equals("getInformation")) {
				response.setContentType("text/plain;charset=UTF-8");
				out = response.getWriter();
				ClientInterface.handleGetInformation(request, out);
			} else if (requestName.equals("printPDF")) {
				response.setContentType("text/plain;charset=UTF-8");
				out = response.getWriter();
				ClientInterface.handlePrintPDF(request, out);
			} else {
				// JSON Responses
				response.setContentType("application/json;charset=UTF-8");
				out = response.getWriter();
				if (requestName.equals("acceptPrint")) {
					if (WebInterface.validateSession(request.getSession(), out, false, false)) 
						WebInterface.acceptPrint(request, out);
				} else if (requestName.equals("rejectPrint")) {
					if (WebInterface.validateSession(request.getSession(), out, false, false)) 
						WebInterface.rejectPrint(request, out);
				} else {
					out.println("{}");
				}
			}
		} else {
			response.setContentType("text/plain;charset=UTF-8");
			out = response.getWriter();
			out.println("Invalid request");
		}
	}

}
