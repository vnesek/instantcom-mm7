/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2014 InstantCom Ltd. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://raw.github.com/vnesek/instantcom-mm7/master/LICENSE.txt
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at appropriate location.
 */

package net.instantcom.mm7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryContent extends BasicContent {

	/**
	 * Reads up to <code>len</code> bytes from <code>in</code> and writes them
	 * to <code>dest</code> starting with <code>off</code>. Returns number of
	 * bytes copied. If returned number is less than len then InputStream has
	 * returned end-of-file.
	 *
	 * @param in
	 * @param dest
	 * @param off
	 * @param len
	 * @return number of bytes copied
	 * @throws IOException
	 */
	static int copyStreamToByteArray(InputStream in, byte[] dest, int off, int len) throws IOException {
		int r = 0;
		while (r < len) {
			int n = in.read(dest, off + r, len - r);
			if (n > 0) {
				r += n;
			} else if (n == -1) {
				break;
			} else {
				throw new IOException("Read 0 bytes from input stream");
			}
		}
		return r;
	}

	static byte[] toByteArray(InputStream in, int startSize, int maxSize) throws IOException {
		if (startSize > maxSize) {
			startSize = maxSize;
		}

		// Allocate a buffer
		byte[] buffer = new byte[startSize];

		int pos = 0;
		for (;;) {
			// Copy stream into buffer
			int r = copyStreamToByteArray(in, buffer, pos, buffer.length - pos);

			// We've reached EOF
			if (r == 0) {
				break;
			}

			pos += r;

			// We've filled up a buffer
			if (pos == buffer.length) {
				// Calculate new buffer length
				int newLen = buffer.length * 2;

				// Don't use more than maxSize
				if (newLen > maxSize) {
					newLen = maxSize;
				}

				// Copy into a new buffer
				byte[] newBuffer = new byte[newLen];
				System.arraycopy(buffer, 0, newBuffer, 0, pos);
				buffer = newBuffer;
			}
		}

		// Copy to result array
		if (pos < buffer.length) {
			byte[] newBuffer = new byte[pos];
			System.arraycopy(buffer, 0, newBuffer, 0, pos);
			buffer = newBuffer;
		}

		return buffer;
	}

	public BinaryContent() {
	}

	public BinaryContent(String contentType, InputStream in) throws IOException {
        this(contentType, in, 2 * 1024 * 1024);
	}

	public BinaryContent(String contentType, InputStream in, int maxSize) throws IOException {
		setContentType(contentType);
		if (in == null) {
			throw new IOException("in == null");
		}
		try {
			data = toByteArray(in, 16 * 1024, maxSize);
		} finally {
			in.close();
		}
	}

	@Override
	public void writeTo(OutputStream out, String contentId, MM7Context ctx) throws IOException {
		if (contentId == null) {
			contentId = getContentId();
		}

		StringBuilder b = new StringBuilder();
		b.append("\r\nContent-Type: ").append(getContentType());
		
		if(getContentLocation() != null && !getContentLocation().equals("")){
			b.append(";Name=\"").append(getContentLocation()).append("\"");
		}
		
		if (contentId != null) {
			b.append("\r\nContent-ID: <" + contentId + ">");
		}

		boolean sevenBit = ("application/smil".equals(getContentType()) || "application/smil+xml".equals(getContentType())); 
		if (sevenBit) {
			b.append("\r\nContent-Transfer-Encoding: 7bit");
		} else {
		    b.append("\r\nContent-Transfer-Encoding: BASE64");
		}

		if(this.getContentLocation() != null) {
			b.append("\r\nContent-Location: ").append(this.getContentLocation());
			b.append("\r\nContent-Disposition: Attachment; Filename=").append(this.getContentLocation());
		}
		b.append("\r\n\r\n");
		out.write(b.toString().getBytes("iso-8859-1"));
		
		if(sevenBit){
			out.write(data);
			out.flush();
		} else {
			OutputStream base64out = ctx.newBase64OutputStream(out);
			
			base64out.write(data);
			base64out.flush();
		}
		
	}

	@Override
	public int getContentLength() {
		return data.length;
	};

    public byte[] getData() {
        return data;
    }

	private byte[] data;
}
