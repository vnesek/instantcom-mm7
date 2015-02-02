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

import org.jdom2.Element;
import org.jdom2.Namespace;

interface JDOMSupport {

	/**
	 * Save object's state to a new JDOM element.
	 *
	 * @param parent
	 *            of a element
	 * @return JDOM element with serialized object' state
	 */
	Element save(Element parent);

	/**
	 * Loads object's state from an JDOM Element.
	 *
	 * @param element
	 *            to load state from
	 */
	void load(Element element);

	public static final Namespace ENVELOPE = Namespace.getNamespace("env", "http://schemas.xmlsoap.org/soap/envelope/");

}
