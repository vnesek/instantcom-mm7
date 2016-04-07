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

import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class MM7Error extends Exception implements JDOMSupport {

	private static final long serialVersionUID = 4698982334914854725L;

	public MM7Error() {
	}

	public MM7Error(String message) {
		super(message);
	}

	public MM7Error(String message, Throwable cause) {
		super(message, cause);
	}

	public MM7Error(Throwable cause) {
		super(cause);
	}

	public String getFaultCode() {
		return faultCode;
	}

	public String getFaultMessage() {
		return faultMessage;
	}

	@Override
	public String getMessage() {
		String m = super.getMessage();
		if (m == null) {
			StringBuilder b = new StringBuilder();
			if (faultCode != null) {
				b.append(faultCode);
			}
			if (faultMessage != null) {
				b.append(':').append(faultMessage);
			}
			if (response != null) {
				b.append(':').append(response);
			}
			m = b.toString();
		}
		return m;
	}

	public MM7Response getResponse() {
		return response;
	}

	@Override
	public void load(Element element) {

		Element body = element.getChild("Body", MM7Message.ENVELOPE);
		Element e = (Element) body.getChildren().get(0);
		
		this.faultCode = e.getChildTextTrim("faultcode");
		this.faultMessage = e.getChildTextTrim("faultstring");
		try {
			Element detail;
			if (element.getNamespace("") != null) {
				 detail = (Element) e.getChild("detail",element.getNamespace("")).getChildren().get(0);
			} else {
				 detail = (Element) e.getChild("detail").getChildren().get(0);
			}
			String message = detail.getName();
			// Instantiate correct status type

			Class<?> clazz = Class.forName("net.instantcom.mm7." + message);
			this.response = (MM7Response) clazz.newInstance();
			this.response.load(element);
		} catch (Throwable t) {
			// Ignored
			XMLOutputter outp = new XMLOutputter();
			String s = outp.outputString(element);
			System.err.println("Failed to instantiate a correct response type" + s);
			t.printStackTrace();
		}
	}

	@Override
	public Element save(Element parent) {
		throw new UnsupportedOperationException();
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	public void setFaultMessage(String faultMessage) {
		this.faultMessage = faultMessage;
	}

	public void setResponse(MM7Response response) {
		this.response = response;
	}

	private MM7Response response;
	private String faultCode;
	private String faultMessage;
}
