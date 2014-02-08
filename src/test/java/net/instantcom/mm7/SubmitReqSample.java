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

public class SubmitReqSample {

	public static void main(String[] args) throws IOException {

		SubmitReq sr = new SubmitReq();
		sr.setVaspId("InstantCom");
		sr.setVasId("Test");
		sr.setLinkedId("linked-id");
		sr.setServiceCode("service-code");
		sr.setPriority(Priority.HIGH);
		sr.setApplicID("Applic-id");
		sr.setChargedParty(ChargedParty.SENDER);
		sr.setSubject("subject");
		sr.setExpiryDate(new RelativeDate("P3H"));

		{
			Address a = new Address();
			a.setAddress("88373737");
			sr.addRecipient(a);
		}

		{
			Address a = new Address();
			a.setAddress("883");
			a.setAddressType(Address.AddressType.SHORT_CODE);
			sr.addRecipient(a);
		}

		{
			Address a = new Address();
			a.setAddress("88373737@foo.com");
			a.setAddressType(Address.AddressType.RFC822_ADDRESS);
			a.setRecipientType(Address.RecipientType.BCC);
			sr.addRecipient(a);
		}

		sr.setTransactionId("23443322");
		sr.setContent(new TextContent("test"));

		MM7Message.save(sr, System.out, new MM7Context());
	}
}
