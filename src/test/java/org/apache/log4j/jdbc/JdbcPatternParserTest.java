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

import org.junit.Assert;
import org.junit.Test;

public class JdbcPatternParserTest {
    JdbcPatternParser parser = new JdbcPatternParser();

    @Test
    public void testParameterizedSql() {
	assertParameterizedSql(
		"JdbcPatternParser{sql=INSERT INTO A1 (TITLE3) VALUES ( ? ),args=[ %d  -  %c %-5p %c %x  -  %m%n ]}",
		"INSERT INTO A1 (TITLE3) VALUES ( ' %d  -  %c %-5p %c %x  -  %m%n ' )");
	assertParameterizedSql(
		"JdbcPatternParser{sql=INSERT INTO A1 (TITLE3) VALUES ( ?, ?, ?, ?,  ?, ? ),args=[%d, %c, %-5p,  '%c, %x,   -  %m%n ]}",
		"INSERT INTO A1 (TITLE3) VALUES ( '%d', '%c', '%-5p', ' ''%c',  '%x', '  -  %m%n ' )");

	assertParameterizedSql(
		"JdbcPatternParser{sql=INSERT INTO A1 (TITLE3) VALUES ( ' just string literal', 'another literal with quotes '' asdf', ?),args=[message: %m]}",
		"INSERT INTO A1 (TITLE3) VALUES ( ' just string literal', 'another literal with quotes '' asdf', 'message: %m')");
    }

    private void assertParameterizedSql(String expected, String input) {
	parser.setPattern(input);
	Assert.assertEquals("parser.setPattern(...).toString() for " + input, expected, parser.toString());
    }
}
