package edu.letu.libprint.db;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;

import edu.letu.libprint.Util;

/**
 * This is the serialized class in which user data is stored
 *   and manipulated.
 * @author chandler
 *
 */
public class UserList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// User parameters, organized by username.
	// I've found this format to be more suitable for serialization
	//      than having a separate User class.
	
	private TreeMap<String, String> passwordHashes = null;
	private TreeMap<String, Boolean> printerAccess = null;
	private TreeMap<String, Boolean> userAccess = null;
	
	private void init() {
		if (passwordHashes == null) passwordHashes = new TreeMap<>();
		if (printerAccess == null) printerAccess = new TreeMap<>();
		if (userAccess == null) userAccess = new TreeMap<>();
		
		this.addUser("chandlergriscom", "chandlergriscom".toCharArray()); // TODO remove
		this.addUser("micahrichards", "micahrichards".toCharArray());
	}
	
	/**
	 * Default constructor. Call init to verify starting state
	 */
	public UserList() {
		init();
	}
	
	/**
	 * Called upon deserialization. In this case we just want to call init() to
	 *   verify that the object is in a valid starting state.
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		init();
	}
	
	/**
	 * 
	 * @return the number of users in this list
	 */
	public int size() {
		return passwordHashes.size();
	}
	
	/**
	 * Add a new user with default access policies
	 * @param username A unique username
	 * @param password Plaintext password (will be hashed)
	 */
	public void addUser(String username, char[] password) {
		passwordHashes.put(username, Util.hashPassword(password));
		printerAccess.put(username, false);
		userAccess.put(username, false);
	}
	
	/**
	 * Completely remove a user from this list
	 * @param username
	 */
	public void removeUser(String username) {
		passwordHashes.remove(username);
		printerAccess.remove(username);
		userAccess.remove(username);
	}
	
	/**
	 * 
	 * @return A set of usernames
	 */
	public Set<String> getUsernames() {
		return passwordHashes.keySet();
	}
	
	/**
	 * Use to change the user's password
	 * @param username
	 * @param password A plaintext password
	 */
	public void setPassword(String username, char[] password) {
		passwordHashes.put(username, Util.hashPassword(password));
	}
	
	/**
	 * Use to change the user's password
	 * @param username
	 * @param password A plaintext password
	 */
	public void setAccessPolicies(String username, boolean printerAccess, boolean userAccess) {
		this.printerAccess.put(username, printerAccess);
		this.userAccess.put(username, userAccess);
	}
	
	/**
	 * 
	 * @param username
	 * @return Whether the user exists or not
	 */
	public boolean userExists(String username) {
		return passwordHashes.containsKey(username);
	}
	
	/**
	 * Checks if the given password is correct for a specific user
	 * @param username
	 * @param password The plaintext password
	 * @return True if the password matches
	 */
	public boolean passwordMatches(String username, char[] password) {
		return Util.verifyPassword(password, passwordHashes.get(username));
	}
	
	/**
	 * 
	 * @param username
	 * @return Whether or not the user can modify printer policies
	 */
	public boolean hasPrinterAccess(String username) {
		return printerAccess.get(username);
	}
	
	/**
	 * 
	 * @param username
	 * @return Whether or not the user can modify user policies
	 */
	public boolean hasUserAccess(String username) {
		return userAccess.get(username);
	}
}
