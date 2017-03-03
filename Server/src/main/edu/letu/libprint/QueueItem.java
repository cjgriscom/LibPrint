package edu.letu.libprint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueueItem {
	public static enum Status {
		Waiting, Printed, Canceled
	}
	
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
	
	private Status status = Status.Waiting;
	private Date time = new Date();
	private String username;
	private String printerName;
	private String filename;
	private String computer;
	private int pages;
	private String total;
	private File location;
	private int ID;
	
	QueueItem(File location, String username, String printerName, String computer, String filename, int pages, String totalCost, int ID) {
		this.username = username;
		this.printerName = printerName;
		this.computer = computer;
		this.filename = filename;
		this.pages = pages;
		this.total = totalCost;
		this.ID = ID;
	}
	
	public Status status() {return status;}
	
	public String asJSON() {
		return "{" +
				"\"ID\":" + ID + ",\n" +
				"\"time\":\"" + TIME_FORMAT.format(time) + "\",\n" +
				"\"status\":\"" + status.name() + "\",\n" +
				"\"username\":\"" + username + "\",\n" +
				"\"printerName\":\"" + printerName + "\",\n" +
				"\"filename\":\"" + filename + "\",\n" +
				"\"computer\":\"" + computer + "\",\n" +
				"\"pages\":" + pages + ",\n" +
				"\"totalCost\":\"" + total + "\"\n" +
				"}";
	}

	public String username() {
		return username;
	}
	
	public File getLocation() {
		return location;
	}
	
	public String getPrinterName() {
		return this.printerName;
	}
	
	/**
	 * Change the status of this print (i.e. move from Waiting to Canceled or Printed)
	 * @param status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
}