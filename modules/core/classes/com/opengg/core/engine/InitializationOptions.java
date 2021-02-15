package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.render.window.WindowOptions;

public class InitializationOptions {
    private String applicationName = "default";
    private String userDataDirectory = "";
    private long applicationId = -1;
    private boolean headless = false;
    private boolean configUserData = false;
    private GGInfo.UserDataOption localDataLocation = GGInfo.UserDataOption.DOCUMENTS;

    private WindowOptions windowOptions = new WindowOptions();

    public String getApplicationName() {
        return applicationName;
    }

    public InitializationOptions setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public String getUserDataDirectory() {
        return userDataDirectory;
    }

    public InitializationOptions setUserDataDirectory(String userDataDirectory) {
        this.userDataDirectory = userDataDirectory;
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

    public boolean configInUserData() {
        return configUserData;
    }

    public InitializationOptions setConfigInUserData(boolean configInUserData) {
        this.configUserData = configInUserData;
        return this;
    }

    public GGInfo.UserDataOption getLocalDataLocation() {
        return localDataLocation;
    }

    public InitializationOptions setLocalDataLocation(GGInfo.UserDataOption localDataLocation) {
        this.localDataLocation = localDataLocation;
        return this;
    }

    public WindowOptions getWindowOptions() {
        return windowOptions;
    }

    public InitializationOptions setWindowOptions(WindowOptions windowOptions) {
        this.windowOptions = windowOptions;
        return this;
    }
}
