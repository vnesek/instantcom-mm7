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

class ParameterListTokenizer {

	public ParameterListTokenizer(String in) {
		this.in = in;
		this.pos = 0;
	}

	public String readParameter() {
		if (pos >= in.length()) return null;

		int end = pos + 1;
		do {
			char c = in.charAt(end);
			if (c == ';' || c == '=') break;
			++end;
		} while (end < in.length());

		String result = in.substring(pos, end).trim();
		pos = end;
		return result;
	}

	private String readQuoted() {
		StringBuffer result = new StringBuffer(30);
		++pos;
		while (pos < in.length()) {
			char c = in.charAt(pos++);
			if (c == '\\' && in.charAt(pos) == '"') {
				result.append('"');
				++pos;
			} else if (c == '"') {
				skipLWS();
				if (pos < in.length() && in.charAt(pos) == ';') {
					++pos;
					skipLWS();
				}
				break;
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	public String readSubtype() {
		if (pos >= in.length()) return null;

		int idx = in.indexOf(";", pos);
		String result;
		if (idx == -1) {
			result = in.substring(pos);
			pos = in.length();
		} else {
			result = in.substring(pos, idx);
			pos = idx + 1;
			skipLWS();
		}
		return result;
	}

	private String readToken() {
		StringBuffer result = new StringBuffer(15);
		while (pos < in.length()) {
			char c = in.charAt(pos++);
			if (c == ';' || Character.isSpaceChar(c)) {
				skipLWS();
				break;
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}

	public String readType() {
		int idx = in.indexOf('/');
		String result;
		if (idx == -1) {
			result = in;
			idx = in.length();
		} else {
			result = in.substring(0, idx);
		}
		pos = idx + 1;
		return result;
	}

	public String readValue() {
		if (pos >= in.length()) return null;

		String result;

		char c = in.charAt(pos);
		if (c == ';') {
			++pos;
			result = null;
		} else if (c == '=') {
			++pos;
			c = in.charAt(pos);
			if (c == '"') {
				result = readQuoted();
			} else {
				result = readToken();
			}
		} else {
			throw new RuntimeException();
		}

		skipLWS();
		return result;
	}

	private void skipLWS() {
		while (pos < in.length() && Character.isSpaceChar(in.charAt(pos))) {
			++pos;
		}
	}

	private String in;
	private int pos;
}
