package edu.letu.libprint;

import java.io.PrintWriter;

import javax.print.PrintService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.quirkygaming.propertylib.MutableProperty;

import edu.letu.libprint.db.Database;

public class WebInterface {
	public static void handleListQueue(HttpServletRequest request, PrintWriter out) {
		PrintQueue.access((printerList) -> {printerList.queueJSON(out);});
	}
	
	public static void handleListHistory(HttpServletRequest request, PrintWriter out) {
		PrintQueue.access((printerList) -> {printerList.historyJSON(out);});
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

	public static String getSystemPrintersOptionList() {
		StringBuilder sb = new StringBuilder();
		for (String printer : PrintDispatch.listSystemPrinters()) {
			sb.append("<option value=\"");
			sb.append(printer);
			sb.append("\">");
			sb.append(printer);
			sb.append("</option>\n");
		}
		return sb.toString();
	}

	public static void acceptPrint(HttpServletRequest request, PrintWriter out) {
		final int ID = resolveID(request);
		
		if (ID == -1) {
			printJsonMessage(out, "Specify a valid ID.", true);
			return;
		}
		
		PrintQueue.access((printQueue) -> {
			if (printQueue.containsID(ID)) {
				QueueItem item = printQueue.get(ID);
				if (item.status() == QueueItem.Status.Waiting) {
					// Convert printer name
					String systemPrinter = Database.accessPrinterList((printerList) -> {
						return printerList.getWindowsPrinterName(item.getPrinterName());
					}, false);
					
					
					if (systemPrinter != null) {
						PrintService printer = PrintDispatch.findPrintService(systemPrinter);
						if (printer != null) {
							try {
								PrintDispatch.printDocument(item.getLocation(), printer);
								printJsonMessage(out, "The item was sent to the printer.", false);
							} catch (Exception e) {
								e.printStackTrace();
								printJsonMessage(out, "An unexpected error occured while dispatching the print: " + e.getClass() + " " + e.getMessage(), true);
							}
							item.setStatus(QueueItem.Status.Printed);
						} else {
							printJsonMessage(out, "The system printer could not be found. Make sure the printer is online and the printer list is configured properly.", true);
							item.setStatus(QueueItem.Status.Error); // TODO we dont want this in production
						}
					} else {
						printJsonMessage(out, "The referenced printer does not exist in the server configuration. Make sure the printer list is configured properly.", true);
						item.setStatus(QueueItem.Status.Error); // TODO we dont want this in production
					}
				} else {
					String status = item.status().name().toLowerCase();
					if (status.equals("error")) status = "removed";
					printJsonMessage(out, "This item has already been " + status + ".", true);
				}
			} else {
				printJsonMessage(out, "The queue does not contain this ID.", true);
			}
		});
		
	}
	
	public static void rejectPrint(HttpServletRequest request, PrintWriter out) {
		final int ID = resolveID(request);
		
		if (ID == -1) {
			printJsonMessage(out, "Specify a valid ID.", true);
			return;
		}
		
		PrintQueue.access((printQueue) -> {
			if (printQueue.containsID(ID)) {
				QueueItem item = printQueue.get(ID);
				if (item.status() == QueueItem.Status.Waiting) {
					item.setStatus(QueueItem.Status.Canceled);
					printJsonMessage(out, "The item was canceled successfully.", false);
				} else {
					String status = item.status().name().toLowerCase();
					printJsonMessage(out, "This item has already been " + status + ".", true);
				}
			} else {
				printJsonMessage(out, "The queue does not contain this ID.", true);
			}
		});
	}

	public static void addPrinter(HttpServletRequest request, PrintWriter out) {
		//Database.accessPrinterList(accessor, modify);
		// TODO stub
		
	}

	public static void removePrinter(HttpServletRequest request, PrintWriter out) {
		// TODO Auto-generated method stub
		
	}
	
	private static int resolveID(HttpServletRequest request) {
		String IDString = request.getParameter("ID");
		
		int ID; // Cast to integer
		try {
			ID = (IDString == null ? -1 : Integer.parseUnsignedInt(IDString));
		} catch (NumberFormatException e) {
			ID = -1;
		}
		return ID;
	}
	
	public static boolean sessionValid(HttpSession session) {
		if (session != null && session.getAttribute("user") != null) {
			
			return true; // TODO validate user
		} else {
			return false;
		}
	}
	
	public static boolean validateJSPSession(HttpSession session, MutableProperty<String> error, boolean reqUserPerms, boolean reqPrintPerms) {
		if (sessionValid(session)) {
			String user = (String) session.getAttribute("user");
			Database.accessUserList((ul) -> {
				if (ul.userExists(user)) {
					if (ul.hasUserAccess(user) || !reqUserPerms) {
						if (!ul.hasPrinterAccess(user) || !reqPrintPerms) {
							error.set(""); // All good
						} else {
							error.set("You do not have permission to edit printers.");
						}
					} else {
						error.set("You do not have permission to edit users.");
					}
				} else {
					error.set("User " + user + " does not exist!");
				}
			}, false);
			return error.equals(""); // True if no error
		} else {
			error.set("You are not logged in.");
			return false;
		}
	}
	
	public static boolean validateSession(HttpSession session, PrintWriter out, boolean reqUserPerms, boolean reqPrintPerms) {
		MutableProperty<String> error = MutableProperty.newProperty(null);
		boolean success = validateJSPSession(session, error, reqUserPerms, reqPrintPerms);
		if (!success) {
			printJsonMessage(out, error.get(), true);
		}
		return success;
	}
	
	private static void printJsonMessage(PrintWriter out, String message, boolean error) {
		out.println("{"
				+ "\"status\": \"" + (error ? "error" : "OK") + "\","
				+ "\"message\": \"" + message + "\""
				+ "}");
	}
}
