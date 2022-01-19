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

// Log4j uses the JUnit framework for internal unit testing. JUnit
// is available from "http://www.junit.org".

package org.apache.log4j.helpers;

import static org.apache.log4j.TestContants.TEST_INPUT_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfiguratorTest;
import org.apache.log4j.xml.XLevel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test variable substitution code.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * 
 * @since 1.0
 */
public class OptionConverterTestCase {

    Properties props;

    @Before
    public void setUp() {
	props = new Properties();
	props.put("TOTO", "wonderful");
	props.put("key1", "value1");
	props.put("key2", "value2");
	// Log4J will NPE without this:
	props.put("line.separator", System.getProperty("line.separator"));
	// Log4J will throw an Error without this:
	props.put("java.home", System.getProperty("java.home"));
	System.setProperties(props);

    }

    @After
    public void tearDown() {
	props = null;
	LogManager.resetConfiguration();
    }

    @Test
    public void varSubstTest1() {
	String r;

	r = OptionConverter.substVars("hello world.", null);
	assertEquals("hello world.", r);

	r = OptionConverter.substVars("hello ${TOTO} world.", null);

	assertEquals("hello wonderful world.", r);
    }

    @Test
    public void varSubstTest2() {
	String r;

	r = OptionConverter.substVars("Test2 ${key1} mid ${key2} end.", null);
	assertEquals("Test2 value1 mid value2 end.", r);
    }

    @Test
    public void varSubstTest3() {
	String r;

	r = OptionConverter.substVars("Test3 ${unset} mid ${key1} end.", null);
	assertEquals("Test3  mid value1 end.", r);
    }

    @Test
    public void varSubstTest4() {
	String val = "Test4 ${incomplete ";
	try {
	    OptionConverter.substVars(val, null);
	} catch (IllegalArgumentException e) {
	    String errorMsg = e.getMessage();
	    // System.out.println('['+errorMsg+']');
	    assertEquals('"' + val + "\" has no closing brace. Opening brace at position 6.", errorMsg);
	}
    }

    @Test
    public void varSubstTest5() {
	Properties props = new Properties();
	props.put("p1", "x1");
	props.put("p2", "${p1}");
	String res = OptionConverter.substVars("${p2}", props);
	System.out.println("Result is [" + res + "].");
	assertEquals(res, "x1");
    }

    /**
     * Tests configuring Log4J from an InputStream.
     * 
     * @since 1.2.17
     */

    @Test
    public void testInputStream() throws IOException {
	File file = new File(TEST_INPUT_PREFIX + "filter1.properties");
	assertTrue(file.exists());
	FileInputStream inputStream = new FileInputStream(file);
	try {
	    OptionConverter.selectAndConfigure(inputStream, null, LogManager.getLoggerRepository());
	} finally {
	    inputStream.close();
	}
	new PropertyConfiguratorTest(this.getClass().getName()).validateNested();
    }

    @Test
    public void toLevelTest1() {
	String val = "INFO";
	Level p = OptionConverter.toLevel(val, null);
	assertEquals(p, Level.INFO);
    }

    @Test
    public void toLevelTest2() {
	String val = "INFO#org.apache.log4j.xml.XLevel";
	Level p = OptionConverter.toLevel(val, null);
	assertEquals(p, Level.INFO);
    }

    @Test
    public void toLevelTest3() {
	String val = "TRACE#org.apache.log4j.xml.XLevel";
	Level p = OptionConverter.toLevel(val, null);
	assertEquals(p, XLevel.TRACE);
    }

    @Test
    public void toLevelTest4() {
	String val = "TR#org.apache.log4j.xml.XLevel";
	Level p = OptionConverter.toLevel(val, null);
	assertEquals(p, null);
    }

    @Test
    public void toLevelTest5() {
	String val = "INFO#org.apache.log4j.xml.TOTO";
	Level p = OptionConverter.toLevel(val, null);
	assertEquals(p, null);
    }

}
