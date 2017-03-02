package edu.letu.libprint;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.letu.libprint.QueueItem.Status;

public class PrintQueue {
	public List<QueueItem> queue = new LinkedList<QueueItem>();
	private static final PrintQueue INSTANCE = new PrintQueue();
	
	private PrintQueue() {
		queue.add(new QueueItem(null, "chandlergriscom", "Black and White", "S1", "example.doc", 5, "Free"));
		queue.add(new QueueItem(null, "LibraryGuest1", "Color", "S3", "example.doc", 1, "$0.50"));
		queue.add(new QueueItem(null, "LibraryGuest1", "Black and White", "S3", "example2.doc", 1, "$0.10"));
		queue.add(new QueueItem(null, "LibraryGuest1", "Color", "S3", "example3.doc", 3, "$1.50"));
		
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
	
	public void add(QueueItem item) {
		queue.add(item);
	}
	
	public int waitingSize() {
		int i = 0;
		for (QueueItem q : queue) {
			if (q.status() == Status.Waiting) i++;
		}
		return i;
	}
	
	public int countDocsPerUser(String username) {
		int i = 0;
		for (QueueItem q : queue) {
			if (q.status() != Status.Waiting) continue;
			if (q.username().equals(username)) i++;
		}
		return i;
	}
	
	public void queueJSON(PrintWriter stream) {
		stream.println("{");
		stream.println("\"queue\": [");
		boolean printComma = false;
		for (QueueItem i : queue) {
			// Only include waiting items
			if (i.status() == QueueItem.Status.Waiting) stream.println((printComma ? "," : "") + i.asJSON());
			printComma = true;
		}
		stream.println("]");
		stream.println("}");
	}
	
	public void historyJSON(PrintWriter stream) {
		stream.println("{");
		stream.println("\"history\": [");
		boolean printComma = false;
		for (QueueItem i : queue) {
			// Only include non-waiting items
			if (i.status() != QueueItem.Status.Waiting) stream.println((printComma ? "," : "") + i.asJSON());
			printComma = true;
		}
		stream.println("]");
		stream.println("}");
	}
}
