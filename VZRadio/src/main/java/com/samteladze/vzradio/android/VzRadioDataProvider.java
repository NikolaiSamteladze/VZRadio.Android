package com.samteladze.vzradio.android;

import com.samteladze.vzradio.android.common.ConverterUtility;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStream;

public class VzRadioDataProvider {

    private static final String VZRADIO_CURRENTLY_PLAYED_SONG_API = "http://rock63.ru/export/vzplay.json";
    private static final String EVENTS_API_URI = "http://rock63.ru/export/api.php?type=afisha";

    private final ILog mLog;

    public VzRadioDataProvider() {
        mLog = LogManager.getLog(this.getClass().getSimpleName());
    }

    public VzRadioDataProvider(ILog log) {
        mLog = log;
    }

    public String getCurrentSongAsJson() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(VZRADIO_CURRENTLY_PLAYED_SONG_API);

        InputStream content;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            content = httpEntity.getContent();
        } catch (IOException e) {
            mLog.error(e, "Failed to get currently played song from VzRadio API");
            return null;
        }

        try {
            String currentSongAsJson = ConverterUtility.inputStreamToString(content);
            return currentSongAsJson;
        } catch (IOException e) {
            mLog.error(e, "Failed to parse the received content into string");
            return null;
        }
    }

    public String getEventsAsJson() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(EVENTS_API_URI);

        InputStream content;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            content = httpEntity.getContent();
        } catch (IOException e) {
            mLog.error(e, "Failed to get events from VzRadio API");
            return null;
        }

        try {
            String eventsAsJson = ConverterUtility.inputStreamToString(content);
            return eventsAsJson;
        } catch (IOException e) {
            mLog.error(e, "Failed to parse the received content into string");
            return null;
        }
    }
}
