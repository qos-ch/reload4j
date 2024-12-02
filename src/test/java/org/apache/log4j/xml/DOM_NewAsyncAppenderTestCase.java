package org.apache.log4j.xml;

import org.apache.log4j.*;
import org.apache.log4j.util.Compare;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.log4j.TestConstants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestConstants.TEST_WITNESS_PREFIX;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DOM_NewAsyncAppenderTestCase {
    Logger root;

    @Before
    public void setUp() {
        root = Logger.getRootLogger();
    }

    @After
    public void tearDown() {
        root.getLoggerRepository().resetConfiguration();
    }


    @Test
    public void test1() throws Exception {
        DOMConfigurator.configure(TEST_INPUT_PREFIX + "xml/newAsyncAppender0.xml");
        Appender appender = root.getAppender("ASYNC");
        assertTrue(appender instanceof NewAsyncAppender);

        NewAsyncAppender newAsyncAppender = (NewAsyncAppender) appender;

        VectorAppender vectorAppender = (VectorAppender) newAsyncAppender.getAppender("V1");
        assertNotNull(vectorAppender);
        

    }
}
