package com.vg.airlinelistapplication.airlines.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vg.airlinelistapplication.airlines.dataconverter.AirlineListDataConverter;
import com.vg.airlinelistapplication.airlines.model.AirlineClass;
import com.vg.airlinelistapplication.constants.Constants;
import com.vg.airlinelistapplication.storage.AirlineTravelPreferenceHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *     This service class will be used to fetch the list of airlines from the JSON
 *     endpoint. This is the base implementation for a response based service to
 *     return result of the airlines.
 * </p>
 * helper methods.
 */
public class FetchAirlineListIntentService extends IntentService {

    private static final String TAG = "FetchedAirlineList";

    private static Context mContext;

    /**
     * Starts this service to perform action to fetch airlines with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchAirlines(Context context, String url) {
        mContext = context;
        Intent intent = new Intent(context, FetchAirlineListIntentService.class);
        intent.setAction(Constants.ACTION_FETCH_AIRLINES);
        intent.putExtra(Constants.EXTRA_URL, url);
        context.startService(intent);
    }

    public FetchAirlineListIntentService() {
        super("FetchAirlineListIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleActionFetchAirlines(intent.getStringExtra(Constants.EXTRA_URL));
        }
    }

    /**
     * Handle action fetching the airlines list in the provided background thread with the provided
     * parameters.
     * @param url - the URL to perform the action
     */
    private void handleActionFetchAirlines(String url) {
        try {

            // Create a HTTP client for performing HTTP download.
            HttpClient httpclient = new DefaultHttpClient();
            // Create a HTTP Post
            HttpPost httppost = new HttpPost(new URI(url));
            // Execute the http post.
            HttpResponse httpresponse = httpclient.execute(httppost);
            // Sleep for 500 milliseconds.
            Thread.sleep(500);
            HttpEntity entity = httpresponse.getEntity();
            InputStream inputStream = entity.getContent();
            int totalLength = (int) entity.getContentLength();
            if (inputStream != null) {
                Log.e(TAG, "Data received from " + url);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //variable to store total downloaded bytes
                int downloadedSize = 0;
                //create a buffer...
                byte[] buffer = new byte[1024];
                //create a buffer value...
                int bufferLength = 0;
                //used to store a temporary size of the buffer
                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0  ) {
                    baos.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    Log.i(TAG, "downloaded size.. " + downloadedSize);
                }
                String jsonResponse = new String(baos.toByteArray());
                Log.i(TAG, jsonResponse);

                ArrayList<AirlineClass> airlineClassArrayList = AirlineListDataConverter.retrieveAirlineArrayList(jsonResponse);
                AirlineTravelPreferenceHelper.setAirlinePreferenceList(mContext, jsonResponse);
                returnAirlineResult(mContext, true, airlineClassArrayList);
                baos.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Return the airline results from the service
     */
    private void returnAirlineResult(Context context, boolean status, ArrayList<AirlineClass> airlineClassArrayList) {
        Intent intentBroadcast = new Intent(Constants.RESPONSE_AIRLINE_RESULT);
        intentBroadcast.putExtra(Constants.RESPONSE_STATUS, status);
        intentBroadcast.putExtra(Constants.RESPONSE_DATA, airlineClassArrayList);
        intentBroadcast.putExtra(Constants.REQUEST_FROM, Constants.RESPONSE_FROM_FETCH_AIRLINE_INTENT_SERVICE);
        context.sendBroadcast(intentBroadcast);
    }

}
