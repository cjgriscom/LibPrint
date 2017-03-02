package edu.letu.libprint.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static TestServlet instance = null;

	public TestServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		instance = this;
		PrintWriter writer = response.getWriter();
		writer.println("<html><head><title>LibPrint Test Servlet</title></head><body><pre>");
		new DatabaseTest().performTests(writer);
		new ClientUploadTest().performTests(writer);
		writer.println("</pre></body></html>");

	}

}
