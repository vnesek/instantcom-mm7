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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * Test reading actual deliver requests from various actual MMSC-s.
 */
public class DeliverReqTest {

	@Test
	public void request2() throws IOException, MM7Error {
		String ct = "multipart/related; boundary=\"NextPart_000_0125_01C19839.7237929064\"; type=text/xml";
		InputStream in = DeliverReq.class.getResourceAsStream("deliver-req2.txt");
		DeliverReq req = (DeliverReq) MM7Response.load(in, ct, new MM7Context());
		
		assertEquals("Reminder", req.getVasId());
		assertEquals("240.110.75.34", req.getRelayServerId());
		assertEquals("97254265781@OMMS.com", req.getSender().toString());
		assertEquals("Weather Forecast", req.getSubject());
		assertEquals(Priority.NORMAL, req.getPriority());
	}

	@Test
	public void request5() throws IOException, MM7Error {
		String ct = "multipart/related; boundary=\"----=_Part_15_16023213.1346680532641\"; type=text/xml";
		InputStream in = DeliverReq.class.getResourceAsStream("deliver-req5.txt");
		DeliverReq req = (DeliverReq) MM7Response.load(in, ct, new MM7Context());
		
		assertEquals("xmmc", req.getVasId());
		assertEquals("xmmc1", req.getRelayServerId());
		assertEquals("+381600001200", req.getSender().toString());
		assertEquals("(no subject)", req.getSubject());
		assertEquals(Priority.NORMAL, req.getPriority());
	}

}
