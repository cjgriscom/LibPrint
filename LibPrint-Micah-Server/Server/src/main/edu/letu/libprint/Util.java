package edu.letu.libprint;

import com.stackoverflow.erickson.PasswordAuthentication;

/**
 * Utility class for misc common functions used in the server
 * @author chandler
 *
 */
public class Util {
	static PasswordAuthentication auth = new PasswordAuthentication();
	
	/**
	 * Hashes the given char array
	 * @param plaintext
	 * @return Hash token
	 */
	public static String hashPassword(char[] plaintext) {
		return auth.hash(plaintext); // TODO stub
	}
	
	/**
	 * Verifies a password against a hash
	 * @param plaintext
	 * @param hash
	 * @return True if they match
	 */
	public static boolean verifyPassword(char[] plaintext, String hash) {
		return auth.authenticate(plaintext, hash); // TODO stub
	}
	
	/**
	 * Overwrites the array with zeroes (use after hashing a password)
	 * @param array
	 */
	public static void wipe(char[] array) {
		for (int i = 0; i < array.length; i++) array[i] = 0;
	}
}
