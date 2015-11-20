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

import org.junit.Test;

import net.instantcom.mm7.Address.RecipientType;

public class SubmitSample {

	@Test
	public void test() throws IOException, MM7Error {
		String url = "http://localhost:2007/mmsc/mm7/MMSServiceSOAPPort";

		SubmitReq sr = new SubmitReq();
		sr.setVaspId("xxx_vaspid");
		sr.setVasId("xxx_vasid");
		sr.setSubject("Nice weather");
		sr.setMessageClass(MessageClass.INFORMATIONAL);
		sr.setServiceCode("7007");
		sr.addRecipient(new Address("+385910000001", RecipientType.TO));

		// Add text content
		TextContent text = new TextContent("We got a real nice weather today.");
		text.setContentId("text");
		sr.setContent(text);

		// Initialize MM7 client to MMSC
		MMSC mmsc = new BasicMMSC(url);
		mmsc.getContext().setMm7Namespace("http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-3");
		mmsc.getContext().setMm7Version("5.3.0");

		// Send a message
		SubmitRsp submitRsp = mmsc.submit(sr);
		System.out.println(submitRsp);
	}
}
