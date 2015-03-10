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
import java.util.Date;
import java.util.List;

import net.instantcom.mm7.Address.RecipientType;

import org.jdom2.Element;

public class SubmitReq extends MM7Request implements HasContent {

	public void addRecipient(Address a) {
		recipients.add(a);
	}

	public Boolean getAllowAdaptations() {
		return allowAdaptations;
	}

	public String getApplicID() {
		return applicID;
	}

	public String getAuxApplicId() {
		return auxApplicId;
	}

	public ChargedParty getChargedParty() {
		return chargedParty;
	}

	public String getChargedPartyId() {
		return chargedPartyId;
	}

	public Content getContent() {
		return content;
	}

	public ContentClass getContentClass() {
		return contentClass;
	}

	public List<Integer> getDeliveryCondition() {
		return deliveryCondition;
	}

	public Boolean getDeliveryReport() {
		return deliveryReport;
	}

	public Boolean getDistributionIndicator() {
		return distributionIndicator;
	}

	public Boolean getDrmContent() {
		return drmContent;
	}

	public RelativeDate getEarlistDeliveryTime() {
		return earlistDeliveryTime;
	}

	public RelativeDate getExpiryDate() {
		return expiryDate;
	}

	public String getLinkedId() {
		return linkedId;
	}

	public MessageClass getMessageClass() {
		return messageClass;
	}

	public Priority getPriority() {
		return priority;
	}

	public Boolean getReadReply() {
		return readReply;
	}

	public List<Address> getRecipients() {
		return recipients;
	}

	public String getReplyApplicID() {
		return replyApplicID;
	}

	public Integer getReplyChargingSize() {
		return replyChargingSize;
	}

	public RelativeDate getReplyDeadline() {
		return replyDeadline;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getSubject() {
		return subject;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public Element save(Element parent) {
		Element e = super.save(parent);
		e.setName("SubmitReq");
		if (!recipients.isEmpty()) {
			Element r = new Element("Recipients", e.getNamespace());
			addRecipients(r, RecipientType.TO);
			addRecipients(r, RecipientType.CC);
			addRecipients(r, RecipientType.BCC);
			if (r.getContentSize() > 0) {
				e.addContent(r);
			}
		}
		if (serviceCode != null) {
			e.addContent(new Element("ServiceCode", e.getNamespace()).setText(serviceCode));
		}
		if (linkedId != null) {
			e.addContent(new Element("LinkedID", e.getNamespace()).setText(linkedId));
		}
		if (messageClass != null) {
			e.addContent(new Element("MessageClass", e.getNamespace()).setText(messageClass.toString()));
		}
		if (timeStamp != null) {
			e.addContent(new Element("TimeStamp", e.getNamespace()).setText(new RelativeDate(timeStamp).toString()));
		}
		if (replyChargingSize != null) {
			e.addContent(new Element("ReplyChargingSize", e.getNamespace()).setText(replyChargingSize.toString()));
		}
		if (replyDeadline != null) {
			e.addContent(new Element("ReplyDeadline", e.getNamespace()).setText(replyDeadline.toString()));
		}
		if (earlistDeliveryTime != null) {
			e.addContent(new Element("EarlistDeliveryTime", e.getNamespace()).setText(earlistDeliveryTime.toString()));
		}
		if (expiryDate != null) {
			e.addContent(new Element("ExpiryDate", e.getNamespace()).setText(expiryDate.toString()));
		}
		if (deliveryReport != null) {
			e.addContent(new Element("DeliveryReport", e.getNamespace()).setText(deliveryReport? "True" : "False"));
		}
		if (readReply != null) {
			e.addContent(new Element("ReadReply", e.getNamespace()).setText(readReply? "True" : "False"));
		}
		if (priority != null) {
			e.addContent(new Element("Priority", e.getNamespace()).setText(priority.toString()));
		}
		if (subject != null) {
			e.addContent(new Element("Subject", e.getNamespace()).setText(subject.toString()));
		}
		if (chargedParty != null) {
			e.addContent(new Element("ChargedParty", e.getNamespace()).setText(chargedParty.toString()));
		}
		if (chargedPartyId != null) {
			e.addContent(new Element("ChargedPartyID", e.getNamespace()).setText(chargedPartyId.toString()));
		}
		if (distributionIndicator != null) {
			e.addContent(new Element("DistributionIndicator", e.getNamespace()).setText(distributionIndicator? "True" : "False"));
		}

		// deliveryCondition
		if (applicID != null) {
			e.addContent(new Element("ApplicID", e.getNamespace()).setText(applicID));
		}
		if (replyApplicID != null) {
			e.addContent(new Element("ReplyApplicID", e.getNamespace()).setText(replyApplicID));
		}
		if (auxApplicId != null) {
			e.addContent(new Element("AuxApplicId", e.getNamespace()).setText(auxApplicId));
		}
		if (contentClass != null) {
			e.addContent(new Element("ContentClass", e.getNamespace()).setText(contentClass.toString()));
		}
		if (drmContent != null) {
			e.addContent(new Element("DRMContent", e.getNamespace()).setText(drmContent? "True" : "False"));
		}
		if (content != null) {
			Element c = new Element("Content", e.getNamespace());
			if (allowAdaptations != null) {
				c.setAttribute("allowAdaptations", allowAdaptations? "True" : "False");
			}
			c.setAttribute("href", "cid:mm7-content");
			e.addContent(c);
		}

		return e;
	}

	public void setAllowAdaptations(Boolean allowAdaptations) {
		this.allowAdaptations = allowAdaptations;
	}

	public void setApplicID(String applicID) {
		this.applicID = applicID;
	}

	public void setAuxApplicId(String auxApplicId) {
		this.auxApplicId = auxApplicId;
	}

	public void setChargedParty(ChargedParty chargedParty) {
		this.chargedParty = chargedParty;
	}

	public void setChargedPartyId(String chargedPartyId) {
		this.chargedPartyId = chargedPartyId;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public void setContentClass(ContentClass contentClass) {
		this.contentClass = contentClass;
	}

	public void setDeliveryCondition(List<Integer> deliveryCondition) {
		this.deliveryCondition = deliveryCondition;
	}

	public void setDeliveryReport(Boolean deliveryReport) {
		this.deliveryReport = deliveryReport;
	}

	public void setDistributionIndicator(Boolean distributionIndicator) {
		this.distributionIndicator = distributionIndicator;
	}

	public void setDrmContent(Boolean drmContent) {
		this.drmContent = drmContent;
	}

	public void setEarlistDeliveryTime(RelativeDate earlistDeliveryTime) {
		this.earlistDeliveryTime = earlistDeliveryTime;
	}

	public void setExpiryDate(RelativeDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setLinkedId(String linkedId) {
		this.linkedId = linkedId;
	}

	public void setMessageClass(MessageClass messageClass) {
		this.messageClass = messageClass;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public void setReadReply(Boolean readReply) {
		this.readReply = readReply;
	}

	public void setRecipients(List<Address> recipients) {
		this.recipients = recipients;
	}

	public void setReplyApplicID(String replyApplicID) {
		this.replyApplicID = replyApplicID;
	}

	public void setReplyChargingSize(Integer replyChargingSize) {
		this.replyChargingSize = replyChargingSize;
	}

	public void setReplyDeadline(RelativeDate replyDeadline) {
		this.replyDeadline = replyDeadline;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
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
	private String serviceCode;
	private String linkedId;
	private MessageClass messageClass = MessageClass.INFORMATIONAL;
	private Date timeStamp;
	private Integer replyChargingSize;
	private RelativeDate replyDeadline;
	private RelativeDate earlistDeliveryTime;
	private RelativeDate expiryDate;
	private Boolean deliveryReport;
	private Boolean readReply;
	private Priority priority;
	private String subject;
	private ChargedParty chargedParty;
	private String chargedPartyId;
	private Boolean distributionIndicator;
	private List<Integer> deliveryCondition;
	private String applicID;
	private String replyApplicID;
	private String auxApplicId;
	private ContentClass contentClass;
	private Boolean drmContent;
	private Boolean allowAdaptations;
	private Content content;
}
