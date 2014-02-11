package com.samteladze.vzradio.android;

import android.util.Log;

/**
 * Created by user441 on 2/2/14.
 */
public class ConsoleLog implements ILog {

    private static final String DefaultSource = "unknown";

    private final String source;

    public ConsoleLog() {
        this(DefaultSource);
    }

    public ConsoleLog(String source) {
        this.source = source;
    }

    @Override
    public void error(String message) {
        Log.e(source, message);
    }

    @Override
    public void error(Throwable exception, String message) {
        Log.e(source, message, exception);
    }

    @Override
    public void warning(String message) {
        Log.w(source, message);
    }

    @Override
    public void info(String message) {
        Log.i(source, message);
    }

    @Override
    public void debug(String message) {
        Log.d(source, message);
    }

}
