package edu.letu.libprint.db;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is the serialized class in which printer information
 *   is stored and manipulated.
 * @author chandler
 *
 */
public class PrinterList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// Printer parameters, organized by printer name.
	// I've found this format to be more suitable for serialization
	//      than having a separate Printer class.
	
	private TreeMap<String, Boolean> active = null;
	private TreeMap<String, String> windowsNames = new TreeMap<>();
	private TreeMap<String, Double> patronPrices = new TreeMap<>();
	private TreeMap<String, Double> studentPrices = new TreeMap<>();
	
	private Integer maxQueueLength = null;
	private Integer maxUserDocs = null;
	
	private void init() {
		if (active == null) active = new TreeMap<>();
		if (windowsNames == null) windowsNames = new TreeMap<>();
		if (patronPrices == null) patronPrices = new TreeMap<>();
		if (studentPrices == null) studentPrices = new TreeMap<>();
		if (maxQueueLength == null) maxQueueLength = 30; // Default
		if (maxUserDocs == null) maxUserDocs = 3; // Default
	}
	
	/**
	 * Default constructor. Call init to verify starting state
	 */
	public PrinterList() {
		init();
	}
	
	/**
	 * Called upon deserialization. In this case we just want to call init() to
	 *   verify that the object is in a valid starting state.
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		init();
	}
	
	
	/**
	 * 
	 * @return the number of printers in this list
	 */
	public int size() {
		return active.size();
	}
	
	/**
	 * 
	 * @return A list of printers by their visible title
	 */
	public Set<String> getPrinterNames() {
		return active.keySet();
	}
	
	/**
	 * Add a new printer
	 * @param printerName The listed title of the printer
	 * @param windowsName The resolvable name of the physical printer
	 * @param active
	 * @param patronPrice A double representing the dollar amount per page
	 * @param studentPrice A double representing the dollar amount per page
	 */
	public void addPrinter(String printerName, String windowsName, boolean active, double patronPrice, double studentPrice) {
		this.windowsNames.put(printerName, windowsName);
		setActive(printerName, active);
		setPrices(printerName, patronPrice, studentPrice);
	}
	
	/**
	 * Completely remove a printer from this list
	 * @param printerName The listed title of the printer
	 */
	public void removePrinter(String printerName) {
		windowsNames.remove(printerName);
		active.remove(printerName);
		patronPrices.remove(printerName);
		studentPrices.remove(printerName);
	}
	
	/**
	 * Activate / deactivate the printer
	 * @param printerName The listed title of the printer
	 * @param active
	 */
	public void setActive(String printerName, boolean active) {
		this.active.put(printerName, active);
	}
	
	/**
	 * Set the listed prices
	 * @param printerName The listed title of the printer
	 * @param patronPrice A double representing the dollar amount per page
	 * @param studentPrice A double representing the dollar amount per page
	 */
	public void setPrices(String printerName, double patronPrice, double studentPrice) {
		this.patronPrices.put(printerName, patronPrice);
		this.studentPrices.put(printerName, studentPrice);
	}
	
	/**
	 * Check if the list contains the given printer
	 * @param printerName
	 * @return
	 */
	public boolean printerExists(String printerName) {
		return getPrinterNames().contains(printerName);
	}
	
	/**
	 * Returns an integer ID suitable for URL parameters
	 * @param printerName
	 * @return
	 */
	public int getPrinterID(String printerName) {
		return Math.abs(printerName.hashCode());
	}
	
	/**
	 * Translate the integer ID to a printer name.  Returns null if invalid.
	 * @param printerID from getPrinterID
	 * @return
	 */
	public String getPrinterByID(String printerID) {
		for (String name : getPrinterNames()) {
			if (printerID.equals(getPrinterID(name)+"")) {
				return name;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param printerName The listed title of the printer
	 * @return Whether the printer is active or not
	 */
	public boolean isActive(String printerName) {
		return active.get(printerName);
	}
	
	/**
	 * 
	 * @param printerName The listed title of the printer
	 * @return The resolvable name of the physical printer
	 */
	public String getWindowsPrinterName(String printerName) {
		return windowsNames.get(printerName);
	}
	
	/**
	 * 
	 * @param printerName The listed title of the printer
	 * @return The price of the printer for public library users
	 */
	public double getPatronPrice(String printerName) {
		return patronPrices.get(printerName);
	}
	
	/**
	 * 
	 * @param printerName The listed title of the printer
	 * @return The price of the printer for students
	 */
	public double getStudentPrice(String printerName) {
		return studentPrices.get(printerName);
	}
	
	/**
	 * 
	 * @return The configurable maximum length of the queue
	 */
	public int getMaxQueueLength() {
		return maxQueueLength;
	}
	
	/**
	 * 
	 * @return The configurable maximum documents that may be submitted by one user at a time
	 */
	public int getMaxDocsPerUser() {
		return maxUserDocs;
	}
	
	/**
	 * Checks for active printers
	 * @return True if there are no active printers
	 */
	public boolean areAllPrintersOffline() {
		boolean offline = true;
		for (boolean a : active.values()) if (a) offline = false; // Found an active printer
		return offline;
	}
}
