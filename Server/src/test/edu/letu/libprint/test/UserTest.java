package edu.letu.libprint.test;

import java.io.PrintWriter;

import com.quirkygaming.propertydb.PropertyDB;
import com.quirkygaming.propertylib.MutableProperty;

import edu.letu.libprint.PropertyDBListener;
import edu.letu.libprint.db.Database;

public class UserTest extends TestClass {
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
		MutableProperty<Boolean> success = MutableProperty.newProperty(false);
		
		out.println("Closing database");
		PropertyDB.closeDatabase(PropertyDBListener.token);
		
		out.println("Initializing database at 60 seconds");
		PropertyDBListener.token = PropertyDB.initializeDB(60000);
		Database.init();
		
		out.println("Adding user: retentiontest");
		Database.accessUserList((userList) -> {
			userList.addUser("retentiontest", "testPWD".toCharArray());
			userList.setAccessPolicies("retentiontest", true, false);
		}, true);
		
		out.println("Closing database");
		PropertyDB.closeDatabase(PropertyDBListener.token);
		
		out.println("Initializing database at default period");
		PropertyDBListener.token = PropertyDB.initializeDB(PropertyDBListener.PERIOD);
		Database.init();

		out.println("Checkign for user: retentiontest");
		Database.accessUserList((userList) -> {
			for (String username : userList.getUsernames()) {
				out.println(username);
				if (username.equals("retentiontest")) {
					if (userList.hasPrinterAccess("retentiontest") == true && userList.hasUserAccess("retentiontest") == false) {
						// Conditions match
						out.println("Found user with correct access parameters");
						success.set(true);
					} else {
						out.println("User has incorrect access parameters");
					}
				}
			}
			userList.removeUser("retentiontest");
		}, true);
		return success.get();
	}
}
