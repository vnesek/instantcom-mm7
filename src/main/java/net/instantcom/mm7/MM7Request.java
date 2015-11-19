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

import java.util.ArrayList;
import java.util.List;

import net.instantcom.mm7.Address.RecipientType;

import org.jdom2.Element;

public class MM7Request extends MM7Message {

	public String getVasId() {
		return vasId;
	}

	public String getVaspId() {
		return vaspId;
	}

	public void setVasId(String vasId) {
		this.vasId = vasId;
	}

	public void setVaspId(String vaspId) {
		this.vaspId = vaspId;
	}

	public Element save(Element parent) {
		Element e = super.save(parent);

			if (!recipients.isEmpty()) {
				Element r = new Element("Recipients", e.getNamespace());
				addRecipients(r, RecipientType.TO);
				addRecipients(r, RecipientType.CC);
				addRecipients(r, RecipientType.BCC);
				if (r.getContentSize() > 0) {
					e.addContent(r);
				}
			}

			if (vaspId != null) {
				e.addContent(new Element("VASPID", e.getNamespace()).setText(vaspId));
			}
			
			if (vasId != null) {
				e.addContent(new Element("VASID", e.getNamespace()).setText(vasId));
			}
			
			if (senderAddress != null) {
				Element sa = new Element("SenderAddress", e.getNamespace());
				e.addContent(sa);
				if (senderAddress.getAddressType() != null) {
					sa.addContent(senderAddress.save(sa));
				} else {
					sa.addContent(senderAddress.getAddress());
				}
			}
			
		if(relayServerId != null){
			e.addContent(new Element("MMSRelayServerID", e.getNamespace()).setText(relayServerId));
		}
		return e;
	}

	public MM7Response reply() {
		throw new UnsupportedOperationException("should be overriden by subclass");
	}

	@Override
	public void load(Element element) {
		super.load(element);

		Element body = element.getChild("Body", MM7Message.ENVELOPE);
		Element req = (Element) body.getChildren().get(0);
		Element recipients =  req.getChild("Recipients",req.getNamespace());
		
		if(recipients != null && recipients.getChildren()!=null && recipients.getChildren().size() > 0){
			
			List<Element> addrList = recipients.getChildren();
			for(Element addr:addrList){
				RecipientType rtype = RecipientType.valuesOf(addr.getName());
				Address a = new Address();
				a.load((Element) addr.getChildren().get(0));
				a.setRecipientType(rtype);
				this.recipients.add(a);	
			}
		}
		
		setVasId(req.getChildTextTrim("VASID", req.getNamespace()));
		setVaspId(req.getChildTextTrim("VASPID", req.getNamespace()));
		setRelayServerId(req.getChildTextTrim("MMSRelayServerID", req.getNamespace()));
	}

	public void setRelayServerId(String relayServerId) {
		this.relayServerId = relayServerId;
	}

	public String getRelayServerId() {
		return relayServerId;
	}

	public void setSenderAddress(Address senderAddress) {
		this.senderAddress = senderAddress;
	}

	public Address getSenderAddress() {
		return senderAddress;
	}
	public void setRecipients(List<Address> recipients) {
		this.recipients = recipients;
	}
	public List<Address> getRecipients() {
		return recipients;
	}
	
	public void addRecipient(Address a) {
		
		recipients.add(a);
	}
	private void addRecipients(Element e, Address.RecipientType recipientType) {
		Element r = new Element(recipientType.toString(), e.getNamespace());
		for (Address a : recipients) {
			if (a.getRecipientType().equals(recipientType)) {
				r.addContent(a.save(e));
			}
		}
		if (r.getContentSize() > 0) {
			e.addContent(r);
		}
	}
	private List<Address> recipients = new ArrayList<Address>();
	private Address senderAddress;
	private String relayServerId;
	private String vaspId;
	private String vasId;
}
