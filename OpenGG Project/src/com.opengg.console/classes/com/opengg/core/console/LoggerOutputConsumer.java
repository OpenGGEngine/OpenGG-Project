package com.opengg.core.console;

public interface LoggerOutputConsumer {
    void onMessage(GGMessage message);

    default Level getMinimumLevel(){
        return Level.INFO;
    }
}
