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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.UtilLoggingLevel;
import org.apache.log4j.spi.LoggingEvent;

import junit.framework.TestCase;

/**
 * Verifies that the level class named inside a serialized {@link LoggingEvent}
 * is loaded by {@code readLevel} only when it is a genuine {@link org.apache.log4j.Level}
 * subclass, so that a crafted event arriving over a socket cannot have an
 * arbitrary class loaded and initialized by name.
 */
public class LevelDeserializationTest extends TestCase {

    public LevelDeserializationTest(final String name) {
        super(name);
    }

    /** A genuine Level subclass still round-trips through the hardened stream. */
    public void testLevelSubclassStillDeserializes() throws Exception {
        byte[] bytes = serializeEventWithLevelClassName(UtilLoggingLevel.class.getName());
        HardenedLoggingEventInputStream in = new HardenedLoggingEventInputStream(new ByteArrayInputStream(bytes));
        LoggingEvent event = (LoggingEvent) in.readObject();
        in.close();
        assertEquals(UtilLoggingLevel.SEVERE_INT, event.getLevel().toInt());
    }

    /** A non-Level class named in the stream must not be loaded or initialized. */
    public void testNonLevelClassIsNotInitialized() throws Exception {
        assertFalse(Log4jLevelLoadFlag.nonLevelInitialized);
        byte[] bytes = serializeEventWithLevelClassName(NonLevelClassFixture.class.getName());
        HardenedLoggingEventInputStream in = new HardenedLoggingEventInputStream(new ByteArrayInputStream(bytes));
        LoggingEvent event = (LoggingEvent) in.readObject();
        in.close();
        assertFalse("non-Level class must not be initialized during deserialization",
                Log4jLevelLoadFlag.nonLevelInitialized);
        assertNotNull(event.getLevel());
    }

    /**
     * Serialize a LoggingEvent whose level is UtilLoggingLevel.SEVERE, substituting the
     * written level-class name with {@code levelClassName}. This mimics the bytes a remote
     * peer would send to a SocketNode/SocketServer.
     */
    private static byte[] serializeEventWithLevelClassName(final String levelClassName) throws IOException {
        Logger root = Logger.getRootLogger();
        LoggingEvent event = new LoggingEvent(root.getClass().getName(), root, UtilLoggingLevel.SEVERE, "hello", null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos) {
            {
                enableReplaceObject(true);
            }

            protected Object replaceObject(Object obj) throws IOException {
                if (UtilLoggingLevel.class.getName().equals(obj)) {
                    return levelClassName;
                }
                return super.replaceObject(obj);
            }
        };
        oos.writeObject(event);
        oos.flush();
        oos.close();
        return baos.toByteArray();
    }
}

/** Observation point that stays loadable without initializing the fixture below. */
class Log4jLevelLoadFlag {
    static volatile boolean nonLevelInitialized = false;
}

/** A plain class that is not a Level; records if its static initializer ever runs. */
class NonLevelClassFixture {
    static {
        Log4jLevelLoadFlag.nonLevelInitialized = true;
    }

    public static Object toLevel(int p) {
        return null;
    }
}
