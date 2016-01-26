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

import org.junit.Test;

public class SubmitRspSample {

	@Test
	public void test() throws IOException, MM7Error {
		InputStream in = SubmitRspSample.class.getResourceAsStream("submit-rsp.xml");

		SubmitRsp rsp = (SubmitRsp) MM7Response.load(in, "text/xml", new MM7Context());
		System.out.println(rsp.getMessageId());
		System.out.println(rsp.getStatusCode());

		MM7Message.save(rsp, System.out, new MM7Context());
	}
}
