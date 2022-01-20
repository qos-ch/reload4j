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
package org.apache.log4j.jdbc;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.TestContants;
import org.apache.log4j.VectorErrorHandler;
import org.apache.log4j.xml.XLevel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JdbcAppenderTest {
    // Closing the last connection to "in-memory h2" removes the database
    // So we keep the connection during the test
    // The logger opens its own connection
    Connection con;

    Logger logger = Logger.getLogger(JdbcAppenderTest.class);

	
    @Before
    public void setup() throws SQLException {
	con = DriverManager.getConnection("jdbc:h2:mem:test_db");
	Statement st = con.createStatement();
	try {
	    st.execute(
		    "create table logs(level varchar(100),location varchar(100),message varchar(100),message2 varchar(100))");
	} finally {
	    st.close();
	}
    }

    @After
    public void cleanup() throws SQLException {
	LogManager.shutdown();
	con.close();
    }

    @Test
    public void verifyJdbcInsecure() throws SQLException {
	String secureJdbcReplacement = "org.apache.log4j.jdbc.JDBCAppender.secure_jdbc_replacement";
	System.setProperty(secureJdbcReplacement, "false");
	try {
	    PropertyConfigurator.configure(TestContants.TEST_INPUT_PREFIX + "jdbc_h2_bufferSize1.properties");
	    Appender jdbcAppender = LogManager.getRootLogger().getAppender("A");
	    // Keep errors
	    VectorErrorHandler errorHandler = new VectorErrorHandler();
	    jdbcAppender.setErrorHandler(errorHandler);

	    Logger logger = Logger.getLogger(JdbcAppenderTest.class);

	    String oldThreadName = Thread.currentThread().getName();
	    try {
		Thread.currentThread().setName("main");
		logger.debug("message with '' quote");
		Assert.assertEquals("batchSize=1, so messages should be added immediately",
			"DEBUG; org.apache.log4j.jdbc.JdbcAppenderTest; message with ' quote;  org.apache.log4j.jdbc.JdbcAppenderTest DEBUG message with ' quote\n",
			joinSorted(getMessagesFromDababase()));

		// It should fail
		logger.fatal("message with ' quote");

		Assert.assertEquals(
			"Inserting a message with ' should cause failure in insecure mode, so only one message should be in the DB",
			"DEBUG; org.apache.log4j.jdbc.JdbcAppenderTest; message with ' quote;  org.apache.log4j.jdbc.JdbcAppenderTest DEBUG message with ' quote\n",
			joinSorted(getMessagesFromDababase()));

		StringBuilder exceptions = new StringBuilder();
		StringBuilder errorCodes = new StringBuilder();
		for (int i = 0; i < errorHandler.size(); i++) {
		    Exception ex = errorHandler.getException(i);
		    exceptions.append(ex.toString());
		    errorCodes.append(ex instanceof SQLException
			    ? ("SQLException.getErrorCode() = " + ((SQLException) ex).getErrorCode())
			    : ("SQL Exception expected, got " + ex.getClass()));
		    exceptions.append('\n');
		    errorCodes.append('\n');
		}
		Assert.assertEquals(
			"Logging a message with ' should trigger SQLException 'Syntax error in SQL statement...' when using insecure JDBCAppender mode, actual exceptions are\n"
				+ exceptions,
			"SQLException.getErrorCode() = 42001\n", errorCodes.toString());
	    } finally {
		Thread.currentThread().setName(oldThreadName);
	    }
	} finally {
	    System.getProperties().remove(secureJdbcReplacement);
	}
    }

    @Test
    public void verifyJdbcBufferSize1() throws SQLException {
	PropertyConfigurator.configure(TestContants.TEST_INPUT_PREFIX + "jdbc_h2_bufferSize1.properties");


	String oldThreadName = Thread.currentThread().getName();
	try {
	    Thread.currentThread().setName("main");
	    logger.debug("message with ' quote");
	    Assert.assertEquals("batchSize=1, so messages should be added immediately",
		    "DEBUG; org.apache.log4j.jdbc.JdbcAppenderTest; message with ' quote;  org.apache.log4j.jdbc.JdbcAppenderTest DEBUG message with ' quote\n",
		    joinSorted(getMessagesFromDababase()));

	    logger.fatal("message with \" quote");

	    Assert.assertEquals("batchSize=1, so two messages should be in DB after two logging calls",
		    "DEBUG; org.apache.log4j.jdbc.JdbcAppenderTest; message with ' quote;  org.apache.log4j.jdbc.JdbcAppenderTest DEBUG message with ' quote\n"
			    + "FATAL; org.apache.log4j.jdbc.JdbcAppenderTest; message with \" quote;  org.apache.log4j.jdbc.JdbcAppenderTest FATAL message with \" quote\n",
		    joinSorted(getMessagesFromDababase()));
	} finally {
	    Thread.currentThread().setName(oldThreadName);
	}
    }

    @Test
    public void verifyJdbcBufferSize2() throws SQLException {
	PropertyConfigurator.configure(TestContants.TEST_INPUT_PREFIX + "jdbc_h2_bufferSize2.properties");

	String oldThreadName = Thread.currentThread().getName();
	try {
	    Thread.currentThread().setName("main");
	    logger.log(XLevel.TRACE, "xtrace message");
	    logger.debug("message with ' quote");
	    logger.info("message with \" quote");
	    logger.warn("?");
	    // bufferSize=2, so this messsage won't yet be stored to the db
	    logger.error("m4");

	    Assert.assertEquals(
		    "DEBUG; org.apache.log4j.jdbc.JdbcAppenderTest; message with ' quote;  org.apache.log4j.jdbc.JdbcAppenderTest DEBUG message with ' quote\n"
			    + "INFO; org.apache.log4j.jdbc.JdbcAppenderTest; message with \" quote;  org.apache.log4j.jdbc.JdbcAppenderTest INFO message with \" quote\n"
			    + "TRACE; org.apache.log4j.jdbc.JdbcAppenderTest; xtrace message;  org.apache.log4j.jdbc.JdbcAppenderTest TRACE xtrace message\n"
			    + "WARN; org.apache.log4j.jdbc.JdbcAppenderTest; ?;  org.apache.log4j.jdbc.JdbcAppenderTest WARN ?\n",
		    joinSorted(getMessagesFromDababase()));

	    logger.fatal("m5");

	    Assert.assertEquals("Logging m5 message should trigger buffer flush for both m4 and m5",
		    "DEBUG; org.apache.log4j.jdbc.JdbcAppenderTest; message with ' quote;  org.apache.log4j.jdbc.JdbcAppenderTest DEBUG message with ' quote\n"
			    + "ERROR; org.apache.log4j.jdbc.JdbcAppenderTest; m4;  org.apache.log4j.jdbc.JdbcAppenderTest ERROR m4\n"
			    + "FATAL; org.apache.log4j.jdbc.JdbcAppenderTest; m5;  org.apache.log4j.jdbc.JdbcAppenderTest FATAL m5\n"
			    + "INFO; org.apache.log4j.jdbc.JdbcAppenderTest; message with \" quote;  org.apache.log4j.jdbc.JdbcAppenderTest INFO message with \" quote\n"
			    + "TRACE; org.apache.log4j.jdbc.JdbcAppenderTest; xtrace message;  org.apache.log4j.jdbc.JdbcAppenderTest TRACE xtrace message\n"
			    + "WARN; org.apache.log4j.jdbc.JdbcAppenderTest; ?;  org.apache.log4j.jdbc.JdbcAppenderTest WARN ?\n",
		    joinSorted(getMessagesFromDababase()));
	} finally {
	    Thread.currentThread().setName(oldThreadName);
	}
    }

    private static String joinSorted(List<String> list) {
	Collections.sort(list);
	StringBuilder sb = new StringBuilder();
	for (String s : list) {
	    sb.append(s).append('\n');
	}
	return sb.toString();
    }

    private List<String> getMessagesFromDababase() throws SQLException {
	List<String> res = new ArrayList<String>();
	PreparedStatement ps = con.prepareStatement("select level,location,message,message2 from logs");
	ResultSet rs = ps.executeQuery();
	try {
	    while (rs.next()) {
		res.add(rs.getString(1) + "; " + rs.getString(2) + "; " + rs.getString(3) + "; " + rs.getString(4));
	    }
	} finally {
	    rs.close();
	}
	return res;
    }
}
