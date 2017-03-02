package edu.letu.libprint;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

public class WebInterface {
	public static void handleListQueue(HttpServletRequest request, PrintWriter out) {
		PrintQueue.access((printerList) -> {printerList.queueJSON(out);});
	}

	public static void handleListSystemPrinters(HttpServletRequest request, PrintWriter out) {
		out.println("{\"systemPrinters\": [");
		boolean printComma = false;
		for (String printer : PrintDispatch.listSystemPrinters()) {
			out.println((printComma ? "," : "") + "\"" + printer + "\"");
			printComma = true;
		}
		out.println("]}");
	}
}
