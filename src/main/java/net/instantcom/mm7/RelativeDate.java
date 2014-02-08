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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class RelativeDate {

	private static DatatypeFactory dataTypeFactory;

	static {
		try {
			dataTypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public RelativeDate(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		XMLGregorianCalendar xcal;
		synchronized (dataTypeFactory) {
			xcal = dataTypeFactory.newXMLGregorianCalendar(cal);
		}
		this.representation = xcal.toXMLFormat();
	}
	
	public RelativeDate(String representation) {
		this.representation = representation;
	}
	
	public Date toDate() {
		if (representation == null) {
			return null;
		}
		XMLGregorianCalendar xcal;
		synchronized (dataTypeFactory) {
			xcal = dataTypeFactory.newXMLGregorianCalendar(representation);
		}
		return xcal.toGregorianCalendar().getTime();
	}

	@Override
	public String toString() {
		return representation;
	}

	private String representation;
}

/*
public static void main(String[] args) throws DatatypeConfigurationException {
	XMLGregorianCalendar cal = dataTypeFactory.newXMLGregorianCalendar("P90D" * "2002-01-02T09:30:47-05:00" *);
	System.out.println(cal.toGregorianCalendar().getTime());
}
*/