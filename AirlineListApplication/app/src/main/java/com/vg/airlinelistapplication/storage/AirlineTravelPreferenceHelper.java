package com.vg.airlinelistapplication.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * This class contains the preferences for the application.
 * Created by vgoswami on 9/30/15.
 */
public class AirlineTravelPreferenceHelper {

    private static final String APP_FIRST_TIME_LAUNCH = "TravellerAppFirstTimeLaunch";
    private static final String AIRLINE_PREFERENCE_LIST = "AirlinePreferenceList";
    private static final String AIRLINE_FAV_LIST_ITEMS = "AirlineFavListItems";
    
    
    /**
     * Returns the shared preferences for the application
     * @param context - the calling application context.
     * @return Shared Preferences.
     */
    private static synchronized SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    /**
     * Set the App first time launched flag.
     * @param context - the calling application context.
     * @param appFirstTimeLaunch - True if the app is launched first time.
     */
    public static void setAppFirstTimeLaunch(Context context, boolean appFirstTimeLaunch) {
        getPreferences(context).edit().putBoolean(APP_FIRST_TIME_LAUNCH, appFirstTimeLaunch).apply();
    }

    /**
     * Get the status of the app launch.
     * @param context - the calling application context.
     * @return True if the app is launched first time, False otherwise
     */
    public static boolean isAppFirstTimeLaunched(Context context) {
        return getPreferences(context).getBoolean(APP_FIRST_TIME_LAUNCH, true);
    }

    /**
     * Set the Airline preference data from server to prevent loading it everytime.
     * @param context - the calling application context.
     * @param jsonDataFromServer - the JSON data from server
     */
    public static void setAirlinePreferenceList(Context context, String jsonDataFromServer) {
        getPreferences(context).edit().putString(AIRLINE_PREFERENCE_LIST, jsonDataFromServer).apply();
    }

    /**
     * Get the Airline Preference List JSON
     * @param context - the calling application context.
     * @return The JSON object
     */
    public static String getAirlinePreferenceList(Context context) {
        return getPreferences(context).getString(AIRLINE_PREFERENCE_LIST, "");
    }

    /**
     * Save the airline as favorite
     * @param context - the calling application context.
     * @param code - the code of the airline in the list.
     */
    public static void addAirlineFavItem(Context context, String code) {
        if (TextUtils.isEmpty(getAirlineFavItems(context))) {
            getPreferences(context).edit().putString(AIRLINE_FAV_LIST_ITEMS, code).apply();
        } else {
            String favItems = getAirlineFavItems(context) + "|" + code;
            getPreferences(context).edit().putString(AIRLINE_FAV_LIST_ITEMS, favItems).apply();
        }
    }

    /**
     * Remove the favorite airline from the saved list
     * @param context - the calling application context.
     * @param code - the code of the airline in the list.
     */
    public static void removeAirlineFavItem(Context context, String code) {
        if (!TextUtils.isEmpty(code)) {
            getPreferences(context).edit().putString(AIRLINE_FAV_LIST_ITEMS,
                    getPreferences(context).getString(AIRLINE_FAV_LIST_ITEMS, "").replace("|" + code, "")).apply();
        }
    }

    /**
     * Get the list of items which were stored as favorites.
     * @param context - the calling application context.
     * @return List of Items stored as favorites.
     */
    public static String getAirlineFavItems(Context context) {
        return getPreferences(context).getString(AIRLINE_FAV_LIST_ITEMS, "");
    }

}
