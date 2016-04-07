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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jvnet.mimepull.MIMEConfig;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEPart;

public class MM7Message implements JDOMSupport {

	public static Namespace ENVELOPE = Namespace.getNamespace("env", "http://schemas.xmlsoap.org/soap/envelope/");

	/**
	 * Loads a correct subclass of a MM7 message by looking at a SOAP request
	 * and instantiating an object of related class. Handles SOAP with
	 * attachments.
	 *
	 * @param in
	 *            input stream to load message from
	 * @param contentType
	 *            of a request. Can be an multipart or text/xml
	 * @param ctx
	 *            configuration for loading of message
	 *
	 * @return an MM7Message instance
	 * @throws IOException
	 *             if can't deserialize SOAP message or some other IO problem
	 *             occurs
	 * @throws MM7Error
	 *             if SOAP fault is received.
	 */
	public static MM7Message load(InputStream in, String contentType, MM7Context ctx) throws IOException, MM7Error {
		ContentType ct = new ContentType(contentType);
		BasicContent content = fromStream(in, ct);
		SoapContent soap = null;
		if (content.getParts() != null) {
			String start = (String) ct.getParameter("start");
			if (start != null) {
				if (start.length() == 0) {
					throw new MM7Error("invalid content type, start parameter is empty: " + contentType);
				}
				if (start.charAt(0) == '<') {
					start = start.substring(1, start.length() - 1);
				}
				for (Content c : content.getParts()) {
					if (start.equals(c.getContentId())) {
						soap = (SoapContent) c;
						break;
					}
				}
			} else {
				for (Content c : content.getParts()) {
					if (c instanceof SoapContent) {
						soap = (SoapContent) c;
						break;
					}
				}
			}
		} else {
			soap = (SoapContent) content;
		}

		// Parse SOAP message to JDOM
		if (soap == null) {
			throw new MM7Error("can't find SOAP parts");
		}
		Document doc = soap.getDoc();

		Element body = doc.getRootElement().getChild("Body", ENVELOPE);
		Element e = (Element) body.getChildren().get(0);
		String message = e.getName();

		// Check if we've got a SOAP fault
		if ("Fault".equals(message)) {
			MM7Error mm7error = new MM7Error();
			mm7error.load(doc.getRootElement());
			throw mm7error;
		}

		// Instantiate a correct message class
		try {
			Class<?> clazz = Class.forName("net.instantcom.mm7." + message);
			MM7Message mm7 = (MM7Message) clazz.newInstance();

			// Load response
			mm7.load(doc.getRootElement());

			// Set content if any
			if (content.getParts() != null && mm7 instanceof HasContent) {
				Element contentElement = e.getChild("Content", e.getNamespace());
				String href = contentElement.getAttributeValue("href", contentElement.getNamespace());
				if (href == null) {
					href = contentElement.getAttributeValue("href");
				}

				// Loop over content, try to match content-location or
				// content-id
				Content payload = null;
				if (href.startsWith("cid:")) {
					// Match by content-id
					String cid = href.substring(4).trim();
					for (Content c : content.getParts()) {
						if (cid.equals(c.getContentId())) {
							payload = c;
							break;
						}
					}
				} else {
					// Match by content-location
					for (Content c : content.getParts()) {
						if (href.equals(c.getContentLocation())) {
							payload = c;
							break;
						}
					}
				}
				// We've got a junk message here... try to be a smart cookie and
				// use first non-SOAP part
				if (payload == null) {
					for (Content c : content.getParts()) {
						if (!(c instanceof SoapContent)) {
							payload = c;
							break;
						}
					}
				}
				if (payload != null) {
					((HasContent) mm7).setContent(payload);
				}
			}
			return mm7;
		} catch (Throwable t) {
			throw new MM7Error("failed to instantiate message " + message, t);
		}
	}

	public static void save(MM7Message mm7, OutputStream out, MM7Context ctx) throws IOException {
		// Setup MM7 version and XML name space if not already set
		if (mm7.getMm7Version() == null) {
			mm7.setMm7Version(ctx.getMm7Version());
		}
		if (mm7.getNamespace() == null) {
			mm7.setNamespace(ctx.getMm7Namespace());
		}

		final XMLOutputter xo = new XMLOutputter();
		xo.setFormat(ctx.getJdomFormat());
		final Writer w = new OutputStreamWriter(out, "utf-8");

		if (mm7.isMultipart()) {
			final Content content = ((HasContent) mm7).getContent();

			final String boundary = mm7.getSoapBoundary();
			w.write("--");
			w.write(boundary);
			w.write("\r\nContent-Type: text/xml; charset=\"utf-8\"\r\nContent-Transfer-Encoding: binary\r\nContent-ID: <");
			w.write(mm7.getSoapContentId());
			w.write(">\r\n\r\n");
			w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");

			xo.output(mm7.toSOAP(ctx), w);

			w.write("\r\n--");
			w.write(boundary);
			w.flush();

			content.writeTo(out, "mm7-content", ctx);
			w.write("\r\n--");
			w.write(boundary);
			w.write("--");
		} else {
			xo.output(mm7.toSOAP(ctx), w);
		}
		w.flush();
	}

	private static BasicContent fromPart(MIMEPart part, ContentType contentType) throws IOException {
		BasicContent result;
		if (contentType.getPrimaryType().equals("text") || contentType.getSubType().equals("smilx")) {
			if (contentType.getSubType().equals("xml")) {
				result = new SoapContent(part.readOnce());
			} else {
				TextContent text = new TextContent();
				String encoding = contentType.getParameter("charset");
				if (encoding == null) {
					encoding = "iso-8859-1";
				}
				text.setText(new String(BinaryContent.toByteArray(part.readOnce(), 1024, 256 * 1024), encoding));
				result = text;
			}
		} else {
			result = new BinaryContent(part.getContentType(), part.readOnce());
		}
		result.setContentType(part.getContentType());
		result.setContentId(part.getContentId());
		result.setContentLocation(getContentLocation(part));
		return result;
	}

	private static BasicContent fromStream(InputStream in, ContentType contentType) throws IOException {
		if (in == null) {
			throw new NullPointerException("input stream is null");
		}
		BasicContent result;
		if (contentType.isMultipart()) {
			MIMEConfig mimeConfig = new MIMEConfig();
			MIMEMessage msg = new MIMEMessage(in, contentType.getParameter("boundary"), mimeConfig);
			List<Content> contents = new ArrayList<Content>();
			for (MIMEPart part : msg.getAttachments()) {
				BasicContent content;
				ContentType partType = new ContentType(part.getContentType());
				if (partType.isMultipart()) {
					content = fromStream(part.readOnce(), partType);
					part.close();
				} else {
					content = fromPart(part, partType);
					part.close();
				}
				content.setContentId(part.getContentId());
				content.setContentLocation(getContentLocation(part));
				content.setContentType(part.getContentType());
				contents.add(content);
			}
			result = new BasicContent(contents);
		} else if (contentType.getMimeTypeWithoutParams().equals("text/xml")) {
			result = new SoapContent(in);
		} else if (contentType.getPrimaryType().equals("text")) {
			TextContent text = new TextContent();
			String encoding = new ContentType(contentType).getParameter("charset");
			if (encoding == null) {
				encoding = "iso-8859-1";
			}
			text.setText(new String(BinaryContent.toByteArray(in, 1024, 256 * 1024), encoding));
			result = text;
		} else {
			result = new BinaryContent(contentType.getMimeType(), in);
		}
		result.setContentType(contentType.getMimeType());
		return result;
	}

	private static String getContentLocation(MIMEPart part) {
		String result;
		List<String> contentLocation = part.getHeader("Content-Location");
		if (contentLocation != null) {
			result = contentLocation.get(0);
		} else {
			result = null;
		}
		return result;

	}

	public String getMm7Version() {
		return mm7Version;
	}

	public String getNamespace() {
		return namespace != null? namespace.getURI() : null;
	}

	public String getSoapBoundary() {
		if (soapBoundary == null) {
			this.soapBoundary = "==MM7-SOAP==" + UUID.randomUUID().toString();
		}
		return soapBoundary;
	}

	public String getSoapContentId() {
		return soapContentId;
	}

	public String getSoapContentType() {
		String contentType;
		if (isMultipart()) {
			contentType = "multipart/related; boundary=\"" + getSoapBoundary() + //
					"\"; type=\"text/xml\"; start=\"<" + getSoapContentId() + ">\"";
		} else {
			contentType = "text/xml; charset=\"utf-8\"";
		}
		return contentType;
	}

	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Returns true if this message has content.
	 *
	 * @return true if SOAP message needs multipart encoding
	 */
	public boolean isMultipart() {
		if (this instanceof HasContent) {
			return ((HasContent) this).getContent() != null;
		}
		return false;
	}

	@Override
	public void load(Element element) {
		Element body = element.getChild("Body", element.getNamespace());

		// Extract MM7 namespace from SOAP body
		Iterator<?> i = body.getDescendants(new ElementFilter());
		while (i.hasNext()) {
			Element e = (Element) i.next();
			Namespace ns = e.getNamespace();
			if (ns != null && ns.getURI().contains("MM7")) {
				this.namespace = ns;
				break;
			}
		}

		if (this.namespace == null) {
			throw new IllegalStateException("can't autodetect MM7 namespace: " + body.toString());
		}

		Element header = element.getChild("Header", element.getNamespace());
		setTransactionId(header.getChildTextTrim("TransactionID", namespace));
	}

	@Override
	public Element save(Element parent) {
		Element e = new Element(getClass().getSimpleName(), namespace);
		final String mm7Version = getMm7Version();
		if (mm7Version != null) {
			e.addContent(new Element("MM7Version", e.getNamespace()).setText(mm7Version));
		}
		return e;
	}

	public void setMm7Version(String mm7Version) {
		this.mm7Version = mm7Version;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace != null? Namespace.getNamespace(mm7NamespacePrefix, namespace) : null;
	}

	public void setSoapBoundary(String mimeBoundary) {
		this.soapBoundary = mimeBoundary;
	}

	public void setSoapContentId(String soapContentId) {
		this.soapContentId = soapContentId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		Document doc = new Document(toSOAP(new MM7Context()));
		StringWriter w = new StringWriter();
		try {
			out.output(doc, w);
			return w.toString();
		} catch (IOException e) {
			return super.toString();
		}
	}

	private Element toSOAP(MM7Context ctx) {
		Element env = new Element("Envelope", ENVELOPE);
		if (namespace != null) {
			env.addNamespaceDeclaration(namespace);
		}
		Element header = new Element("Header", ENVELOPE);
		if (transactionId != null) {
			header.addContent(new Element("TransactionID", namespace) //
					.setText(transactionId) //
					.setAttribute("mustUnderstand", "1", ENVELOPE));
		}
		env.addContent(header);

		Element body = new Element("Body", ENVELOPE);
		body.addContent(save(body));
		env.addContent(body);
		return env;
	}

	public void setMm7NamespacePrefix(String mm7NamespacePrefix) {
		this.mm7NamespacePrefix = mm7NamespacePrefix;
		setNamespace(getNamespace());
	}

	public String getMm7NamespacePrefix() {
		return mm7NamespacePrefix;
	}

	Namespace namespace;
	private String mm7Version;
	private String mm7NamespacePrefix;
	private String soapBoundary;
	private String soapContentId = "mm7-soap";
	private String transactionId;
}