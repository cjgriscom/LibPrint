package edu.letu.libprint;

import java.io.File;
import java.util.Date;

public class QueueItem {
	public static enum Status {
		Waiting, Printed, Canceled
	}
	
	private Status status = Status.Waiting;
	private Date time = new Date();
	private String username;
	private String printerName;
	private String filename;
	private String computer;
	private int pages;
	private String total;
	private File location;
	
	public QueueItem(File location, String username, String printerName, String computer, String filename, int pages, String totalCost) {
		this.username = username;
		this.printerName = printerName;
		this.computer = computer;
		this.filename = filename;
		this.pages = pages;
		this.total = totalCost;
	}
	
	public Status status() {return status;}
	
	@SuppressWarnings("deprecation")
	public String asJSON() {
		return "{" +
				"\"time\":\"" + time.getHours() + ":" + time.getMinutes() + "\",\n" +// TODO use better API
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
}