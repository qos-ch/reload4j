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

import static org.apache.log4j.TestContants.TEST_INPUT_PREFIX;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for SMTPAppender.
 */
public class SMTPAppenderTest {

    Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * Reset configuration after every test.
     */
    @After
    public void tearDown() {
	LogManager.resetConfiguration();
    }

    /**
     * Trivial implementation of TriggeringEventEvaluator.
     */
    public static final class MockTriggeringEventEvaluator implements TriggeringEventEvaluator {
	/**
	 * {@inheritDoc}
	 */
	public boolean isTriggeringEvent(final LoggingEvent event) {
	    return true;
	}
    }

    /**
     * Tests that triggeringPolicy element will set evaluator.
     */
    @Test
    public void testTrigger() {
	DOMConfigurator.configure(TEST_INPUT_PREFIX + "xml/smtpAppender1.xml");
	SMTPAppender appender = (SMTPAppender) Logger.getRootLogger().getAppender("A1");
	TriggeringEventEvaluator evaluator = appender.getEvaluator();
	assertTrue(evaluator instanceof MockTriggeringEventEvaluator);
    }
    
    @Ignore // manual testing to verify SMTPS protocol
    @Test
    public void testWithSMTPS() {
	DOMConfigurator.configure(TEST_INPUT_PREFIX + "xml/smtpAppender2.xml");
	SMTPAppender appender = (SMTPAppender) Logger.getRootLogger().getAppender("SMTP");
	TriggeringEventEvaluator evaluator = appender.getEvaluator();
	assertTrue(evaluator instanceof MockTriggeringEventEvaluator);
	logger.debug("hello");
    }
}
