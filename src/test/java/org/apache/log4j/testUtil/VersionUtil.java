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

public class VersionUtil {
    static final int DEFAULT_GUESS = 8;

    static public int getJavaMajorVersion() {
        String javaVersionString = System.getProperty("java.version");
        System.out.println("javaVersionString=" + javaVersionString);
        return getJavaMajorVersion(javaVersionString);
    }

    static public int getJavaMajorVersion(String versionString) {
        if (versionString == null)
            return DEFAULT_GUESS;
        if (versionString.startsWith("1.")) {
            return versionString.charAt(2) - '0';
        } else {
            String firstDigits = extractFirstDigits(versionString);
            try {
                return Integer.parseInt(firstDigits);
            } catch (NumberFormatException e) {
                return DEFAULT_GUESS;
            }
        }
    }

    private static String extractFirstDigits(String versionString) {
        StringBuffer buf = new StringBuffer();
        for (char c : versionString.toCharArray()) {
            if (Character.isDigit(c))
                buf.append(c);
            else
                break;
        }
        return buf.toString();

    }
}
