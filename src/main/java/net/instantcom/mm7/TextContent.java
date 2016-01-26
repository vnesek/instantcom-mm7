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

public class TextContent extends BasicContent {

	public TextContent() {
		this("");
	}

	public TextContent(String text) {
		
		super("text/plain; charset=\"utf-8\"");
		setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
		b.append("\r\n\r\n");
		out.write(b.toString().getBytes("iso-8859-1"));
		out.write(text.getBytes("utf-8"));
	}

	@Override
	public int getContentLength() {
		return text.length();
	}

	private String text;
}
