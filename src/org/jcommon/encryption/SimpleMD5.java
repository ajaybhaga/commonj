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
}