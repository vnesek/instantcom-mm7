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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BasicContent implements Content {

	public BasicContent() {
	}

	public BasicContent(Content... parts) {
		this(Arrays.asList(parts));
	}

	public BasicContent(List<Content> parts) {
		setParts(parts);
	}

	@Override
	public String getContentId() {
		if (contentId == null) {
			contentId = UUID.randomUUID().toString();
		}
		return contentId;
	}

	public int getContentLength() {
		return -1;
	}

	@Override
	public String getContentLocation() {
		return contentLocation;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public List<Content> getParts() {
		return parts;
	}

	@Override
	public Iterator<Content> iterator() {
		Iterator<Content> result;
		if (parts != null) {
			result = parts.iterator();
		} else {
			result = new ArrayList<Content>().iterator();
		}
		return result;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setParts(List<Content> parts) {
		this.parts = parts;
		if (parts != null && !parts.isEmpty() && contentType == null) {
			// Check if there is a smil inside
			Content smil = null;
			for (Content c : parts) {
				if (c.getContentType().contains("smil")) {
					smil = c;
					break;
				}
			}

			this.boundary = "==Multipart==" + UUID.randomUUID().toString();
			if (smil == null) {
				setContentType("multipart/mixed; boundary=\"" + boundary + "\"");
			} else {
				String smilcontentType = smil.getContentType();
				if (smilcontentType.indexOf(";")!=-1) {
					smilcontentType = smilcontentType.substring(0, smilcontentType.indexOf(';'));
				}
				setContentType("multipart/related; start=\"<" + smil.getContentId() + ">\"; type=\"" + smilcontentType
						+ "\"; boundary=\"" + boundary + "\"");
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(getClass().getSimpleName());
		b.append("({");
		b.append(getContentType());
		b.append('}');
		if (contentId != null) {
			b.append(", cid:").append(contentId);
		}
		if (contentLocation != null) {
			b.append(", ").append(contentLocation);
		}
		if (parts != null && !parts.isEmpty()) {
			b.append(", [");
			for (Content c : parts) {
				b.append(c);
				b.append(", ");
			}
			b.setLength(b.length() - 2);
			b.append(']');
		} else {
			b.append(", length=").append(getContentLength());
		}
		b.append(')');
		return b.toString();
	}

	@Override
	public void writeTo(OutputStream out, String contentId, MM7Context ctx) throws IOException {
		if (contentId == null) {
			contentId = getContentId();
		}
		StringBuilder b = new StringBuilder();
		b.append("\r\nContent-Type: ");
		b.append(getContentType());
		if (contentId != null) {
			b.append("\r\nContent-ID: <" + contentId + ">");
		}
		b.append("\r\n");
		out.write(b.toString().getBytes("iso-8859-1"));

		for (Content c : getParts()) {
			b.setLength(0);
			b.append("\r\n--");
			b.append(boundary);
			out.write(b.toString().getBytes("iso-8859-1"));
			c.writeTo(out, null, ctx);
		}

		b.setLength(0);
		b.append("\r\n--");
		b.append(boundary);
		b.append("--");
		out.write(b.toString().getBytes("iso-8859-1"));
	}

	private String boundary;
	private String contentLocation;
	private String contentId;
	private String contentType;
	private List<Content> parts;
}