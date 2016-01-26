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

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

class SoapContent extends BasicContent {

	public SoapContent(InputStream in) throws IOException {
		super("text/xml; charset=\"utf-8\"");
		try {
			this.doc = new SAXBuilder().build(in);
		} catch (JDOMException e) {
			throw new IOException("failed to parse SOAP message", e);
		}
	}

	public Document getDoc() {
		return doc;
	}
	
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	@Override
	public void writeTo(OutputStream out, String contentId, MM7Context ctx) throws IOException {
		new XMLOutputter().output(doc, out);
	}

	private Document doc;
}
