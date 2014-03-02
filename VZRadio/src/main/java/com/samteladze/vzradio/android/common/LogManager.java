package com.samteladze.vzradio.android.common;

import com.samteladze.vzradio.android.BuildConfig;

import java.lang.reflect.Type;

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

    public static ILog getLog(Class currentClass) {
        return getLog(currentClass.getSimpleName());
    }
}
