package com.example.statusia.Tools;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;

public class PathMap {
    Context context;

    public PathMap(Context context){
        this.context = context;
    }

    public void savePathMap(String key, String value){
        HashMap<String,String> map = getMap();
        map.put(key,value);
        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        SharedPreferences sharedPreferences = context.getSharedPreferences("PATH_MAP",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PATH_MAP_JSON",jsonString);
        editor.apply();
    }

    public HashMap<String,String> getMap() {
        HashMap<String,String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = context.getApplicationContext().getSharedPreferences("PATH_MAP", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("PATH_MAP_JSON", "NULL");
                if (!jsonString.equals("NULL")) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = jsonObject.getString(key);
                        outputMap.put(key, value);
                    }
                }
            }
        } catch (JSONException e){
            Log.e(TAG, "loadPathMap: "+e);
            e.printStackTrace();
        }
        return outputMap;
    }

}
