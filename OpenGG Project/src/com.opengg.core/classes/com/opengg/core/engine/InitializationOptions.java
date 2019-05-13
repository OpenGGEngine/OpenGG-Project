package com.opengg.core.engine;

import com.opengg.core.render.window.WindowInfo;

public class InitializationOptions {
    private String applicationName = "default";
    private long applicationId = -1;
    private boolean headless = false;

    private WindowInfo windowInfo = new WindowInfo();

    public String getApplicationName() {
        return applicationName;
    }

    public InitializationOptions setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public InitializationOptions setApplicationId(long applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public boolean isHeadless() {
        return headless;
    }

    public InitializationOptions setHeadless(boolean headless) {
        this.headless = headless;
        return this;
    }

    public WindowInfo getWindowInfo() {
        return windowInfo;
    }

    public InitializationOptions setWindowInfo(WindowInfo windowInfo) {
        this.windowInfo = windowInfo;
        return this;
    }
}
