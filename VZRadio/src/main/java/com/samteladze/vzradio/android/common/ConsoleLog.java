package com.samteladze.vzradio.android.common;

import android.util.Log;

public class ConsoleLog implements ILog {
    private static final String DEFAULT_SOURCE = "Unknown";

    private final String source;

    public ConsoleLog() {
        this(DEFAULT_SOURCE);
    }

    public ConsoleLog(String source) {
        this.source = source;
    }

    @Override
    public void error(Object message) {
        error(null, message);
    }

    @Override
    public void error(Throwable exception) {
        error(exception, "");
    }

    @Override
    public void error(Throwable exception, Object message) {
        Log.e(source, message.toString(), exception);
    }

    @Override
    public void error(String format, Object... args) {
        error(null, format, args);
    }

    @Override
    public void error(Throwable exception, String format, Object... args) {
        error(exception, String.format(format, args));
    }

    @Override
    public void warning(Object message) {
        Log.w(source, message.toString());
    }

    @Override
    public void warning(String format, Object... args) {
        warning(String.format(format, args));
    }

    @Override
    public void info(Object message) {
        Log.i(source, message.toString());
    }

    @Override
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    @Override
    public void debug(Object message) {
        if (EnvironmentHelper.isDebuggable()) {
            Log.d(source, message.toString());
        }
    }

    @Override
    public void debug(String format, Object... args) {
        debug(String.format(format, args));
    }
}
