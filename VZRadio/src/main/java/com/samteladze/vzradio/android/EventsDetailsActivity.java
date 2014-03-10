package com.samteladze.vzradio.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;

import java.text.DateFormat;
import java.util.Date;

public class EventsDetailsActivity extends Activity {

    private final ILog mLog;
    private ImageLoader mImageLoader = null;

    public EventsDetailsActivity() {
        super();
        mLog = LogManager.getLog(EventsDetailsActivity.class);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent invocationIntent = getIntent();

        if (invocationIntent == null) {
            mLog.debug("Failed to get invocation intent for Event Details activity");
        }

        String title = invocationIntent.getStringExtra("title");
        String venue = invocationIntent.getStringExtra("venue");
        String startTime = invocationIntent.getStringExtra("startTime");
        String description = invocationIntent.getStringExtra("description");
        String imageUri = invocationIntent.getStringExtra("imageUri");

        mLog.info(title);

        LinearLayout wrapperView = (LinearLayout) findViewById(R.id.event_details_wrapper);

        if (imageUri != null) {
            ImageView imageView =
                    (ImageView) wrapperView.findViewById(R.id.event_details_image);
            if (mImageLoader != null) {
                mImageLoader.displayImage(imageUri, imageView);
            } else {
                mLog.error("ImageLoader is null");
            }
        }

        TextView titleView = (TextView) wrapperView.findViewById(R.id.event_details_title);
        titleView.setText(title);

        TextView venueView = (TextView) wrapperView.findViewById(R.id.event_details_venue);
        venueView.setText(venue);

        TextView eventStartTimeView = (TextView) wrapperView.findViewById(R.id.event_details_start_time);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        eventStartTimeView.setText(dateFormat.format(new Date(1000 * Long.parseLong(startTime))));

        WebView eventDescriptionWebView =
                (WebView) wrapperView.findViewById(R.id.event_details_description);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head>")
                .append("<style type=\"text/css\">body { color: #fff; }")
                .append("</style></head><body>")
                .append(description)
                .append("</body></html>");

        eventDescriptionWebView.loadData(stringBuilder.toString(), "text/html; charset=UTF-8", null);
        eventDescriptionWebView.setBackgroundColor(0x00000000);
        eventDescriptionWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        eventDescriptionWebView.getSettings()
                .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.events_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

}
