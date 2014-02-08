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

/**
 * Version and other misc information.
 */
interface About {

	/** Default MM7 version used */
	String MM7_VERSION = "6.7.0";
	
	/** Default MM7 XML namespace used */
	String MM7_NAMESPACE = "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-6-7";
	
	/** Library version */
	String VERSION = "0.8.0";

	/** Library copyright */
	String COPYRIGHT = "Copyright (c) InstantCom d.o.o. 2007-2014. All rights reserved.";

	/** Serial version UID used by classes in a package */
	long SERIAL_VERSION_UID = Long.parseLong(VERSION.replace('.', '0'));
}
