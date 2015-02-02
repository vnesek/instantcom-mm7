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
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MMSC implementation using standard java {@link HttpURLConnection} to connect
 * to MMSC. Use {@link MM7Context} to configure message
 * serialization/deserialization.
 */
public class BasicMMSC implements MMSC {

	public BasicMMSC(String url) {
		setUrl(url);
	}

	@Override
	public SubmitRsp submit(SubmitReq submitReq) throws MM7Error {
		MM7Response rsp = post(submitReq);
		if (!(rsp instanceof SubmitRsp)) {
			// Error
			throw new MM7Error(rsp.toString());
		}
		return (SubmitRsp) rsp;
	}

	private MM7Response post(MM7Request request) throws MM7Error {
		try {
			URL u = new URL(getUrl());
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			conn.setRequestProperty("Content-Type", request.getSoapContentType());
			conn.setRequestProperty("User-Agent", getContext().getUserAgent());
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("SOAPAction", "\"\"");

			OutputStream out = conn.getOutputStream();
			try {
				MM7Message.save(request, out, getContext());
			} finally {
				out.close();
			}

			String contentType = conn.getContentType();
			try {
				if (contentType.startsWith("text/xml") || contentType.startsWith("multipart/")) {
					InputStream in;
					try {
						in = conn.getInputStream();
					} catch (IOException e) {
						if (conn.getResponseCode() >= 400) {
							in = conn.getErrorStream();
						} else {
							throw e;
						}
					}
					return (MM7Response) MM7Message.load(in, contentType, getContext());
				} else {
					throw new MM7Error("unexpected content: " + conn.getResponseCode() + " "
							+ conn.getResponseMessage() + "\n" + conn.getContent());
				}
			} finally {
				conn.disconnect();
			}
		} catch (IOException ioe) {
			throw new MM7Error("io error", ioe);
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setContext(MM7Context context) {
		this.context = context;
	}

	@Override
	public MM7Context getContext() {
		if (context == null) {
			context = new MM7Context();
		}
		return context;
	}

	private String url;
	private MM7Context context;
}
