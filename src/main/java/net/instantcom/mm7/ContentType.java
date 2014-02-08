/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2003-2014 Nmote Ltd. All rights reserved.
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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MIME content type representation.
 */
public class ContentType implements Serializable {

	public static final ContentType ANY = new ContentType("*/*");

	public static final ContentType AUDIO = new ContentType("audio/*");
	public static final ContentType IMAGE = new ContentType("image/*");
	public static final ContentType TEXT = new ContentType("text/*");

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public ContentType(ContentType mimeType) {
		this.primaryType = mimeType.primaryType;
		this.subType = mimeType.subType;
		if (mimeType.params != null) {
			this.params = new HashMap<String, String>(mimeType.params);
		}
	}
	
	public ContentType(String mimeType) {
		setContentType(mimeType);
	}

	public ContentType(String mimeType, Map<String, String> params) {
		setContentType(mimeType);
		this.params = params;
	}

	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof ContentType && o != null) {
			ContentType m = (ContentType) o;
			result = hashCode() == m.hashCode() && equals(primaryType, m.primaryType) && equals(subType, m.subType);
		}
		return result;
	}

	public String getMimeType() {
		StringBuffer b = new StringBuffer();
		b.append(getPrimaryType());
		if (getSubType() != null) {
			b.append('/');
			b.append(getSubType());
		}

		if (params != null) {
			for (Map.Entry<String, String> me : params.entrySet()) {
				b.append("; ");
				b.append(me.getKey());
				if (me.getValue() != null) {
					b.append("=\"");
					b.append(me.getValue().toString());
					b.append('\"');
				}
			}
		}
		return b.toString();
	}

	public String getMimeTypeWithoutParams() {
		StringBuffer b = new StringBuffer();
		b.append(getPrimaryType());
		if (getSubType() != null) {
			b.append('/');
			b.append(getSubType());
		}

		return b.toString();
	}

	public String getParameter(String name) {
		String result;
		if (params != null) {
			result = params.get(name);
		} else {
			result = null;
		}
		return result;
	}

	public int getParameterCount() {
		int count;
		if (params != null) {
			count = params.size();
		} else {
			count = 0;
		}
		return count;
	}

	public Map<String, String> getParameters() {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		return Collections.unmodifiableMap(params);
	}

	/**
	 * Gets the media type.
	 * 
	 * @return Returns a Sting
	 */
	public String getPrimaryType() {
		return primaryType;
	}

	/**
	 * Gets the subtype
	 * 
	 * @return Returns a String
	 */
	public String getSubType() {
		return subType;
	}

	public int hashCode() {
		if (hash == -1) {
			hash = primaryType.hashCode();
			if (subType != null) {
				hash ^= subType.hashCode();
			}
		}
		return hash;
	}

	public boolean isMultipart() {
		return "multipart".equals(getPrimaryType());
	}

	public boolean matches(ContentType m) {
		boolean result;
		if ("*".equals(primaryType)) {
			result = true;
		} else if ("*".equals(subType)) {
			result = primaryType.equals(m.primaryType);
		} else {
			result = equals(m);
			if (result && m.params != null && params != null) {
				result = m.params.entrySet().containsAll(params.entrySet());
			}
		}
		return result;
	}

	public String toString() {
		return getMimeType();
	}

	@SuppressWarnings("unused")
	private void removeParam(String name) {
		if (params != null) {
			params.remove(name);
		}
	}

	private void setContentType(String mimeType) {
		ParameterListTokenizer tok = new ParameterListTokenizer(mimeType);
		setPrimaryType(tok.readType());
		setSubType(tok.readSubtype());
		if (params != null) {
			params.clear();
		} else {
			params = new HashMap<String, String>();
		}
		for (;;) {
			String name = tok.readParameter();
			if (name == null) break;
			String value = tok.readValue();
			setParam(name, value);
		}
	}

	private void setParam(String name, String value) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.put(name, value);
	}

	/**
	 * Sets the media type.
	 * 
	 * @param type
	 *            The mimeType to set
	 */
	private void setPrimaryType(String type) {
		if (type == null) { throw new NullPointerException("type can't be null"); }
		this.primaryType = type;
		hash = -1;
	}

	/**
	 * Sets the subtype
	 * 
	 * @param subtype
	 *            The subtype to set
	 */
	private void setSubType(String subtype) {
		this.subType = subtype;
		hash = -1;
	}

	private int hash;
	private Map<String, String> params;
	private String primaryType;
	private String subType;

	private static boolean equals(Object a, Object b) {
		boolean result;
		if (a != null) {
			result = a.equals(b);
		} else {
			result = b == null;
		}
		return result;
	}
}