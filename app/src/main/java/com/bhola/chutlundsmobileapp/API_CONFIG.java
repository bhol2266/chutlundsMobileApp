package com.bhola.chutlundsmobileapp;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class API_CONFIG {

    private static String TAG = "jsonArray";
    public static void HomepageVideoAPI(String API_URL, Context context) {
        SplashScreen.Trending_collectonData = new ArrayList<>();
        SplashScreen.Upcoming_collectonData = new ArrayList<>();
        SplashScreen.Popular_collectonData = new ArrayList<>();
        SplashScreen.New_collectonData = new ArrayList<>();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("finalDataArray");
                    JSONArray jsonArray_Trending = jsonArray.getJSONArray(0);
                    JSONArray jsonArray2_Upcoming = jsonArray.getJSONArray(1);
                    JSONArray jsonArray3_Popular = jsonArray.getJSONArray(2);
                    JSONArray jsonArray4_New = jsonArray.getJSONArray(3);

                    for (int i = 0; i < jsonArray_Trending.length(); i++) {
                        JSONObject obj = jsonArray_Trending.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        SplashScreen.Trending_collectonData.add(videoModel);
                    }

                    for (int i = 0; i < jsonArray2_Upcoming.length(); i++) {
                        JSONObject obj = jsonArray2_Upcoming.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        SplashScreen.Upcoming_collectonData.add(videoModel);
                    }

                    for (int i = 0; i < jsonArray3_Popular.length(); i++) {
                        JSONObject obj = jsonArray3_Popular.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        SplashScreen.Popular_collectonData.add(videoModel);
                    }

                    for (int i = 0; i < jsonArray4_New.length(); i++) {
                        JSONObject obj = jsonArray4_New.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        SplashScreen.New_collectonData.add(videoModel);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSONException: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }


}

//    private void getAPI_DATA() {
//        try {
//            String API_URL = "https://www.chutlunds.com/api/spangbang/getvideos";
//            String url = "https://spankbang.com/s/boobs";
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("url", url);
//            final String requestBody = jsonBody.toString();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
//                    response -> {
//                        Toast.makeText(this, "DATA Fetched", Toast.LENGTH_SHORT).show();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            JSONArray finalDataArray = jsonObject.getJSONArray(("finalDataArray"));
//                            dataText.setText((CharSequence) finalDataArray.getJSONObject(4).toString());
//                            String imageURL =finalDataArray.getJSONObject(4).getString("thumbnailArray");
//                            Picasso.with(MainActivity.this).load(imageURL).into(image);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    },
//                    error -> Toast.makeText(this, "", Toast.LENGTH_SHORT).show()) {
//                //Add parameters
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    //body data
//                    Map<String, String> params = new HashMap<>();
//                    params.put("url", "https://spankbang.com/s/desi/?o=trending");
//                    return params;
//                }
//            };
//
//
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(stringRequest);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//



