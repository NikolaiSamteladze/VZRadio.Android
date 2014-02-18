package com.samteladze.vzradio.android.common;

import com.samteladze.vzradio.android.BuildConfig;

/**
 * Created by nsamteladze on 2/17/14.
 */
public class LogManager {
    public static ILog getLog(String source) {
        if (EnvironmentHelper.isDebuggable()) {
            return new ConsoleLog(source);
        } else {
            return new FileLog(source);
        }
    }
}
