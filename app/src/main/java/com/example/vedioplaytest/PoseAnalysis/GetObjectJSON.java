package com.example.vedioplaytest.PoseAnalysis;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetObjectJSON {
    private JSONObject data;

    public GetObjectJSON(String json) {
        try {
            JSONObject temp = new JSONObject(json);
            JSONArray tempJson = temp.getJSONArray("predictions");
            data = (JSONObject) tempJson.get(0);

        } catch (JSONException error) {
            Log.e("analysis error" , error.getMessage());
        }
    }

    public double exportData(String field, String key) {
        try {
            return Math.round( ( (JSONObject) data.get(field) ).getDouble(key) * 100 ) / 100.0;

        } catch (JSONException error) {
            Log.e("export error" , error.getMessage());
            return -1D;
        }
    }
}
