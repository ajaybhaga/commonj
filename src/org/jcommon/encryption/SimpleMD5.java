package org.jcommon.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jcommon.lang.Hexadecimal;

public class SimpleMD5 {
	MessageDigest md;

	public SimpleMD5(String text, String key) {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(text.getBytes());
		md.update(key.getBytes());
	}

	public String toHexString() {
		byte encrypted[] = md.digest();
		String out = "";
		for (int i = 0; i < encrypted.length; i++) {
			out = out + new Hexadecimal(encrypted[i]).toFormatString(2);
		}
		return out;
	}
	
	public static String toHexString(String text, String key) {
		SimpleMD5 md5 = new SimpleMD5(text, key);
		return md5.toHexString();
	}
	
	public static void main(String[] args) throws Exception {
		String text = "testing";
		String key = "mhicks";
		System.out.println(toHexString(text, key));
	}
}