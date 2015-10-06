package com.vg.airlinelistapplication.airlines;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.vg.airlinelistapplication.airlines.model.AirlineClass;
import com.vg.airlinelistapplication.airlines.service.FetchAirlineListIntentService;
import com.vg.airlinelistapplication.constants.Constants;

import java.util.ArrayList;

/**
 * This class will be a singleton class for implementing the request for fetching the
 * airlines from a given URL.
 * Created by vgoswami on 9/30/15.
 */
public class AirlinesListFetcher {

    private static final String TAG = "AirlineListFetcher";


    /**
     * Static Reference for singleton
     */
    private static AirlinesListFetcher mAirlinesListFetcher;
    /**
     * Context which will be used for Volley
     */
    private Context mContext;
    /**
     * Request Queue
     */
    private RequestQueue mRequestQueue;
    /**
     * Image Loader
     */
    private ImageLoader mImageLoader;

    /**
     * Private Constructor
     */
    private AirlinesListFetcher(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(100);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Singleton instance of the class.
     * @param context - the calling application context.
     * @return Singleton instance.
     */
    public static synchronized AirlinesListFetcher getInstance(Context context) {
        if (mAirlinesListFetcher == null) mAirlinesListFetcher = new AirlinesListFetcher(context);
        return mAirlinesListFetcher;
    }

    /**
     * Returns the request queue created from a volley framework.
     * @return {@link RequestQueue}
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add the request to the Queue
     * @param req - the {@link Request} to add to the queue
     * @param <T> - the request type
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Get the image loader for loading the image.
     * @return {@link ImageLoader}
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Fet the airline list from the URL.
     * @param url - the URL of the JSON.
     */
    public void fetchAirlineList(String url) {
        FetchAirlineListIntentService.startActionFetchAirlines(mContext, url);
    }

    /**
     * Return the airline results from the service
     */
    private void returnAirlineResult(Context context, boolean status, ArrayList<AirlineClass> airlineClassArrayList) {
        Intent intentBroadcast = new Intent(Constants.RESPONSE_AIRLINE_RESULT);
        intentBroadcast.putExtra(Constants.RESPONSE_STATUS, status);
        intentBroadcast.putExtra(Constants.RESPONSE_DATA, airlineClassArrayList);
        intentBroadcast.putExtra(Constants.REQUEST_FROM, Constants.RESPONSE_FROM_FETCH_AIRLINE_LOGO);
        context.sendBroadcast(intentBroadcast);
    }
}
