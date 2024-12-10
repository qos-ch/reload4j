package org.apache.log4j.xml;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.util.Compare;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.log4j.TestConstants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestConstants.TEST_WITNESS_PREFIX;
import static org.junit.Assert.*;

public class DOM_NewAsyncAppenderTestCase {
    Logger root;

    @Before
    public void setUp() {
        root = Logger.getRootLogger();
    }

    @After
    public void tearDown() {
        System.out.println("----tearing down ");
        root.getLoggerRepository().resetConfiguration();
    }


    @Test
    public void smoke() throws Exception {
        String msg = "hello";
        DOMConfigurator.configure(TEST_INPUT_PREFIX + "xml/newAsyncAppender0.xml");
        root.info(msg);

        Appender appender = root.getAppender("ASYNC");
        assertTrue(appender instanceof NewAsyncAppender);

        NewAsyncAppender newAsyncAppender = (NewAsyncAppender) appender;

        VectorAppender vectorAppender = (VectorAppender) newAsyncAppender.getAppender("V1");
        assertNotNull(vectorAppender);


        Thread.currentThread().sleep(100);

        assertEquals(1, vectorAppender.getVector().size());

        LoggingEvent event = (LoggingEvent) vectorAppender.vector.elementAt(0);

        assertEquals(msg, event.getMessage());


    }
}
