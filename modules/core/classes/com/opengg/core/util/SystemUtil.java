package com.opengg.core.util;

public class SystemUtil {
    static {
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        boolean linux = System.getProperty("os.name").contains("nux");

        IS_WINDOWS = windows;
        IS_LINUX = linux;

        if (windows) {
            WINDOW_SESSION_HOST = WindowSessionType.WINDOWS;
        } else {
            if (System.getenv("XDG_SESSION_TYPE").equals("wayland")) {
                WINDOW_SESSION_HOST = WindowSessionType.WAYLAND;
            } else {
                WINDOW_SESSION_HOST = WindowSessionType.X11;
            }
        }
    }

    public static boolean IS_WINDOWS;
    public static boolean IS_LINUX;

    /**
     * Contains the windowing system host type. Mostly relevant for distinction between X11 and Wayland.
     */
    public static WindowSessionType WINDOW_SESSION_HOST;

    public enum WindowSessionType {
        WINDOWS,
        X11,
        WAYLAND;
    }
}
