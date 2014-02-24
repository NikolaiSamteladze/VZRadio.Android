package com.samteladze.vzradio.android.common;

import com.samteladze.vzradio.android.BuildConfig;

/**
 * Created by nsamteladze on 2/17/14.
 */
public class EnvironmentHelper {
    public static boolean isDebugEnvironment() {
        return BuildConfig.DEBUG;
    }

    public static boolean isProductionEnvironment() {
        return !BuildConfig.DEBUG;
    }

    public static boolean allowsDebugLogging() {
        return (sInDebugMode || isDebugEnvironment());
    }

    public static boolean requiresFileLogging() {
        return (sFileLoggingRequired || isProductionEnvironment());
    }

    // This should come from shared preferences
    public static boolean sInDebugMode = false;
    public static boolean sFileLoggingRequired = false;
}
