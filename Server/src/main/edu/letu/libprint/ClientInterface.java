package edu.letu.libprint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.pdfbox.pdmodel.PDDocument;

import edu.letu.libprint.QueueItem.Status;
import edu.letu.libprint.db.Database;
import edu.letu.libprint.db.PrinterList;
import edu.letu.libprint.db.UndefinedDomainCodeException;

public class ClientInterface {
	public static final ScheduledThreadPoolExecutor fileManagerExecutor = new ScheduledThreadPoolExecutor(5);
	
	private static enum UserType {Patron, Student, Error}
	
	private static File cacheDirectory = new File(Util.getStorageRoot(), "cache");
	static {
		cacheDirectory.mkdirs(); // Ensure it exists
		for (File f : cacheDirectory.listFiles()) f.delete(); // Delete cache on startup
	}
	
	public static String generateSecToken(String username, String computer) throws NoSuchAlgorithmException, UndefinedDomainCodeException {
		return Util.generateSecToken(Database.getDomainCode(), username, computer);
	}
	
	public static boolean validateSecToken(String secToken, String username, String computer) throws NoSuchAlgorithmException, UndefinedDomainCodeException {
		if (secToken == null || username == null
				|| computer == null) {
			return false;
		}
		
		return secToken.equals(generateSecToken(username, computer));
	}
	
	private static UserType validateClient(HttpServletRequest request, PrintWriter out) {
		final String secToken = request.getParameter("secToken");
		final String username = request.getParameter("username");
		final String computer = request.getParameter("computer");
		
		// This check fails if any parameters are null, of the token is invalid
		try {
			if (validateSecToken(secToken, username, computer) == false) {
				return printErrorResponse("The print server could not verify your computer's security information. "
						+ "Ensure your library computer software is up to date.", out);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return printErrorResponse("The print server failed to generate a security token. "
					+ "This is the result of a java misconfiguration on the library's end. "
					+ "Please contact IT for support.", out);
			
		} catch (UndefinedDomainCodeException e) {
			System.err.println("A user tried to print, but no domainCode is defined!");
			return printErrorResponse("The print server has not yet defined a domain code. "
					+ "This is the result of a server misconfiguration on the library's end. "
					+ "Please contact IT for support.", out);
		}
		
		boolean printersOffline = Database.accessPrinterList((printerList) -> {
			return printerList.areAllPrintersOffline();
		}, false);
		
		if (printersOffline) {
			return printErrorResponse("All printers are currently being maintained or are offline. "
					+ "Please try again later.", out);
		}
		
		int maxQueueItems = Database.maxQueueLength();
		
		int maxUserDocs = Database.getMaxDocsPerUser();
		
		int queueSize = PrintQueue.access((printQueue) -> {return printQueue.waitingSize();});
		
		int docs = PrintQueue.access((printQueue) -> {return printQueue.countDocsPerUser(username);});
		
		if (queueSize >= maxQueueItems) {
			return printErrorResponse("The print queue is currently full. Please try again later.", out);
		}
		
		if (docs >= maxUserDocs) {
			return printErrorResponse("You have submitted more than " + maxUserDocs + 
					" documents. Approach the front desk to confirm or cancel your currently pending prints.", out);
		}
		
		// Use the username to distinguish between guests and students
		return username.startsWith("LibraryGuest") ? UserType.Patron : UserType.Student;
	}
	
	public static void handleGetInformation(HttpServletRequest request, PrintWriter out) {
		UserType ut;
		if ((ut = validateClient(request, out)) == UserType.Error) return;
		
		out.println("Response: OK");
		Database.accessPrinterList((printerList) -> {
			int i = 0;
			for (String printerName : printerList.getPrinterNames()) {
				if (printerList.isActive(printerName)) out.println("Printer" + (i++) + ": " + printerName + ", " + getPrinterPriceString(ut, printerName, printerList));
			}
		}, false);
	}
	
	private static double getPrinterPrice(UserType ut, String printerName, PrinterList printerList) {
		return (ut == UserType.Patron // Distinguish proper price return
			? printerList.getPatronPrice(printerName)
			: printerList.getStudentPrice(printerName));
	}

	private static String getPrinterPriceString(UserType ut, String printerName, PrinterList printerList) {
		double priceD = getPrinterPrice(ut, printerName, printerList);
		
		return Util.priceString(priceD, true);
	}

	private static String getTotalPriceString(double pagePrice, int count) {
		double priceD = pagePrice * count;

		return Util.priceString(priceD, false);
	}

	public static void handlePrintPDF(HttpServletRequest request, PrintWriter out) {
		UserType ut;
		if ((ut = validateClient(request, out)) == UserType.Error) return;
		final String username = request.getParameter("username");
		final String computer = request.getParameter("computer");
		
		String printerName;
		if ((printerName = request.getParameter("printerName")) == null) {
			printErrorResponse("Please specify a printer to use and try again.", out);
			return;
		}
		
		boolean validPrinter = Database.accessPrinterList((printerList) -> {
			return printerList.getPrinterNames().contains(printerName) &&
					printerList.isActive(printerName);
		}, false);
		
		if (!validPrinter) {
			printErrorResponse("The specified printer is not available. "
					+ "It may be out of paper or under maintenance.", out);
			return;
		}
		
		File outFile = null;
		
		int count;
		String filename;
		
		try {
			Part filePart = request.getPart("file");
			outFile = savePDF(filePart);
			if (outFile == null || !outFile.exists()) {
				printErrorResponse("An unexpected upload error occured. Please try again.", out);
				return;
			}
			
			// Extract PDF information
			PDDocument doc = PDDocument.load(outFile);
			count = doc.getNumberOfPages();
			filename = doc.getDocumentInformation().getTitle();
			
			doc.close();
			
			if (count <= 0) throw new IOException("Page count <= 0");
		} catch (IOException | ServletException e) {
			e.printStackTrace();
			printErrorResponse("The printed document failed to be sent over the network. Please try again. "
					+ e.getMessage(), out);
			return;
		}
		
		String totalCost = Database.accessPrinterList((printerList) -> {
			return getTotalPriceString(getPrinterPrice(ut, printerName, printerList), count);
		}, false);
		
		final File queueFile = outFile;
		QueueItem q = PrintQueue.access((printQueue) -> {
			return printQueue.add(queueFile, username, printerName, computer, filename, count, totalCost);
		});
		
		// Delete and expire the printer after x minutes
		fileManagerExecutor.schedule(() -> expireQueueItem(q), Database.getDocExpirationTimeMinutes(), TimeUnit.MINUTES);
		
		out.println("Response: OK");
		out.println("TotalCost: " + totalCost);
	}
	
	private static Object expireQueueItem(QueueItem q) {
		q.setStatus(Status.Expired);
		q.getLocation().delete();
		return null;
	}

	private static File savePDF(Part filePart) throws IOException {
		InputStream in = filePart.getInputStream();
		File outFile = new File(cacheDirectory, UUID.randomUUID() + ".pdf");
		
		OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
		Util.copyStream(in, os);
		os.close();
		
		return outFile;
	}
	
	private static UserType printErrorResponse(String error, PrintWriter out) {
		out.println("Response: Error");
		out.println("Error: " + error.replaceAll(":", "--"));
		return UserType.Error;
	}
}
