package com.vg.airlinelistapplication.airlines.dataconverter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.vg.airlinelistapplication.airlines.model.AirlineClass;
import com.vg.airlinelistapplication.storage.AirlineTravelPreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Airline List Data converter for converting the JSON value.
 * Created by vgoswami on 9/29/15.
 */
public class AirlineListDataConverter {

    /**
     * Retrieve Airline Array List
     * @param jsonData - the JSON data which has to be parsed.
     * @return ArrayList of Airline
     */
    public static ArrayList<AirlineClass> retrieveAirlineArrayList(String jsonData) {
        ArrayList<AirlineClass> airlineClassArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                AirlineClass airlineClass = new Gson().fromJson(jsonObject.toString(), AirlineClass.class);
                airlineClassArrayList.add(airlineClass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return airlineClassArrayList;
    }

    /**
     * Convert to JSON Array From String.
     * @param airlineClassArrayList - the list.
     * @return string in the format of JSONArray
     */
    public static String convertToJSONArrayFromString(List<AirlineClass> airlineClassArrayList) {
        String jsonString = "";
        try {
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(airlineClassArrayList, new TypeToken<List<AirlineClass>>(){}.getType());
            if (!element.isJsonArray()) {
                Log.e("JSONFailure", "Failed to convert to JSON Array");
            }
            return element.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonString;
    }

    /**
     * Get Favorite List from Preferences.
     * @param context - the calling application context.
     * @return List of Favorite airlines from preferences.
     */
    public static ArrayList<AirlineClass> getFavoriteListFromPreferences(Context context) {
        ArrayList<AirlineClass> arrayList = new ArrayList<>();
        String[] favAirlineCodes = TextUtils.split(AirlineTravelPreferenceHelper.getAirlineFavItems(context), Pattern.quote("|"));
        List<String> stringList = Arrays.asList(favAirlineCodes);
        ArrayList<AirlineClass> retrievedArrayList = retrieveAirlineArrayList(AirlineTravelPreferenceHelper.getAirlinePreferenceList(context));
        for (AirlineClass airlineClass : retrievedArrayList) {
            if (stringList.contains(airlineClass.getCode())) {
                arrayList.add(airlineClass);
            }
        }
        return arrayList;
    }
}
