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
import java.util.Date;

import net.instantcom.mm7.Address.RecipientType;

import org.junit.Test;

public class SubmitSample {

	private static InputStream load(String file) {
		return SubmitSample.class.getResourceAsStream(file);
	}
	/*
	 * 
./nc  221.176.2.121 8899 < submit.txt 

./nc -l 8765 > submit.txt < rep.txt 

*/
	@Test
	public void test() throws IOException, MM7Error {
		String url = "http://42.96.185.95:8765";

		SubmitReq sr = new SubmitReq();
		sr.setTransactionId(String.valueOf((new Date()).getTime()));
		sr.setVaspId("400437");
		sr.setVasId("10085");
		sr.setSubject("MM7Test");
		sr.setMessageClass(MessageClass.INFORMATIONAL);
		sr.setServiceCode("7007");
		sr.addRecipient(new Address("+8618703815655", RecipientType.TO));
		sr.setPriority(Priority.NORMAL);
		sr.setSenderAddress(new Address("10085", RecipientType.TO));
		sr.setChargedParty(ChargedParty.RECIPIENT);

		// Add text content
		
		TextContent text = new TextContent("We got a real nice weather today.");
		text.setContentId("text");
		
		BinaryContent image = new BinaryContent("image/jpeg", load("smrz.JPG"));
		image.setContentId("image");
		
		sr.setContent(new BasicContent( image, text));

		// Initialize MM7 client to MMSC
		MMSC mmsc = new BasicMMSC(url);
		mmsc.getContext().setMm7Namespace("http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-3");
		mmsc.getContext().setMm7Version("5.3.0");

		// Send a message
		MM7Message.save(sr, System.out, new MM7Context());
		SubmitRsp submitRsp = mmsc.submit(sr);
	    System.out.println(submitRsp);
	}
}
