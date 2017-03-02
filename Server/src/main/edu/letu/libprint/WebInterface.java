package edu.letu.libprint;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

public class WebInterface {
	public static void handleListQueue(HttpServletRequest request, PrintWriter out) {
		// TODO 
		PrintQueue.access((printerList) -> {printerList.queueJSON(out);});
	}
}
