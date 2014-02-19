package com.samteladze.vzradio.android.common;

import com.samteladze.vzradio.android.BuildConfig;

/**
 * Created by nsamteladze on 2/17/14.
 */
public class EnvironmentHelper {
    public static boolean isDebuggable() {
        return BuildConfig.DEBUG || sDebugMode;
    }

    public static boolean sDebugMode = false;
}
