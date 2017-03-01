package edu.letu.libprint.db;

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
	
	private final TreeMap<String, Boolean> active = new TreeMap<>();
	private final TreeMap<String, String> windowsNames = new TreeMap<>();
	private final TreeMap<String, Double> patronPrices = new TreeMap<>();
	private final TreeMap<String, Double> studentPrices = new TreeMap<>();

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
}
