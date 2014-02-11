package com.samteladze.vzradio.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by user441 on 2/2/14.
 */
public class ConverterUtility {

    public static String InputStreamToString(InputStream inputStream)
            throws IOException {

        if (inputStream == null) throw new IllegalArgumentException("inputStream can't be null");

        BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
        String nextLine;
        StringBuilder stringBuilder = new StringBuilder();

        while ((nextLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(nextLine);
        }

        return stringBuilder.toString();
    }
}
