package edu.letu.libprint.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import edu.letu.libprint.Util;

public class ClientUploadTest extends TestClass {
	
	public boolean testUpload(PrintWriter out) throws Exception {
		InputStream is = TestServlet.instance.getServletContext().getResourceAsStream("WEB-INF/TestPrint.pdf");
		String param = "?request=printPDF&secToken=temp&username=chan&computer=S1&printerName=Color";
		String type = "application/pdf";
		URL u = new URL("http://localhost:8080/LibPrint/RequestHandler" + param);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", type);
		conn.setRequestProperty("Content-Length", String.valueOf(39762));
		OutputStream os = conn.getOutputStream();
		Util.copyStream(is, os);
		Scanner s = new Scanner(conn.getInputStream());
		while (s.hasNextLine()) out.println(s.nextLine());
		s.close();
		conn.disconnect();
		return true; // TODO finish test
	}
}
