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
package org.apache.log4j;

import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.InterruptUtil;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>An asynchronous appender closely reproducing the behaviour of logback's <code>AsyncAppender</code>.
 * </p>
 *
 * <p>See <a href="https://logback.qos.ch/manual/appenders.html#AsyncAppender">Logback's AsyncAppender documentation</a> for configuration options.
 * </p>
 *
 * Here is a sample configuration file for <code>NewAsyncAppender</code>.
 *
 * <pre>
 * &lt;log4j:configuration debug="true"
 *                      xmlns:log4j='http://jakarta.apache.org/log4j/'&gt;
 *
 *    &lt;appender name="FILE" class="org.apache.log4j.FileAppender"&gt;
 *      &lt;param name="File"   value="/tmp/foobar.log" /&gt;
 *      &lt;param name="Append" value="false" /&gt;
 *
 *      &lt;layout class="org.apache.log4j.PatternLayout"&gt;
 *        &lt;param name="ConversionPattern" value="%-5p %c{2} - %m%n"/&gt;
 *      &lt;/layout&gt;
 *   &lt;/appender&gt;
 *
 *     &lt;appender name="ASYNC" class="org.apache.log4j.NewAsyncAppender"&gt;
 *         &lt;param name="QueueSize" value="512"/&gt;
 *         &lt;appender-ref ref="FILE"/&gt;
 *     &lt;/appender&gt;
 *
 *     &lt;root&gt;
 *         &lt;level value="info"/&gt;
 *         &lt;appender-ref ref="ASYNC"/&gt;
 *     &lt;/root&gt;
 *
 * &lt;/log4j:configuration&gt;
 *
 *
 * </pre>
 *
 * <p>Note: <code>NewAyncAppedner</code> requires DOMConfigurator, i.e. XML configuration format.</p>
 * @since 1.2.26
 */
public class NewAsyncAppender extends AppenderSkeleton implements AppenderAttachable {
    /**
     * The default buffer size.
     */
    public static final int DEFAULT_QUEUE_SIZE = 256;
    int queueSize = DEFAULT_QUEUE_SIZE;
    /**
     * The default maximum queue flush time allowed during appender stop. If the
     * worker takes longer than this time it will exit, discarding any remaining
     * items in the queue
     */
    public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
    int maxFlushTime = DEFAULT_MAX_FLUSH_TIME;

    AppenderAttachableImpl appenders = new AppenderAttachableImpl();
    AtomicInteger appenderCount = new AtomicInteger(0);
    BlockingQueue<LoggingEvent> blockingQueue;
    Worker worker = new Worker();

    boolean neverBlock = false;
    static final int UNDEFINED = -1;
    int discardingThreshold = UNDEFINED;

    boolean includeCallerData = false;

    public NewAsyncAppender() {
    }

    @Override
    public void activateOptions() {
        if (queueSize < 1) {
            LogLog.error("Invalid queue size [" + queueSize + "]");
            return;
        }

        blockingQueue = new ArrayBlockingQueue<LoggingEvent>(queueSize);
        if (discardingThreshold == UNDEFINED)
            discardingThreshold = queueSize / 5;
        LogLog.debug("Setting discardingThreshold to " + discardingThreshold);
        worker.setDaemon(true);
        worker.setName("NewAsyncSingleAppender-Worker-" + getName());
        worker.start();

    }

    protected void append(LoggingEvent event) {
        if (isQueueBelowDiscardingThreshold() && isDiscardable(event)) {
            return;
        }
        preprocess(event);
        put(event);
    }
    public boolean isQueueBelowDiscardingThreshold() {
        return (blockingQueue.remainingCapacity() < discardingThreshold);
    }

    /**
     * Events of level TRACE, DEBUG and INFO are deemed to be discardable.
     *
     * @param event
     * @return true if the event is of level TRACE, DEBUG or INFO false otherwise.
     */
    protected boolean isDiscardable(LoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() <= Level.INFO_INT;
    }

    protected void preprocess(LoggingEvent eventObject) {
        // Set the NDC and thread name for the calling thread as these
        // LoggingEvent fields were not set at event creation time.
        eventObject.getNDC();
        eventObject.getThreadName();
        // Get a copy of this thread's MDC.
        eventObject.getMDCCopy();
        if (includeCallerData) {
            eventObject.getLocationInformation();
        }
        eventObject.getRenderedMessage();
        eventObject.getThrowableStrRep();
    }

    private void put(LoggingEvent eventObject) {
        if (neverBlock) {
            blockingQueue.offer(eventObject);
        } else {
            putUninterruptibly(eventObject);
        }
    }
    private void putUninterruptibly(LoggingEvent eventObject) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    blockingQueue.put(eventObject);
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
    public void close() {
        if (closed)
            return;

        closed = true;

        worker.interrupt();
        InterruptUtil interruptUtil = new InterruptUtil();

        try {
            interruptUtil.maskInterruptFlag();
            worker.join(maxFlushTime);
            if(worker.isAlive()) {
                LogLog.warn("Max queue flush timeout (" + maxFlushTime + " ms) exceeded. Approximately "
                                + blockingQueue.size() + " queued events were possibly discarded.");
            } else {
                LogLog.debug("Queue flush finished successfully within timeout.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            interruptUtil.maskInterruptFlag();
        }
    }

    boolean isClosed() {
        return closed;
    }

    public boolean requiresLayout() {
        return false;
    }

    public void addAppender(Appender newAppender) {
        if (appenderCount.compareAndSet(0, 1)) {
            LogLog.debug("Attaching appender named [" + newAppender.getName() + "] to NewAsyncAppender.");
            appenders.addAppender(newAppender);
        } else {
            LogLog.warn("One and only one appender may be attached to NewAsyncAppender.");
            LogLog.warn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }

    public Enumeration getAllAppenders() {
        return appenders.getAllAppenders();
    }

    public Appender getAppender(String name) {
        return appenders.getAppender(name);
    }

    public boolean isAttached(Appender appender) {
        return appenders.isAttached(appender);
    }

    public void removeAllAppenders() {
        appenders.removeAllAppenders();
    }

    public void removeAppender(Appender appender) {
        appenders.removeAppender(appender);
    }

    public void removeAppender(String name) {
        appenders.removeAppender(name);
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public int getMaxFlushTime() {
        return maxFlushTime;
    }

    public void setMaxFlushTime(int maxFlushTime) {
        this.maxFlushTime = maxFlushTime;
    }

    public void setNeverBlock(boolean neverBlock) {
        this.neverBlock = neverBlock;
    }

    public boolean isNeverBlock() {
        return neverBlock;
    }

    public boolean isIncludeCallerData() {
        return includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    class Worker extends Thread {

        public void run() {
            NewAsyncAppender parent = NewAsyncAppender.this;
            AppenderAttachableImpl appenders = parent.appenders;

            // loop while the parent is started
            while (!parent.closed) {
                try {
                    List<LoggingEvent> elements = new ArrayList<LoggingEvent>();
                    LoggingEvent e0 = parent.blockingQueue.take();
                    elements.add(e0);
                    parent.blockingQueue.drainTo(elements);
                    for (LoggingEvent e : elements) {
                        appenders.appendLoopOnAppenders(e);
                    }
                } catch (InterruptedException e1) {
                    // exit if interrupted
                    break;
                }
            }

            LogLog.debug("Worker thread will flush remaining events before exiting. ");

            for (LoggingEvent e : parent.blockingQueue) {
                appenders.appendLoopOnAppenders(e);
                parent.blockingQueue.remove(e);
            }

            // this closes and removes appenders
            appenders.removeAllAppenders();
        }
    }
}
