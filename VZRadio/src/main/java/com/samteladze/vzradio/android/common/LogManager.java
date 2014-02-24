package com.samteladze.vzradio.android.common;

import com.samteladze.vzradio.android.BuildConfig;

/**
 * Created by nsamteladze on 2/17/14.
 */
public class LogManager {
    public static ILog getLog(String source) {
        if (EnvironmentHelper.requiresFileLogging()) {
            return new FileLog(source);
        } else {
            return new ConsoleLog(source);
        }
    }
}
