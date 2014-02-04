package com.samteladze.vzradio.android;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStream;

public class VzRadioDataProvider {

    private static final String VzRadioCurrentlyPlayedSongApi = "http://rock63.ru/export/vzplay.json";

    private final ILog log;

    public VzRadioDataProvider() {
        this.log = new ConsoleLog(this.getClass().getCanonicalName());
    }

    public VzRadioDataProvider(ILog log) {
        this.log = log;
    }

    public String GetCurrentSongAsJson() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(VzRadioCurrentlyPlayedSongApi);

        InputStream content;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            content = httpEntity.getContent();
        } catch (IOException e) {
            log.Error("Failed to get currently played song from VzRadio API", e);
            return null;
        }

        try {
            String currentSongAsJson = ConverterUtility.InputStreamToString(content);
            return currentSongAsJson;
        } catch (IOException e) {
            log.Error("Failed to parse the received content into string", e);
            return null;
        }
    }
}
