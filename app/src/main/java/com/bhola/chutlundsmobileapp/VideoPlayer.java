package com.bhola.chutlundsmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VideoPlayer extends AppCompatActivity {
    String vidoetitle, href;
    private String TAG="jsonObject";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vidoetitle = getIntent().getStringExtra("title");
        href = getIntent().getStringExtra("href");

//        getVideoData_API(href);

      Button fetchBtn=findViewById(R.id.fetchBtn);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        getVideoData_API();

            }
        });

    }

    private void getVideoData_API() {
        RequestQueue requestQueue=Volley.newRequestQueue(VideoPlayer.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://www.chutlunds.live/api/spangbang/videoPlayer", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "jsonObject:   "+jsonObject);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Exception: "+e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("href",href);
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> params=new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void getVideoData_API(String url) {
        String API_URL = "https://www.chutlunds.live/api/spangbang/videoPlayer";
        try {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.d("jsonObject", "jsonObject: " + jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    },
                    error -> {
                        Log.d("jsonObject", "getVideoData_API: "+ error.toString());
                    }) {
                //Add parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //body data
                    Map<String, String> params = new HashMap<>();
                    params.put("href", url);
                    Log.d("jsonObject", "params: "+ params);

                    return params;
                }
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError{
                    Map<String,String> params=new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.d("jsonObject", "getVideoData_API: " + e.getMessage());
        }
    }


}