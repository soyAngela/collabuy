package com.example.collabuy;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonBuilder {

    public static JSONArray buildList(String data){
        try {
            return new JSONArray(data);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject buildObject(String data){
        try{
            return new JSONObject(data);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
