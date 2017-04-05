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
	private static MutableProperty<String> domainCode;
	private static MutableProperty<Integer> maxDocsPerUser;
	private static MutableProperty<Integer> maxQueueLength;
	private static MutableProperty<Long> sessionExpirationTime;
	
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
		
		// Initialize the domainCode string
		domainCode = PropertyDB.initiateProperty(
				Util.getStorageRoot(), "DomainCode", CONFIG_VERSION,
				null, pdb_handler);
		
		// Initialize the maxDocsPerUser
		maxDocsPerUser = PropertyDB.initiateProperty(
				Util.getStorageRoot(), "MaxDocsPerUser", CONFIG_VERSION,
				3, pdb_handler); // Default to 3
		
		// Initialize the MaxQueueLength
		maxQueueLength = PropertyDB.initiateProperty(
				Util.getStorageRoot(), "MaxQueueLength", CONFIG_VERSION,
				30, pdb_handler); // Default to 30
		
		// Initialize the sessionExpirationTime
		sessionExpirationTime = PropertyDB.initiateProperty(
				Util.getStorageRoot(), "SessionExpirationTime", CONFIG_VERSION,
				1000l*60*60*15, pdb_handler); // Default to 15 hours
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
	
	/**
	 * Domain code is set on initial server configuration
	 * @return
	 */
	public static boolean isDomainCodeSet() {
		return domainCode.get() != null;
	}
	
	/**
	 * Set the domain code (used to synchronize client and server security)
	 * @param newCode
	 */
	public static void setDomainCode(String newCode) {
		domainCode.set(newCode);
		domainCode.update();
	}
	
	/**
	 * 
	 * @param maxQueueLength
	 */
	public static void setMaxQueueLength(int mql) {
		maxQueueLength.set(mql);
		maxQueueLength.update();
	}
	
	/**
	 * 
	 * @param maxDocsPerUser
	 */
	public static void setMaxDocsPerUser(int mdu) {
		maxDocsPerUser.set(mdu);
		maxDocsPerUser.update();
	}
	
	/**
	 * 
	 * @param time
	 */
	public static void setSessionExpirationTimeMillis(long time) {
		sessionExpirationTime.set(time);
		sessionExpirationTime.update();
	}
	
	/**
	 * Get the domain code
	 * @return
	 * @throws UndefinedDomainCodeException if it has not been set yet
	 */
	public static String getDomainCode() throws UndefinedDomainCodeException {
		if (isDomainCodeSet()) {
			return domainCode.get();
		} else {
			throw new UndefinedDomainCodeException();
		}
	}
	
	/**
	 * Get max docs that can be submitted per user
	 * @return
	 */
	public static int getMaxDocsPerUser() {
		// Default to 3
		return maxDocsPerUser.get();
	}

	/**
	 * Get max docs that can be in the queue at once
	 * @return
	 */
	public static int maxQueueLength() {
		// Default to 30
		return maxQueueLength.get();
	}
	
	/**
	 * This could be used to make a configurable expiration time
	 * @return
	 */
	public static long getSessionExpirationTimeMillis() {
		// Default to 15 hours
		return sessionExpirationTime.get();
	}
}
