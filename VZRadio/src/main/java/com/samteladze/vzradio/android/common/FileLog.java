package com.samteladze.vzradio.android.common;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FileLog implements ILog {

    private static final String DEFAULT_LOG_DIR = "VZRadio";
    private static final String DEFAULT_LOG_FILE = "log.txt";

    private static final String ERROR_TAG = "ERROR";
    private static final String WARNING_TAG = "WARNING";
    private static final String INFO_TAG = "INFO";
    private static final String DEBUG_TAG = "DEBUG";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_SOURCE = "Unknown";

    private static final String LOG_MESSAGE_FORMAT = "%s [%s] - %s | %s\n";
    private static final String LOG_EXCEPTION_FORMAT = "\nEXCEPTION:\n%s\nMESSAGE:\n%s\nSTACK TRACE:\n%s";

    private final ILog mConsoleLog;

    public String source;

    public FileLog() {
        this(DEFAULT_SOURCE);
    }

    public FileLog(String logSource) {
        mConsoleLog = new ConsoleLog(getClass().getSimpleName());
        source = logSource;
    }

    private void writeEntry(Throwable exception, String message, String tag) {


        if (!isExternalStorageWritable()) {
            mConsoleLog.error("Failed to write log to a file. " +
                    "External does not permit writing. Logged message: %s", message);
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory(),
               PathHelper.combine(DEFAULT_LOG_DIR, DEFAULT_LOG_FILE));

        if (!file.exists()) {
           if (!file.getParentFile().exists()) {
               if (!file.getParentFile().mkdirs()) {
                   mConsoleLog.error("Failed to create directory to save logs to. " +
                           "Directory: %s", file.getAbsoluteFile());
                   return;
               }
           }

           if (!file.getParentFile().isDirectory()) {
               mConsoleLog.error("Failed to create directory to save logs to. " +
                       "File exists and is not a directory. Path: %s",
                       file.getParentFile().getAbsolutePath());
               return;
           }

           try {
               file.createNewFile();
           }
           catch (IOException e) {
               mConsoleLog.error(e,
                       "Failed to create a new file to log to. Path: %s", file.getAbsoluteFile());
               return;
           }
        }

        if (!file.isFile()) {
            mConsoleLog.error("Failed to log to file. Path exists, but is a directory. Path: %s",
                    file.getAbsoluteFile());
            return;
        }

        // At this point file is ready to be written to

        FileWriter writer;
        try {
            writer = new FileWriter(file, true);

            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String utcNow = dateFormat.format(new Date());

            /* Entry will accumulate all information that needs to be
               logged (message, exceptions, etc.)
             */
            String entry = message;

            // Construct a complex message entry if we have an exception (with causes) to log
            if (exception != null) {
                StringBuilder builder = new StringBuilder(message);

                builder.append(String.format(LOG_EXCEPTION_FORMAT,
                        exception.getClass().getSimpleName(), exception.getMessage(),
                        exception.getStackTrace()));

                Throwable cause = exception.getCause();
                while (cause != null) {
                    builder.append(String.format(LOG_EXCEPTION_FORMAT,
                            cause.getClass().getSimpleName(), cause.getMessage(),
                            cause.getStackTrace()));

                    cause = cause.getCause();
                }

                entry = builder.toString();
            }

            writer.write(String.format(LOG_MESSAGE_FORMAT, utcNow, tag, source, entry));
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            mConsoleLog.error(e);
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            mConsoleLog.error("External storage is not mounted. State: %s", state);
            return false;
        }
    }

    @Override
    public void error(Object message) {
        error(null, message.toString());
    }

    @Override
    public void error(String format, Object... args) {
        error(null, format, args);
    }

    @Override
    public void error(Throwable exception) {
        error(exception, "");
    }

    @Override
    public void error(Throwable exception, Object message) {
        writeEntry(exception, message.toString(), ERROR_TAG);
    }

    @Override
    public void error(Throwable exception, String format, Object... args) {
        error(exception, String.format(format, args));
    }

    @Override
    public void warning(Object message) {
        writeEntry(null, message.toString(), WARNING_TAG);
    }

    @Override
    public void warning(String format, Object... args) {
        warning(String.format(format, args));
    }

    @Override
    public void info(Object message) {
        writeEntry(null, message.toString(), INFO_TAG);
    }

    @Override
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    @Override
    public void debug(Object message) {
        if (EnvironmentHelper.isDebuggable()) {
            writeEntry(null, message.toString(), DEBUG_TAG);
        }
    }

    @Override
    public void debug(String format, Object... args) {
        debug(String.format(format, args));
    }
}
