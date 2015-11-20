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

	public BasicContent(String contentType) {
		this.contentType = contentType;
	}
	
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
	public void addParts(Content content) {
		if(parts==null){
			parts = new ArrayList<Content>(); 
			this.boundary = "==Multipart==" + UUID.randomUUID().toString();
		}
			
		parts.add(content);
		
		if (content.getContentType().contains("smil")) {
			setContentType("multipart/related; start=\"<" + content.getContentId() + ">\"; type=\"" + content.getContentType()
					+ "\"; boundary=\"" + boundary + "\"");
		}else{
			setContentType("multipart/mixed; boundary=\"" + boundary + "\"");
		}
	}
	
	public SoapContent findSoapContent(String start){
		if(parts == null) return null;
		if(start==null || "".equals(start)){
			for (Content c : parts) {
				if (c instanceof SoapContent) {
					return (SoapContent) c;
				}
			}
		}else{
			for (Content c : parts) {
				if (start.equals(c.getContentId())) {
					return (SoapContent) c;
				}
			}
		}
		return null;
	}
	
	public Content findPayload(String contentId,String href){
		Content payload = null;
		if(parts == null) return null;
		if(contentId==null || "".equals(contentId)){
			for (Content c : parts) {
				if (href.equals(c.getContentLocation())) {
					payload =   c;
					break;
				}
			}
		}else{
			for (Content c : parts) {
				if (contentId.equals(c.getContentId())) {
					payload =  c;
					break;
				}
			}
		}
		
		if (payload == null) {
			for (Content c : parts) {
				if (!(c instanceof SoapContent)) {
					payload = c;
					break;
				}
			}
		}
		return payload;
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

	private void setContentType(String contentType) {
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
				setContentType("multipart/related; start=\"<" + smil.getContentId() + ">\"; type=\"" + smil.getContentType()
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

		for (Content c : this.parts) {
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