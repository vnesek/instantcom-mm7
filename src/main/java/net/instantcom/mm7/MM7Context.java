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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.jdom2.output.Format;
import org.jvnet.mimepull.MIMEConfig;

/**
 * Configuration for reading and writing MM7 messages. You can configure
 * parameters for serializing and deserializing and few recovery options.
 *
 * @author vnesek
 */
public class MM7Context {

	private static String[] BASE64_OUTPUT_STREAM_CLASSES = { "org.apache.commons.codec.binary.Base64OutputStream",
			"com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream" };

	public Class<? extends OutputStream> getBase64OutputStream() {
		return base64OutputStream;
	}

	public Format getJdomFormat() {
		if (jdomFormat == null) {
			jdomFormat = Format.getPrettyFormat();
		}
		return jdomFormat;
	}

	public MIMEConfig getMimePullConfig() {
		if (mimePullConfig == null) {
			mimePullConfig = new MIMEConfig();
		}
		return mimePullConfig;
	}

	public String getMm7Namespace() {
		return mm7Namespace;
	}

	public String getMm7Version() {
		return mm7Version;
	}

	public String getPassword() {
		return password;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getUsername() {
		return username;
	}

	public boolean isUseFirstContentFoundIfHrefIsInvalid() {
		return useFirstContentFoundIfHrefIsInvalid;
	}

	public OutputStream newBase64OutputStream(OutputStream out) {
		if (base64OutputStream == null) {
			initializeBase64OutputStream();
		}
		try {
			return base64OutputStreamConstructor.newInstance(out);
		} catch (NullPointerException e) {
			throw new IllegalStateException("no BASE64 output stream configured");
		} catch (Exception e) {
			throw new IllegalStateException("failed to construct BASE64 output stream", e);
		}
	}

	public void setBase64OutputStream(Class<? extends OutputStream> base64OutputStream) {
		if (base64OutputStream == null) {
			this.base64OutputStream = null;
			this.base64OutputStreamConstructor = null;
		}
		try {
			Constructor<? extends OutputStream> c = base64OutputStream.getConstructor(OutputStream.class);
			{ // Let's try the constructor out ...
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStream s = c.newInstance(baos);
				s.write("test123".getBytes("iso-8859-1"));
				s.close();
				String encoded = baos.toString("iso-8859-1");
				if (!encoded.equals("dGVzdDEyMw==")) {
					throw new IllegalArgumentException(base64OutputStream.getName()
							+ " incorrectly encodes BASE64 data");
				}
			}
			this.base64OutputStream = base64OutputStream;
			this.base64OutputStreamConstructor = c;
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(base64OutputStream.getName()
					+ " must expose public constructor taking java.io.OutputStream argument");
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException(base64OutputStream.getName() + " can't be used for BASE64 encoding: "
					+ e);
		}
	}

	public void setJdomFormat(Format jdomFormat) {
		this.jdomFormat = jdomFormat;
	}

	public void setMimePullConfig(MIMEConfig mimePullConfig) {
		this.mimePullConfig = mimePullConfig;
	}

	public void setMm7Namespace(String namespace) {
		this.mm7Namespace = namespace;
	}

	public void setMm7Version(String mm7version) {
		this.mm7Version = mm7version;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUseFirstContentFoundIfHrefIsInvalid(boolean useFirstIfContentHrefIsInvalid) {
		this.useFirstContentFoundIfHrefIsInvalid = useFirstIfContentHrefIsInvalid;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@SuppressWarnings("unchecked")
	private void initializeBase64OutputStream() {
		for (String className : BASE64_OUTPUT_STREAM_CLASSES) {
			try {
				Class<?> clazz = Class.forName(className);
				setBase64OutputStream((Class<? extends OutputStream>) clazz);
				break;
			} catch (Exception e) {
			}
		}
	}

	private Class<? extends OutputStream> base64OutputStream;
	private Constructor<? extends OutputStream> base64OutputStreamConstructor;
	private Format jdomFormat;
	private MIMEConfig mimePullConfig;
	private String mm7Namespace = About.MM7_NAMESPACE;
	private String mm7Version = About.MM7_VERSION;
	private String password;
	private boolean useFirstContentFoundIfHrefIsInvalid = true;
	private String userAgent = "InstantCom-MM7/" + About.VERSION;
	private String username;
}
