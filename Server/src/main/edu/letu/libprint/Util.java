package edu.letu.libprint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.stackoverflow.erickson.PasswordAuthentication;

/**
 * Utility class for misc common functions used in the server
 * @author chandler
 *
 */
public class Util {
	static PasswordAuthentication auth = new PasswordAuthentication();
	
	private static File storageRoot = null;
	
	/**
	 * Hashes the given char array
	 * @param plaintext
	 * @return Hash token
	 */
	public static String hashPassword(char[] plaintext) {
		return auth.hash(plaintext);
	}
	
	/**
	 * Verifies a password against a hash
	 * @param plaintext
	 * @param hash
	 * @return True if they match
	 */
	public static boolean verifyPassword(char[] plaintext, String hash) {
		return auth.authenticate(plaintext, hash);
	}
	
	/**
	 * Overwrites the array with zeroes (use after hashing a password)
	 * @param array
	 */
	public static void wipe(char[] array) {
		for (int i = 0; i < array.length; i++) array[i] = 0;
	}
	
	public static File getStorageRoot() {
		if (storageRoot == null) { // Resolve storage directory differently depending on OS
			String os = System.getProperty("os.name");
			if (os.startsWith("Windows")) {
				storageRoot = new File(System.getenv("APPDATA"), "LibPrint/");
			} else {
				storageRoot = new File(System.getProperty("user.home"), ".LibPrint/");
			}
			storageRoot.mkdirs(); // Make sure it exists
			if (!storageRoot.exists()) throw new RuntimeException("Could not create configuration directory: " + storageRoot.getAbsolutePath());
		}
		return storageRoot;
	}
	
	/**
	 * Copy one stream to another
	 * @param in
	 * @param os
	 * @throws IOException
	 */
	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[4096];
		while (true) {
			int r = is.read(buf);
			if (r == -1) {
				break;
			}
			os.write(buf, 0, r);
		}
	}
}
