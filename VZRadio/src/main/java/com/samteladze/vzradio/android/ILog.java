package com.samteladze.vzradio.android;

/**
 * Created by user441 on 2/2/14.
 */
public interface ILog {
    void error(String message);

    void error(Throwable exception, String message);

    void warning(String message);

    void info(String message);

    void debug(String message);
}
