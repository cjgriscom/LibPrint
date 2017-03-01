package edu.letu.libprint;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.quirkygaming.errorlib.ErrorHandler;
import com.quirkygaming.propertydb.InitializationToken;
import com.quirkygaming.propertydb.PropertyDB;

import edu.letu.libprint.db.Database;

/**
 * Application Lifecycle Listener implementation class PropertyDBListener
 *
 */
@WebListener
public class PropertyDBListener implements ServletContextListener {
	private static final int PERIOD = 1000; // Sync every second
	
	public static InitializationToken token = null;
	
	// For testing purposes, throw all I/O exceptions.
	// This may need to be changed for a formal release.
	// ErrorHandler.logAll may be used to direct errors to an arbitrary stream.
	public static final ErrorHandler<RuntimeException> pdb_handler = ErrorHandler.throwAll();
	
	// This runs once on startup; initialize PropertyDB
	public void contextInitialized(ServletContextEvent c) {
		if (PropertyDB.initialized()) {
			throw new IllegalStateException("PropertyDB has already been initialized!");
		} else {
			// Initialize DB with given I/O-cycle time
			token = PropertyDB.initializeDB(PERIOD);
			Database.init();
		}
	}
	
	// When server is closing, exit PropertyDB (ensures everything is cleaned up)
	public void contextDestroyed(ServletContextEvent c) {
		if (token != null) {
			PropertyDB.closeDatabase(token);
		}
	}
	
}
