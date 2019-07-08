package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.render.window.WindowInfo;

public class InitializationOptions {
    private String applicationName = "default";
    private long applicationId = -1;
    private boolean headless = false;
    private GGInfo.UserDataOption localDataLocation = GGInfo.UserDataOption.DOCUMENTS;

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

    public GGInfo.UserDataOption getLocalDataLocation() {
        return localDataLocation;
    }

    public InitializationOptions setLocalDataLocation(GGInfo.UserDataOption localDataLocation) {
        this.localDataLocation = localDataLocation;
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
