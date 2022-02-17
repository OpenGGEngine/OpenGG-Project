package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.render.window.WindowOptions;

public record InitializationOptions(String applicationName, String userDataDirectory, long applicationId,
                                    boolean initializeSound, boolean headless, boolean configUserData, boolean redirectStandardIO,
                                    GGInfo.UserDataOption localDataLocation, WindowOptions windowOptions) {
    public InitializationOptions() {
        this("default", "", -1,
                true, false, false, true,
                GGInfo.UserDataOption.DOCUMENTS, new WindowOptions());
    }

    public InitializationOptions setApplicationName(String applicationName) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setUserDataDirectory(String userDataDirectory) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setApplicationId(long applicationId) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setHeadless(boolean headless) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setInitializeSound(boolean initializeSound) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setRedirectStandardIO(boolean redirectStandardIO) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setConfigInUserData(boolean configUserData) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }
    public InitializationOptions setLocalDataLocation(GGInfo.UserDataOption localDataLocation) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }

    public InitializationOptions setWindowOptions(WindowOptions windowOptions) {
        return new InitializationOptions(applicationName, userDataDirectory, applicationId, initializeSound, headless, configUserData, redirectStandardIO, localDataLocation, windowOptions);
    }
}
