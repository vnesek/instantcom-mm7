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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		MM7Message.save(req, byteos, new MM7Context());
		
		req = (DeliverReq) MM7Response.load(new ByteArrayInputStream(byteos.toByteArray()), req.getSoapContentType(), new MM7Context());
		
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
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		MM7Message.save(req, byteos, new MM7Context());
		
		req = (DeliverReq) MM7Response.load(new ByteArrayInputStream(byteos.toByteArray()), req.getSoapContentType(), new MM7Context());
		
		assertEquals("xmmc", req.getVasId());
		assertEquals("xmmc1", req.getRelayServerId());
		assertEquals("+381600001200", req.getSender().toString());
		assertEquals("(no subject)", req.getSubject());
		assertEquals(Priority.NORMAL, req.getPriority());
	}
	
	@Test
	public void readCaiXinDatafromHW() throws IOException, MM7Error {
		String ct = "multipart/related; boundary=\"--NextPart_0_9094_20600\"; type=text/xml";
		InputStream in = DeliverReq.class.getResourceAsStream("caixin.txt");
		DeliverReq req = (DeliverReq) MM7Response.load(in, ct, new MM7Context());


		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = null ;
		try{
			post = new HttpPost("http://127.0.0.1:8080/mm7serv/10085receiver");
			//post = new HttpPost("http://42.96.185.95:8765");
			post.addHeader("Content-Type", req.getSoapContentType());
			post.addHeader("SOAPAction", "");
			ByteArrayOutputStream byteos = new ByteArrayOutputStream();
			MM7Message.save(req, byteos, new MM7Context());
			post.setEntity(new ByteArrayEntity(byteos.toByteArray()));
			HttpResponse resp = httpclient.execute(post);
			HttpEntity entity = resp.getEntity();
			String message = EntityUtils.toString(entity, "utf-8");
			System.out.println(message);
		}finally{
			if(post!=null)	post.releaseConnection();
		}
		
	/*	for(Content c : req.getContent()){
			ContentType ctype = new ContentType(c.getContentType());
			if(ctype.getPrimaryType().equals("image")){
				BinaryContent image = (BinaryContent)c;
				String fileName = DeliverReq.class.getResource("caixin.txt").getFile()+".jpg";
				System.out.println(fileName);
				Base64 base64 = new Base64();
				FileUtils.writeByteArrayToFile(new File(fileName), base64.encode(image.getData()));
			}
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		MM7Message.save(req, byteos, new MM7Context());
		
		req = (DeliverReq) MM7Response.load(new ByteArrayInputStream(byteos.toByteArray()), req.getSoapContentType(), new MM7Context());
		assertEquals("+8618703815655", req.getSender().toString());*/
	}
	
	@Test
	public void sendrequest() throws IOException, MM7Error {
		String ct = "multipart/related; boundary=\"Nokia-mm-messageHandler-BoUnDaRy-=_-735647067\"; type=text/xml";
		InputStream in = DeliverReq.class.getResourceAsStream("deliver-req6-hw.txt");
		DeliverReq req = (DeliverReq) MM7Response.load(in, ct, new MM7Context());
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		MM7Message.save(req, byteos, new MM7Context());
		
		req = (DeliverReq) MM7Response.load(new ByteArrayInputStream(byteos.toByteArray()), req.getSoapContentType(), new MM7Context());
		assertEquals(null, req.getVasId());
		assertEquals("910000", req.getRelayServerId());
		assertEquals("13951900000", req.getSender().toString());
		assertEquals("This is a test", req.getSubject());
		assertEquals(Priority.LOW, req.getPriority());
		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = null ;
		try{
			post = new HttpPost("http://127.0.0.1:55603");
			//post = new HttpPost("http://42.96.185.95:8765");
			post.addHeader("Content-Type", req.getSoapContentType());
			post.addHeader("SOAPAction", "");
			 byteos = new ByteArrayOutputStream();
			MM7Message.save(req, byteos, new MM7Context());
			post.setEntity(new ByteArrayEntity(byteos.toByteArray()));
			HttpResponse resp = httpclient.execute(post);
			HttpEntity entity = resp.getEntity();
			String message = EntityUtils.toString(entity, "utf-8");
			System.out.println(message);
		}finally{
			if(post!=null)	post.releaseConnection();
		}
	}
}
