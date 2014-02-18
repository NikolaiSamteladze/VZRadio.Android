package com.samteladze.vzradio.android.common;

/**
 * Created by user441 on 2/2/14.
 */
public interface ILog {
    void error(Object message);

    void error(Throwable exception);
    void error(Throwable exception, Object message);

    void error(String format, Object... args);
    void error(Throwable exception, String format, Object... args);

    void warning(Object message);
    void warning(String format, Object... args);
    void info(Object message);
    void info(String format, Object... args);
    void debug(Object message);
    void debug(String format, Object... args);
}
