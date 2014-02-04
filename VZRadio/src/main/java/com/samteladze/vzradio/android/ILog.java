package com.samteladze.vzradio.android;

/**
 * Created by user441 on 2/2/14.
 */
public interface ILog {
    void Error(String message);

    void Error(String message, Throwable exception);

    void Warning(String message);

    void Info(String message);

    void Debug(String message);
}
