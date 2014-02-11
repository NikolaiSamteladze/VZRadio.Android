package com.samteladze.vzradio.android;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStream;

public class VzRadioDataAdapter {

    private static final String VzRadioCurrentlyPlayedSongApi = "http://rock63.ru/export/vzplay.json";
    private static final String EVENTS_API_URI = "http://rock63.ru/export/api.php?type=news";

    private final ILog log;

    public VzRadioDataAdapter() {
        this.log = new ConsoleLog(this.getClass().getCanonicalName());
    }

    public VzRadioDataAdapter(ILog log) {
        this.log = log;
    }

    public String getCurrentSongAsJson() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(VzRadioCurrentlyPlayedSongApi);

        InputStream content;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            content = httpEntity.getContent();
        } catch (IOException e) {
            log.error(e, "Failed to get currently played song from VzRadio API");
            return null;
        }

        try {
            String currentSongAsJson = ConverterUtility.InputStreamToString(content);
            return currentSongAsJson;
        } catch (IOException e) {
            log.error(e, "Failed to parse the received content into string");
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
            log.error(e, "Failed to get events from VzRadio API");
            return null;
        }

        try {
            String eventsAsJson = ConverterUtility.InputStreamToString(content);
            return eventsAsJson;
        } catch (IOException e) {
            log.error(e, "Failed to parse the received content into string");
            return null;
        }
    }
}
