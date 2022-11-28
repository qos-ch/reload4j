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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JdbcPatternParserTest {

    String[] EMPTY_STRING_ARRAY = new String[] {};

    @Test
    public void testSingleQuotesAndSpaces() {
        ParserState expected = new ParserState("INSERT INTO A1 (TITLE3) VALUES ( ?, ?, ?, ?,  ?, ? )", "%d", "%t",
                "%-5p", " '%c", "%x", "  -  %m%n ");
        assertParserStateEquality("INSERT INTO A1 (TITLE3) VALUES ( '%d', '%t', '%-5p', ' ''%c',  '%x', '  -  %m%n ' )",
                expected);
    }

    @Test
    public void testWithLiteralsAndSingleQuotes() {

        String prefix = "INSERT INTO A1 (TITLE3) VALUES ( ' aString', 'anotherString with '' xyz'";

        ParserState expected = new ParserState(prefix + ", ?)", "message: %m");
        assertParserStateEquality(prefix + ", 'message: %m')", expected);
    }

    @Test
    public void testMixedPatterns() {
        ParserState expected = new ParserState("INSERT INTO A1 (TITLE3) VALUES ( ?, ?, ?, ?,  ?, ? )", "%d", "%d",
                "%-5p", " %c", "%x", "  -  %m%n");
        assertParserStateEquality("INSERT INTO A1 (TITLE3) VALUES ( '%d', '%d', '%-5p', ' %c',  '%x', '  -  %m%n' )",
                expected);
    }

    @Test
    public void testSingleLumpedValue() {
        ParserState expected = new ParserState("INSERT INTO A1 (TITLE3) VALUES ( ? )",
                " %d  -  %c %-5p %c %x  -  %m%n ");
        assertParserStateEquality("INSERT INTO A1 (TITLE3) VALUES ( ' %d  -  %c %-5p %c %x  -  %m%n ' )", expected);
    }

    private void assertParserStateEquality(String input, ParserState expected) {
        JdbcPatternParser parser = new JdbcPatternParser(input);
        List<String> patternStringReps = parser.getUnmodifiablePatternStringRepresentationList();

        ParserState actual = new ParserState(parser.getParameterizedSql(),
                patternStringReps.toArray(EMPTY_STRING_ARRAY));

        Assert.assertEquals(expected, actual);
    }

    // this class represents JdbcPatternParser internal state
    private static class ParserState {
        String statementStr;
        String[] args;

        ParserState(String statementStr, String... args) {
            this.statementStr = statementStr;
            this.args = args;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(args);
            result = prime * result + ((statementStr == null) ? 0 : statementStr.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "ParserState [statement=" + statementStr + ", args=" + Arrays.toString(args) + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ParserState other = (ParserState) obj;
            if (!Arrays.equals(args, other.args))
                return false;
            if (statementStr == null) {
                if (other.statementStr != null)
                    return false;
            } else if (!statementStr.equals(other.statementStr))
                return false;
            return true;
        }

    }
}
