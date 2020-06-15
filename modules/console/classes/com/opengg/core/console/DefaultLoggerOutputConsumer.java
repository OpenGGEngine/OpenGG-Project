package com.opengg.core.console;

import java.util.function.Consumer;

public class DefaultLoggerOutputConsumer implements LoggerOutputConsumer {
    private final Level level;
    private final Consumer<GGMessage> consumer;

    public DefaultLoggerOutputConsumer(Level level, Consumer<GGMessage> consumer){
        this.level = level;
        this.consumer = consumer;
    }

    @Override
    public void onMessage(GGMessage message) {
        consumer.accept(message);
    }

    @Override
    public Level getMinimumLevel() {
        return level;
    }
}
