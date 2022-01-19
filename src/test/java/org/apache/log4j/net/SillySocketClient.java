/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.net;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A silly client used send objects to SocketServer
 * 
 * @author ceki
 *
 */
public class SillySocketClient {

    static Logger LOGGER = LogManager.getLogger(SillySocketClient.class);
    static InetAddress LOCAL_HOST_ADDRESS;
    static String LOCALHOST_STR;

    static int PORT;

    ObjectOutputStream oos;

    public static void main(String[] args) throws IOException, InterruptedException {
	Properties props = System.getProperties();
//		for (Object key : props.keySet()) {
//			System.out.println(key + ":" + props.getProperty((String) key));
//		}

	if (args.length == 1)
	    init(args[0]);
	else {
	    usage("Wrong number of arguments.");
	    return;
	}
	LOCAL_HOST_ADDRESS = getAddressByName(LOCALHOST_STR);

	SillySocketClient ssc = new SillySocketClient();
	ssc.connect(LOCAL_HOST_ADDRESS, PORT);

	MDC.put("a", "aValue");

	// MDC carries objects as is!!
	// MDC.put("aKey", new BadPayload("aValue"));
	ssc.sendThisEvent(LOGGER, Level.INFO, "hello", null);

	Throwable t = new Exception("testing");
	ssc.sendThisEvent(LOGGER, Level.INFO, new BadPayload("x"), t);

	;
	Thread.sleep(200);
    }

    private void sendThisEvent(Logger logger, Level level, Object message, Throwable t) throws IOException {
	LoggingEvent le = makeLoggingEvent(logger, level, message, t);
	this.append(le);
    }

    private static LoggingEvent makeLoggingEvent(Logger logger, Level level, Object message, Throwable t) {
	LoggingEvent le = new LoggingEvent("", logger, level, message, t);
	return le;
    }

    static void init(String portStr) {
	try {
	    PORT = Integer.parseInt(portStr);
	} catch (java.lang.NumberFormatException e) {
	    e.printStackTrace();
	    usage("Could not interpret port number [" + portStr + "].");
	}
    }

    static void usage(String msg) {
	System.err.println(msg);
	System.err.println("Usage: java " + SillySocketClient.class.getName() + " port");
	System.exit(1);
    }

    static InetAddress getAddressByName(String host) throws java.net.UnknownHostException {
	return InetAddress.getByName(host);
    }

    void connect(InetAddress address, int port) {
	if (address == null)
	    return;
	try {
	    oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
	} catch (IOException e) {
	    if (e instanceof InterruptedIOException) {
		Thread.currentThread().interrupt();
	    }
	    String msg = "Could not connect to remote log4j server at [" + address.getHostName() + "].";
	    LogLog.error(msg);
	}
    }

    public void writeObject(Object o) throws IOException {
	oos.writeObject(o);
	oos.flush();
    }

    public void append(LoggingEvent event) throws IOException {
	event.getNDC();
	event.getThreadName();
	event.getMDCCopy();
	event.getRenderedMessage();
	event.getThrowableStrRep();

	writeObject(event);
    }

}
