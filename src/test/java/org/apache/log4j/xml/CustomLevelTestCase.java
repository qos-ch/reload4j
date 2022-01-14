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

package org.apache.log4j.xml;

import static org.apache.log4j.TestContants.TARGET_OUTPUT_PREFIX;
import static org.apache.log4j.TestContants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestContants.TEST_WITNESS_PREFIX;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.apache.log4j.util.Compare;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomLevelTestCase  {

  static String TEMP = TARGET_OUTPUT_PREFIX+"customLevel.out";

  Logger root;
  Logger logger;


  @Before
  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(CustomLevelTestCase.class);
  }

  @After
  public void tearDown() {
    root.getLoggerRepository().resetConfiguration();
  }

  @Test
  public void test1() throws Exception {
    DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/customLevel1.xml");
    common();
    assertTrue(Compare.compare(TEMP, TEST_WITNESS_PREFIX+"customLevel.1"));
  }

  @Test
  public void test2() throws Exception {
    DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/customLevel2.xml");
    common();
    assertTrue(Compare.compare(TEMP, TEST_WITNESS_PREFIX+"customLevel.2"));
  }

  @Test
  public void test3() throws Exception {
    DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/customLevel3.xml");
    common();
    assertTrue(Compare.compare(TEMP, TEST_WITNESS_PREFIX+"customLevel.3"));
  }

  @Test
  public void test4() throws Exception {
    DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/customLevel4.xml");
    common();
    assertTrue(Compare.compare(TEMP, TEST_WITNESS_PREFIX+"customLevel.4"));
  }


  void common() {
    int i = 0;
    logger.debug("Message " + ++i);
    logger.info ("Message " + ++i);
    logger.warn ("Message " + ++i);
    logger.error("Message " + ++i);
    logger.log(XLevel.TRACE, "Message " + ++i);
  }


}
