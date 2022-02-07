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

package org.apache.log4j.defaultInit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.TestConstants;

public class DefaultInitTest2 {

    @Before
    public void setUp() {
	String userDir = System.getProperty("user.dir");
	System.setProperty("log4j.configuration",
		"file:///" + userDir + "/" + TestConstants.TEST_INPUT_PREFIX + "xml/defaultInit.xml");

    }

    @After
    public void tearDown() {
	System.clearProperty("log4j.configuration");
	LogManager.shutdown();
    }

    @Test
    public void xmlTest() {
	Logger root = Logger.getRootLogger();
	boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
	assertTrue(rootIsConfigured);
	@SuppressWarnings("unchecked")
	Enumeration<Appender> e = root.getAllAppenders();
	Appender appender = (Appender) e.nextElement();
	assertEquals(appender.getName(), "D1");
    }

}
