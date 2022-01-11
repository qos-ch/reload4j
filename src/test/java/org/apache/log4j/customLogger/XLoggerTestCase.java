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

package org.apache.log4j.customLogger;

import static org.apache.log4j.TestContants.TARGET_OUTPUT_PREFIX;
import static org.apache.log4j.TestContants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestContants.TEST_WITNESS_PREFIX;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.util.Compare;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.Log4jAndNothingElseFilter;
import org.apache.log4j.util.Transformer;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Test;

/**
 * Tests handling of custom loggers.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class XLoggerTestCase {

	static String FILTERED = TARGET_OUTPUT_PREFIX+"filtered";
	static XLogger logger = (XLogger) XLogger.getLogger(XLoggerTestCase.class);

	@After
	public void tearDown() {
		logger.getLoggerRepository().resetConfiguration();
	}

	@Test
	public void test1() throws Exception {
		common(1);
	}

	@Test
	public void test2() throws Exception {
		common(2);
	}

	void common(int number) throws Exception {
		DOMConfigurator.configure(TEST_INPUT_PREFIX + "xml/customLogger" + number + ".xml");

		int i = -1;

		logger.trace("Message " + ++i);
		logger.debug("Message " + ++i);
		logger.warn("Message " + ++i);
		logger.error("Message " + ++i);
		logger.fatal("Message " + ++i);
		Exception e = new Exception("Just testing");
		logger.debug("Message " + ++i, e);

		Transformer.transform(TARGET_OUTPUT_PREFIX+"xlogger.out", FILTERED,
				new Filter[] { new LineNumberFilter(), new Log4jAndNothingElseFilter() });
		assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"customLogger." + number));

	}

}
