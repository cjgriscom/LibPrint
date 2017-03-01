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
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		TestWriter testWriter = new TestWriter(writer);
		writer.println("<html><head><title>LibPrint Test Servlet</title></head><body><p>");
		
		writer.println("</p></body></html>");
		
	}

}

class TestWriter {
	PrintWriter out;
	TestWriter(PrintWriter out) {
		this.out = out;
	}
	
	public void println(String data) {
		out.println(data + "<br/>");
	}
}
