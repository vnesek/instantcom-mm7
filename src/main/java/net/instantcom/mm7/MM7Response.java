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

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

public class MM7Response extends MM7Message {

	public static final int SC_SUCCESS = 1000;
	public static final int SC_PARTIAL_SUCCESS = 1100;
	public static final int SC_CLIENT_ERROR = 2000;
	public static final int SC_OPERATION_RESTRICTED = 2001;
	public static final int SC_ADDRESS_ERROR = 2002;
	public static final int SC_ADDRESS_NOT_FOUND = 2003;
	public static final int SC_MULTIMEDIA_CONTENT_REFUSED = 2004;
	public static final int SC_MESSAGE_ID_NOT_FOUND = 2005;
	public static final int SC_LINKEDID_NOT_FOUND = 2006;
	public static final int SC_MESSAGE_FORMAT_CORRUPT = 2007;
	public static final int SC_APPLICATION_ID_NOT_FOUND = 2008;
	public static final int SC_REPLY_APPLICATION_ID_NOT_FOUND = 2009;
	public static final int SC_SERVER_ERROR = 3000;
	public static final int SC_NOT_POSSIBLE = 3001;
	public static final int SC_MESSAGE_REJECTED = 3002;
	public static final int SC_MULTIPLE_ADDRESSES_NOT_SUPPORTED = 3003;
	public static final int SC_APPLICATION_ADDRESSING_NOT_SUPPORTED = 3004;
	public static final int SC_GENERAL_SERVICE_ERROR = 4000;
	public static final int SC_IMPROPER_IDENTIFICATION = 4001;
	public static final int SC_UNSUPPORTED_VERSION = 4002;
	public static final int SC_UNSUPPORTED_OPERATION = 4003;
	public static final int SC_VALIDATION_ERROR = 4004;
	public static final int SC_SERVICE_ERRORh = 4005;
	public static final int SC_SERVICE_UNAVAILABLE = 4006;
	public static final int SC_SERVICE_DENIED = 4007;
	public static final int SC_APPLICATION_DENIED = 4008;

	private static Map<Integer, String> STATUS_CODES;
	
	static {
		Map<Integer, String> sc = new HashMap<Integer, String>();
		sc.put(0, "Unspecified");
		sc.put(1000, "Success");
		sc.put(1100, "Partial success");
		sc.put(2000, "Client error");
		sc.put(2001, "Operation restricted");
		sc.put(2002, "Address Error");
		sc.put(2003, "Address Not Found");
		sc.put(2004, "Multimedia content refused");
		sc.put(2005, "Message ID Not found");
		sc.put(2006, "LinkedID not found");
		sc.put(2007, "Message format corrupt");
		sc.put(2008, "Application ID not found");
		sc.put(2009, "Reply Application ID not found");
		sc.put(3000, "Server Error");
		sc.put(3001, "Not Possible");
		sc.put(3002, "Message rejected");
		sc.put(3003, "Multiple addresses not supported ");
		sc.put(3004, "Application Addressing not supported");
		sc.put(4000, "General service error");
		sc.put(4001, "Improper identification");
		sc.put(4002, "Unsupported version");
		sc.put(4003, "Unsupported operation");
		sc.put(4004, "Validation error");
		sc.put(4005, "Service error");
		sc.put(4006, "Service unavailable");
		sc.put(4007, "Service denied");
		sc.put(4008, "Application denied");
		
		STATUS_CODES = sc;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusText() {
		String text = statusText;
		if (text == null) {
			text = STATUS_CODES.get(statusCode);
		}
		return text;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	@Override
	public void load(Element element) {
		super.load(element);

		Element body = element.getChild("Body", MM7Message.ENVELOPE);
		Element child = (Element) body.getChildren().get(0);

		// Handle SOAP faults, status will be found in a Fault detail element
		if ("Fault".equals(child.getName())) {
			//child = (Element) child.getChild("detail").getChildren().get(0);
			if (element.getNamespace("") != null) {
				 child = (Element) child.getChild("detail",element.getNamespace("")).getChildren().get(0);
			} else {
				 child = (Element) child.getChild("detail").getChildren().get(0);
			}
		}

		Element status = child.getChild("Status", namespace);

		if (status != null) {
			setStatusCode(Integer.parseInt(status.getChildTextTrim("StatusCode", namespace)));
			setStatusText(status.getChildTextTrim("StatusText", namespace));
		}
	}

	@Override
	public Element save(Element parent) {
		Element e = super.save(parent);
		Element status = new Element("Status", e.getNamespace());
		if (statusCode > 0) {
			status.addContent(new Element("StatusCode", e.getNamespace()).setText(Integer.toString(statusCode)));
			status.addContent(new Element("StatusText", e.getNamespace()).setText(statusText));
		}
		e.addContent(status);
		return e;
	}

	public void setDetails(Object details) {
		this.details = details;
	}

	public Object getDetails() {
		return details;
	}

	public boolean isSuccess() {
		return statusCode == 1000;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(getClass().getSimpleName());
		b.append("(").append(statusCode).append(" ").append(statusText);
		b.append(", tid=").append(getTransactionId()).append(")");
		return b.toString();
	}

	private int statusCode;
	private String statusText;
	private Object details;
}