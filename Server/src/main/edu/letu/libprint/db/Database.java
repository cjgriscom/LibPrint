package edu.letu.libprint.db;

import java.util.function.Consumer;
import java.util.function.Function;

import com.quirkygaming.errorlib.ErrorHandler;
import com.quirkygaming.propertydb.PropertyDB;
import com.quirkygaming.propertylib.MutableProperty;

import edu.letu.libprint.PropertyDBListener;
import edu.letu.libprint.Util;

public class Database { // Persistent Database Accessor
	
	private static final long CONFIG_VERSION = 1L; 
	
	public static final ErrorHandler<RuntimeException> pdb_handler = 
			PropertyDBListener.pdb_handler;
	
	private static MutableProperty<UserList> userList;
	private static MutableProperty<PrinterList> printerList;
	
	private Database() {}
	
	/**
	 * Initializes the database
	 * Called by PropertyDBListener
	 */
	public static void init() {
		// Initialize the UserList object
		userList = PropertyDB.initiateProperty(
				Util.getStorageRoot(), "UserList", CONFIG_VERSION,
				new UserList(), pdb_handler);
		
		// Initialize the PrinterList object
		printerList = PropertyDB.initiateProperty(
				Util.getStorageRoot(), "PrinterList", CONFIG_VERSION,
				new PrinterList(), pdb_handler);
	}
	
	/**
	 * Synchronized accessor method for the database's UserList
	 * Supply the accessor consumer with 
	 * @param accessor A lambda expression like (userList) -> {...}
	 * @param modify Set to true if the accessor modifies the UserList. 
	 */
	public static void accessUserList(Consumer<UserList> accessor, boolean modify) {
		accessUserList((userList) -> {
			accessor.accept(userList);
			return Void.TYPE;
		}, modify);
	}

	/**
	 * Synchronized accessor method for the database's PrinterList
	 * Supply the accessor consumer with 
	 * @param accessor A lambda expression like (printerList) -> {...}
	 * @param modify Set to true if the accessor modifies the PrinterList. 
	 */
	public static void accessPrinterList(Consumer<PrinterList> accessor, boolean modify) {
		accessPrinterList((printerList) -> {
			accessor.accept(printerList);
			return Void.TYPE;
		}, modify);
	}

	/**
	 * Synchronized accessor method for the database's UserList
	 * Supply the accessor function with 
	 * @param accessor A lambda expression like (userList) -> {...return result;}
	 * @param modify Set to true if the accessor modifies the UserList. 
	 */
	public static <T> T accessUserList(Function<UserList, T> accessor, boolean modify) {
		T ret;
		synchronized(userList) { // Synchronize as per PropertyDB specifications
			ret = accessor.apply(userList.get());
		}
		if (modify) userList.update(); // Tell PropertyDB to sync the object after modification
		return ret;
	}

	/**
	 * Synchronized accessor method for the database's PrinterList
	 * Supply the accessor function with 
	 * @param accessor A lambda expression like (printerList) -> {...return result;}
	 * @param modify Set to true if the accessor modifies the PrinterList. 
	 */
	public static synchronized <T> T accessPrinterList(Function<PrinterList, T> accessor, boolean modify) {
		T ret;
		synchronized(printerList) {
			ret = accessor.apply(printerList.get());
		}
		if (modify) printerList.update(); // Tell PropertyDB to sync the object after modification
		return ret;
	}
}
