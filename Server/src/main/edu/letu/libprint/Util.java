package edu.letu.libprint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.stackoverflow.erickson.PasswordAuthentication;

/**
 * Utility class for misc common functions used in the server
 * @author chandler
 *
 */
public class Util {
	static PasswordAuthentication auth = new PasswordAuthentication();
	
	private static File storageRoot = null;
	
	
/* C# Code
string GenerateSecToken(string domainCode, string username, string computer) {
    return GetBase64EncodedSHA256Hash(domainCode + ":" + username + ":" + computer);
}

string GetBase64EncodedSHA256Hash(string plaintext) {
    using (SHA256 hash = SHA256Managed.Create()) {
        Byte[] result = hash.ComputeHash(Encoding.UTF8.GetBytes(plaintext));
        return Convert.ToBase64String(result);
    }
}
 */
	/**
	 * Produces a security token for authenticating the client.
	 * @param domainCode A code supplied in the printer configuration
	 * @param username Username supplied by the client
	 * @param computer Computer domain name of the client
	 * @return SHA256 hash of the concatenated strings
	 * @throws NoSuchAlgorithmException 
	 */
	public static String generateSecToken(String domainCode, String username, String computer) throws NoSuchAlgorithmException {
		String text = domainCode + ":" + username + ":" + computer;
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(hash).replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("=", "");
	}
	
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
	
	/**
	 * Sanitize JSON string values
	 * @param in Input string
	 * @return
	 */
	public static String sanitizeJSONString(String in) {
		return in.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	/**
	 * Sanitize URL string values
	 * @param in Input string
	 * @return
	 */
	public static String sanitizeURL(String in) {
		try {
			return URLEncoder.encode(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Get a price-style formatted double like 0.00
	 * @param priceD
	 * @return
	 */
	public static String formatPrice(double priceD) {
		return String.format("%1.2f", priceD);
	}
	
	/**
	 * Get a formatted price string from a double
	 * @param priceD
	 * @param perPage Append " per page" to the end of a price?
	 * @return
	 */
	public static String priceString(double priceD, boolean perPage) {
		if (priceD == 0.00) return "Free";
		else return "$" + formatPrice(priceD) + (perPage ? " per page" : "");
	}
}
