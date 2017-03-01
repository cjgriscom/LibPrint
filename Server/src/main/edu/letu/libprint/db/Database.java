package edu.letu.libprint.db;

import java.io.File;
import java.util.function.Consumer;

import com.quirkygaming.errorlib.ErrorHandler;
import com.quirkygaming.propertydb.PropertyDB;
import com.quirkygaming.propertylib.MutableProperty;

import edu.letu.libprint.PropertyDBListener;

public class Database { // Persistent Database Accessor
	
	private static final long CONFIG_VERSION = 1L; 
	
	public static final ErrorHandler<RuntimeException> pdb_handler = 
			PropertyDBListener.pdb_handler;
	
	private static File storageDirectory;
	
	private static MutableProperty<UserList> userList;
	private static MutableProperty<PrinterList> printerList;
	
	private static String storageRoot; // TODO use a more bulletproof solution
	static {
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows")) {
			storageRoot = System.getenv("APPDATA");
		} else {
			storageRoot = System.getenv(System.getProperty("user.home"));
		}
		
	}
	
	private Database() {}
	
	public static void init() { // Called by PropertyDBListener
		// Use APPDATA/LibPrint as data storage
		storageDirectory = new File(storageRoot, ".LibPrint/");
		storageDirectory.mkdirs(); // Make sure it exists
		
		// Initialize the UserList object
		userList = PropertyDB.initiateProperty(
				storageDirectory, "UserList", CONFIG_VERSION,
				new UserList(), pdb_handler);
		
		// Initialize the PrinterList object
		printerList = PropertyDB.initiateProperty(
				storageDirectory, "PrinterList", CONFIG_VERSION,
				new PrinterList(), pdb_handler);
	}
	
	// Use these methods to perform synchronized read/writes
	// Supply the modifier consumer with a lambda expression like (obj) -> {...}
	// ---- ACCESSOR METHODS ----
	
	public static synchronized void accessUserList(Consumer<UserList> accessor, boolean modify) {
		accessor.accept(userList.get());
		if (modify) userList.update(); // Tell PropertyDB to sync the object after modification
	}
	
	public static synchronized void accessPrinterList(Consumer<PrinterList> accessor, boolean modify) {
		accessor.accept(printerList.get());
		if (modify) printerList.update(); // Tell PropertyDB to sync the object after modification
	}
	
}
