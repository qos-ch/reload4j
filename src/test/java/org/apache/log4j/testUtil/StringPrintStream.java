/*
 * Copyright 2022 QOS.CH Sarl (Switzerland)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.testUtil;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StringPrintStream extends PrintStream {
    public static final String LINE_SEP = System.getProperty("line.separator");
    PrintStream other;
    public List<String> stringList = new ArrayList<String>();

    public StringPrintStream(PrintStream ps) {
	super(ps);
	other = ps;
    }

    public void print(String s) {
	other.print(s);
	stringList.add(s);
    }

    public void println(String s) {
	other.println(s);
	stringList.add(s);
    }

    public void println(Object o) {
	other.println(o);
	stringList.add(o.toString());
    }
}
