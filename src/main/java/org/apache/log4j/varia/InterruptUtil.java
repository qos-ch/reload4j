package org.apache.log4j.varia;

import org.apache.log4j.helpers.LogLog;

public class InterruptUtil {
    final boolean previouslyInterrupted;

    public InterruptUtil() {
        super();
        previouslyInterrupted = Thread.currentThread().isInterrupted();
    }

    public void maskInterruptFlag() {
        if (previouslyInterrupted) {
            Thread.interrupted();
        }
    }

    public void unmaskInterruptFlag() {
        if (previouslyInterrupted) {
            try {
                Thread.currentThread().interrupt();
            } catch (SecurityException se) {
                LogLog.error("Failed to interrupt current thread", se);
            }
        }
    }
}
