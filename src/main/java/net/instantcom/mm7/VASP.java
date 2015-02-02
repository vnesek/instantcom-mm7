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
 * MM7 Interface implemented by Value Added Service Provider (VASP) side. You
 * will need to implement it to receive messages. Configure an
 * {@link MM7Servlet} with VASP instance.
 *
 * TODO add all the other VASP methods
 */
public interface VASP {

	/**
	 * Handles message delivered from MMSC.
	 *
	 * @param deliverReq
	 *            MMS message delivered from MMSC.
	 *
	 * @return deliverRsp instance
	 *
	 * @throws MM7Error
	 *             if message can't be delivered
	 */
	DeliverRsp deliver(DeliverReq deliverReq) throws MM7Error;

	/**
	 * Context used for serializing/deserializing MM7 messages.
	 *
	 * @return context instance
	 */
	MM7Context getContext();
}
