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

package org.apache.log4j;

import static org.apache.log4j.TestConstants.TARGET_OUTPUT_PREFIX;
import static org.apache.log4j.TestConstants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestConstants.TEST_WITNESS_PREFIX;

import org.apache.log4j.util.Compare;
import org.apache.log4j.xml.XLevel;
import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

/**
 * Test the configuration of the hierarchy-wide threshold.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class HierarchyThresholdTest extends TestCase {

    static String THT_PREFIX = TARGET_OUTPUT_PREFIX + "hierarchyThreshold";
    static Logger logger = Logger.getLogger(HierarchyThresholdTest.class);

    public HierarchyThresholdTest(String name) {
        super(name);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        logger.getLoggerRepository().resetConfiguration();
    }

    public void test1() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold1.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "1.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.1"));
    }

    public void test2() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold2.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "2.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.2"));
    }

    public void test3() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold3.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "3.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.3"));
    }

    public void test4() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold4.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "4.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.4"));
    }

    public void test5() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold5.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "5.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.5"));
    }

    public void test6() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold6.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "6.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.6"));
    }

    public void test7() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold7.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "7.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.7"));
    }

    public void test8() throws Exception {
        PropertyConfigurator.configure(TEST_INPUT_PREFIX + "hierarchyThreshold8.properties");
        common();
        assertTrue(Compare.compare(THT_PREFIX + "8.log", TEST_WITNESS_PREFIX + "hierarchyThreshold.8"));
    }

    static void common() {
        String oldThreadName = Thread.currentThread().getName();
        Thread.currentThread().setName("main");

        logger.log(XLevel.TRACE, "m0");
        logger.debug("m1");
        logger.info("m2");
        logger.warn("m3");
        logger.error("m4");
        logger.fatal("m5");

        Thread.currentThread().setName(oldThreadName);
    }

}
