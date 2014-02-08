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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ForwardLockContent implements Content {

	public ForwardLockContent(Content part) {
		this.part = part;
		this.boundary = "==Forward-Lock==" + UUID.randomUUID().toString();
	}

	@Override
	public String getContentId() {
		return getPart().getContentId();
	};
	
	@Override
	public String getContentLocation() {
		return getPart().getContentLocation();
	}
	
	@Override
	public String getContentType() {
		return "application/vnd.oma.drm.message; boundary=\"" + boundary  + "\"";
	}
	
	@Override
	public List<Content> getParts() {
		return null;
	}

	@Override
	public Iterator<Content> iterator() {
		return null;
	}
		
	@Override
	public void writeTo(OutputStream out, String contentId, MM7Context ctx) throws IOException {
		if (contentId == null) {
			contentId = getPart().getContentId();
		}
		StringBuilder b = new StringBuilder();
		b.append("\r\nContent-Type: " + getContentType());
		if (contentId != null) {
			b.append("\r\nContent-ID: <" + contentId + ">");
		}
		b.append("\r\n\r\n--");
		b.append(boundary);
		out.write(b.toString().getBytes("iso-8859-1"));
		out.flush();

		getPart().writeTo(out, contentId != null? "forward-lock-" + contentId : null, ctx);
		out.flush();
		
		b.setLength(0);
		b.append("\r\n--");
		b.append(boundary);
		b.append("--");
		out.write(b.toString().getBytes("iso-8859-1"));
	}
	
	private Content getPart() {
		return part;
	}
	
	private String boundary;
	private Content part;
}
