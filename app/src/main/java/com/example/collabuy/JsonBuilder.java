package com.example.collabuy;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonBuilder {

    public static JSONArray buildProductList(String data){
        try {
            return new JSONArray(data);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
