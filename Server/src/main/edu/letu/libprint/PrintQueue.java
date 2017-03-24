package edu.letu.libprint;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.letu.libprint.QueueItem.Status;

public class PrintQueue {
	public ArrayList<QueueItem> queue = new ArrayList<QueueItem>();
	private static final PrintQueue INSTANCE = new PrintQueue();
	
	private PrintQueue() {
		// TODO remove examples
		add(null, "chandlergriscom", "Black and White", "S1", "example.doc", 5, "Free");
		add(null, "LibraryGuest1", "Color", "S3", "example.doc", 1, "$0.50");
		add(null, "LibraryGuest1", "Black and White", "S3", "example2.doc", 1, "$0.10");
		add(null, "LibraryGuest1", "Color", "S3", "example3.doc", 3, "$1.50");
	}
	

	/**
	 * Synchronized accessor method for this singleton
	 * Supply the accessor consumer with 
	 * @param accessor A lambda expression like (printQueue) -> {...}
	 */
	public static void access(Consumer<PrintQueue> accessor) {
		access((printQueue) -> {
			accessor.accept(printQueue);
			return Void.TYPE;
		});
	}

	/**
	 * Synchronized accessor method for this singleton
	 * Supply the accessor function with 
	 * @param accessor A lambda expression like (printQueue) -> {...return result;}
	 */
	public static synchronized <T> T access(Function<PrintQueue, T> accessor) {
		T ret = accessor.apply(INSTANCE);
		return ret;
	}
	
	/**
	 * Add a new QueueItem
	 * @param location
	 * @param username
	 * @param printerName
	 * @param computer
	 * @param filename
	 * @param pages
	 * @param totalCost
	 */
	public void add(File location, String username, String printerName, String computer, String filename, int pages, String totalCost) {
		if (filename == null) filename = "untitled";
		queue.add(new QueueItem(location, username, printerName, computer, filename, pages, totalCost, queue.size()));
	}
	
	/**
	 * Retrieve an item from the queue (warning no error checking!!)
	 * Use containsID to check for a valid ID
	 * @param ID
	 * @return
	 */
	public QueueItem get(int ID) {
		return queue.get(ID);
	}
	
	/**
	 * Return the number of waiting elements in the queue
	 * @return
	 */
	public int waitingSize() {
		int i = 0;
		for (QueueItem q : queue) {
			if (q.status() == Status.Waiting) i++;
		}
		return i;
	}
	
	/**
	 * Return the number of active documents in the queue submitted by a specific user 
	 * @param username
	 * @return
	 */
	public int countDocsPerUser(String username) {
		int i = 0;
		for (QueueItem q : queue) {
			if (q.status() != Status.Waiting) continue;
			if (q.username().equals(username)) i++;
		}
		return i;
	}
	
	/**
	 * Print the entire queue in JSON format
	 * @param stream
	 */
	public void queueJSON(PrintWriter stream) {
		stream.println("{");
		stream.println("\"queue\": [");
		boolean printComma = false;
		for (QueueItem i : queue) {
			// Only include waiting items
			if (i.status() == QueueItem.Status.Waiting) {
				stream.println((printComma ? "," : "") + i.asJSON());
				printComma = true;
			}
			
		}
		stream.println("]");
		stream.println("}");
	}
	
	/**
	 * Print the JSON history list
	 * @param stream
	 */
	public void historyJSON(PrintWriter stream) {
		stream.println("{");
		stream.println("\"history\": [");
		boolean printComma = false;
		for (QueueItem i : queue) {
			// Only include non-waiting items
			if (i.status() != QueueItem.Status.Waiting) {
				stream.println((printComma ? "," : "") + i.asJSON());
				printComma = true;
			}
			
		}
		stream.println("]");
		stream.println("}");
	}

	/**
	 * Return true if the ID exists within this queue
	 * @param ID
	 * @return
	 */
	public boolean containsID(int ID) {
		return ID < queue.size();
	}
}
