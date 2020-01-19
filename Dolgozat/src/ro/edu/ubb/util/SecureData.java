package ro.edu.ubb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to secure data.
 * 
 * @author Nemet Orsolya
 *
 */
public class SecureData {
	
	private SecureData() {
		
	}

	public static byte[] hashPassword(String password) {
		MessageDigest md;
		byte[] byteData = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(password.getBytes());
			byteData = md.digest();
		} catch (NullPointerException | NoSuchAlgorithmException e) {
			throw new UtilException("An error occured while hashing.");
		}
		return byteData;

	}

	public static String convertHexToString(byte[] byteData) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
