package com.bhola.chutlundsmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView dataText;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button fetchBtn = findViewById(R.id.fetchBtn);
        dataText = findViewById(R.id.dataText);
        image = findViewById(R.id.image);

        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAPI_DATA();

            }
        });
    }

    private void getAPI_DATA() {
        try {
            String API_URL = "https://www.chutlunds.live/api/spangbang/getvideos";
            String url = "https://spankbang.com/s/boobs";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("url", url);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                    response -> {
                        Toast.makeText(this, "DATA Fetched", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray finalDataArray = jsonObject.getJSONArray(("finalDataArray"));
                            dataText.setText((CharSequence) finalDataArray.getJSONObject(4).toString());
                            String imageURL =finalDataArray.getJSONObject(4).getString("thumbnailArray");
                            Picasso.with(MainActivity.this).load(imageURL).into(image);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    },
                    error -> Toast.makeText(this, "", Toast.LENGTH_SHORT).show()) {
                //Add parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //body data
                    Map<String, String> params = new HashMap<>();
                    params.put("url", "https://spankbang.com/s/desi/?o=trending");
                    return params;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
