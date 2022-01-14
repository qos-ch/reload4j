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

import javax.naming.Context;
import javax.naming.NamingException;

public class JNDIUtil {

	// See https://jakarta.ee/specifications/platform/8/platform-spec-8.html#a616
	// there are the java:comp, java:module, java:app, java:global namespaces
	public static final String JNDI_JAVA_NAMESPACE = "java:";

	static final String RESTRICTION_MSG = "JNDI name must start with " + JNDI_JAVA_NAMESPACE + " but was ";

	public static Object lookupObject(Context ctx, String name) throws NamingException {
		if (ctx == null) {
			return null;
		}

		if (isNullOrEmpty(name)) {
			return null;
		}

		jndiNameSecurityCheck(name);

		Object lookup = ctx.lookup(name);
		return lookup;
	}

	private static boolean isNullOrEmpty(String str) {
		return ((str == null) || str.trim().length() == 0);
	}

	public static void jndiNameSecurityCheck(String name) throws NamingException {
		if (!name.startsWith(JNDI_JAVA_NAMESPACE)) {
			throw new NamingException(RESTRICTION_MSG + name);
		}
	}
}
