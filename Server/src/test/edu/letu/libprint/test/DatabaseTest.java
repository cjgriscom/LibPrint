package edu.letu.libprint.test;

import java.io.PrintWriter;

import com.quirkygaming.propertydb.PropertyDB;
import com.quirkygaming.propertylib.MutableProperty;

import edu.letu.libprint.PropertyDBListener;
import edu.letu.libprint.db.Database;
import edu.letu.libprint.db.UserList.AccessLevel;

public class DatabaseTest extends TestClass {
	boolean addListAndRemoveUser(final PrintWriter out) {
		MutableProperty<Boolean> success = MutableProperty.newProperty(false);
		Database.accessUserList((userList) -> {
			userList.addUser("listusertest", "password".toCharArray());
			out.println("Added listusertest");
			for (String username : userList.getUsernames()) {
				if (username.equals("listusertest")) success.set(true); // Found the test user
				String output = username + ":" + 
						" p." + userList.hasPrinterAccess(username) + 
						" u." + userList.hasUserAccess(username);
				out.println(output);
			};
			userList.removeUser("listusertest");
			out.println("Removed listusertest");
			for (String username : userList.getUsernames()) {
				if (username.equals("listusertest")) success.set(false); // Should not find the test user
				String output = username + ":" + 
						" p." + userList.hasPrinterAccess(username) + 
						" u." + userList.hasUserAccess(username);
				out.println(output);
			};
		}, true);
		return success.get();
	}
	
	boolean passwordTest(final PrintWriter out) {
		MutableProperty<Boolean> success = MutableProperty.newProperty(true);
		Database.accessUserList((userList) -> {
			userList.addUser("pwtest", "RealPassword".toCharArray());
			boolean correctPasswordMatches = userList.passwordMatches("pwtest", "RealPassword".toCharArray());
			boolean incorrectPasswordMatches = userList.passwordMatches("pwtest", "FakePassword".toCharArray());
			out.println("correctPasswordMatches:   " + correctPasswordMatches);
			out.println("incorrectPasswordMatches: " + incorrectPasswordMatches);
			if (!correctPasswordMatches || incorrectPasswordMatches) success.set(false);
			userList.removeUser("pwtest");
		}, true);
		return success.get();
	}
	
	boolean retentionTest(final PrintWriter out) {
		MutableProperty<Boolean> userSuccess = MutableProperty.newProperty(false);
		MutableProperty<Boolean> printerSuccess = MutableProperty.newProperty(false);

		out.println("Closing database");
		PropertyDB.closeDatabase(PropertyDBListener.token);
		
		out.println("Initializing database at 60 seconds");
		PropertyDBListener.token = PropertyDB.initializeDB(60000);
		Database.init();
		
		out.println("Adding user: retentiontest");
		Database.accessUserList((userList) -> {
			userList.addUser("retentiontest", "testPWD".toCharArray());
			userList.setAccessPolicies("retentiontest", AccessLevel.Printer_Manager);
		}, true);
		
		out.println("Adding printer: retentiontest");
		Database.accessPrinterList((printerList) -> {
			printerList.addPrinter("retentiontest", 
					"win_retentiontest", false, 0.75, 0.10);
		}, true);
		
		out.println("Closing database");
		PropertyDB.closeDatabase(PropertyDBListener.token);
		
		out.println("Initializing database at default period");
		PropertyDBListener.token = PropertyDB.initializeDB(PropertyDBListener.PERIOD);
		Database.init();

		out.println("Checking for user: retentiontest");
		Database.accessUserList((userList) -> {
			for (String username : userList.getUsernames()) {
				out.println(username);
				if (username.equals("retentiontest")) {
					if (userList.hasPrinterAccess("retentiontest") == true && userList.hasUserAccess("retentiontest") == false) {
						// Conditions match
						out.println("Found user with correct access parameters");
						userSuccess.set(true);
					} else {
						out.println("User has incorrect access parameters");
					}
				}
			}
			out.println("Removing user");
			userList.removeUser("retentiontest");
		}, true);
		
		out.println("Checking for printer: retentiontest");
		Database.accessPrinterList((printerList) -> {
			for (String printerName : printerList.getPrinterNames()) {
				out.println(printerName);
				if (printerName.equals("retentiontest")) {
					if (printerList.isActive("retentiontest") == false && 
							printerList.getWindowsPrinterName("retentiontest").equals("win_retentiontest") &&
							printerList.getPatronPrice("retentiontest") == 0.75 && 
							printerList.getStudentPrice("retentiontest") == 0.10) {
						// Conditions match
						out.println("Found printer with correct attributes");
						printerSuccess.set(true);
					} else {
						out.println("Printer has incorrect attributes");
					}
				}
			}
			out.println("Removing printer");
			printerList.removePrinter("retentiontest");
		}, true);
		return userSuccess.get() && printerSuccess.get();
	}
}
