package my.app;

import java.security.MessageDigest;

public class CryptoJS {

	public static String SHA512(String saltedToken) throws Exception {
		MessageDigest md = null;
		md = MessageDigest.getInstance("SHA-512");
		md.update(saltedToken.getBytes());
		byte byteData[] = md.digest();
		String base64 = bytesToHex(byteData);
		return base64;
	}
	
	private final static char[] hexArray = "0123456789abcdef".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}	

}
