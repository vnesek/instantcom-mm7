instantcom-mm7 Java client-server MMS MM7 library
=================================================

Java 1.6+ client and server implementation of MMS MM7 protocol.

MM7 is the interface between MMSC and a value-added service provider (VASP). Can be used 
for sending and receiving messages. It is based on SOAP with attachments, using HTTP as 
the transport protocol.

See http://en.wikipedia.org/wiki/MM7_(MMS)#MM7

Features
--------
* Supports Java 1.6+. Should work with 1.5 if needed.
* Supports both client (VASP) and server (MMSC) operations.
* No external dependecies other than JDOM 2+ and mimepull 1.9+.
* Manual SOAP message + attachments parsing. JAXP, SAAJ and it's ilk have a problem with MM7.
* Supports any MM7 namespace. MM7 uses namespaces for versioning making client work with
  multiple servers hard.
* Dual licensed under GPL v2 and CDDL.

Status
------
Currently it is work in progress. You can use it to connect to MMSC, tested with Nokia MMSC
and few other implementations. Server side implementation is incomplete as well client
functions other than basic message sending. Need more javadocs and tests.

In production use with several mobile operators.  

Building
--------
To produce instantcom-mm7.jar you will need apache maven installed. Run:

> mvn clean package

Usage
-----

* Send a textual MMS message to MMSC.

```java
		SubmitReq sr = new SubmitReq();
		sr.setVaspId("xxx_vaspid");
		sr.setVasId("xxx_vasid");
		sr.setSubject("Nice weather");
		sr.setMessageClass(MessageClass.INFORMATIONAL);
		sr.setServiceCode("7007");
		sr.addRecipient(new Address("+385910000001", RecipientType.TO));

		// Add text content
		TextContent text = new TextContent("We got a real nice weather today.");
		text.setContentId("text");
		sr.setContent(text);

		// Initialize MM7 client to MMSC
		MMSC mmsc = new BasicMMSC(url);

		// Send a message
		SubmitRsp submitRsp = mmsc.submit(sr);
		System.out.println(submitRsp);
```

License
-------
Dual licensed under CDDL v1.1 and GPL v2. See LICENSE.txt

Author contact and support
--------------------------
For any further information please contact Vjekoslav Nesek (vnesek@instantcom.net)
