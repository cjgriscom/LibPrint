package edu.letu.libprint;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.printing.PDFPageable;

public class PrintDispatch {

	/**
	 * Prints a PDF document to a system printer
	 * @param pdf
	 * @param systemPrinter
	 * @throws PrinterException
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public static void printDocument(File pdf, PrintService printService) throws PrinterException, InvalidPasswordException, IOException {
		PDDocument document = PDDocument.load(pdf);
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));
		job.setPrintService(printService);
		job.print();
	}
	
	/**
	 * Get a list of all the registered printers on this computer
	 * @return
	 */
	public static String[] listSystemPrinters() {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		String[] printers = new String[printServices.length];
		for (int i = 0; i < printServices.length; i++) {
			printers[i] = printServices[i].getName().trim();
		}
		return printers;
	}
	
	/**
	 * Use this to resolve the system printer. 
	 * @param systemPrinter The name of the system printer 
	 * @return The printer object, or null if it does not exist
	 */
	public static PrintService findPrintService(String systemPrinter) {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		for (PrintService printService : printServices) {

			if (printService.getName().trim().equals(systemPrinter)) {
				return printService;
			}
		}
		return null;
	}
}