package org.apache.log4j;

/**
 * Logging request runnable.
 */
final class Greeter implements Runnable {
    /**
     * Logger.
     */
    private final Logger logger;

    /**
     * Repetitions.
     */
    private final int repetitions;

    /**
     * Create new instance.
     *
     * @param logger      logger, may not be null.
     * @param repetitions repetitions.
     */
    public Greeter(final Logger logger, final int repetitions) {
        if (logger == null) {
            throw new IllegalArgumentException("logger");
        }

        this.logger = logger;
        this.repetitions = repetitions;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            for (int i = 0; i < repetitions; i++) {
                logger.info("Hello, World");
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
