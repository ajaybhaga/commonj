package org.jcommon.j2ee;

import java.io.*;

import javax.servlet.http.*;

public class NTLMAuthenticator {
	public static final NTLMUser authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String auth = request.getHeader("Authorization");
		if (auth == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("WWW-Authenticate", "NTLM");
			response.flushBuffer();
			return null;
		}
		if (auth.startsWith("NTLM ")) {
			byte[] msg = new sun.misc.BASE64Decoder().decodeBuffer(auth.substring(5));
			int off = 0, length, offset;
			if (msg[8] == 1) {
				byte z = 0;
				byte[] msg1 = { (byte) 'N', (byte) 'T', (byte) 'L', (byte) 'M',
						(byte) 'S', (byte) 'S', (byte) 'P', z, (byte) 2, z, z,
						z, z, z, z, z, (byte) 40, z, z, z, (byte) 1,
						(byte) 130, z, z, z, (byte) 2, (byte) 2, (byte) 2, z,
						z, z, z, z, z, z, z, z, z, z, z };
				response.setHeader("WWW-Authenticate", "NTLM " + new sun.misc.BASE64Encoder().encodeBuffer(msg1).trim());
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			} else if (msg[8] == 3) {
				off = 30;

				length = msg[off + 17] * 256 + msg[off + 16];
				offset = msg[off + 19] * 256 + msg[off + 18];
				String remoteHost = new String(msg, offset, length, "UnicodeLittleUnmarked");

				length = msg[off + 1] * 256 + msg[off];
				offset = msg[off + 3] * 256 + msg[off + 2];
				String domain = new String(msg, offset, length, "UnicodeLittleUnmarked");

				length = msg[off + 9] * 256 + msg[off + 8];
				offset = msg[off + 11] * 256 + msg[off + 10];
				String username = new String(msg, offset, length, "UnicodeLittleUnmarked");

				return new NTLMUser(username, remoteHost, domain);
			}
		}
		return null;
	}
}
