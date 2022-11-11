package com.bhola.chutlundsmobileapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soup.neumorphism.NeumorphButton;

public class VideosList extends AppCompatActivity {

    VideosAdapter adapter;
    private String TAG = "TAGA";
    private String SPANGBANG_URL_API;
    RecyclerView recyclerView;
    ArrayList<String> filteredObject;
    ProgressBar progressbar;
    String CurrentPageNum = "1";
    boolean noVideosFromAPI = false;
    Spinner spinnerFilter, spinnerQuality, spinnerDuration, spinnerDate;
    NestedScrollView nestedScrollView;
    LinearLayout notFoundMessageLayout;
    Button notFoundGoback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        actionBar();
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView = findViewById(R.id.recyclerView);
        progressbar = findViewById(R.id.progressbar);
        recyclerView.setLayoutManager(layoutManager);

        //Not found layout
        nestedScrollView = findViewById(R.id.nestedScrollView);
        notFoundMessageLayout = findViewById(R.id.notFoundMessageLayout);
        notFoundGoback = findViewById(R.id.notFoundGoback);

        SPANGBANG_URL_API = getIntent().getStringExtra("url");
        getVideoData_API(SPANGBANG_URL_API + "?o=all");
        Log.d(TAG, "onCreate: " + SPANGBANG_URL_API + "?o=all");
        filteredObject = new ArrayList<>();
        videoListFilters();
    }

    private void videoListFilters() {

        spinnerFilter = findViewById(R.id.spinnerFilter);
        String[] arrayFilter = getResources().getStringArray(R.array.filter);

        ArrayAdapter<String> adapterFilter = new ArrayAdapter<String>(this, R.layout.spinner, arrayFilter);
        adapterFilter.setDropDownViewResource(R.layout.spinneritem);
        spinnerFilter.setAdapter(adapterFilter);
        spinnerFilter.setSelection(0);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurrentPageNum = "1";

                HashMap filterMap = new HashMap();
                filterMap.put("All", "o=all");
                filterMap.put("Trending", "o=trending");
                filterMap.put("New", "o=new");
                filterMap.put("Popular", "o=top");
                filterMap.put("Featured", "o=featured");


                for (int i = 0; i < filteredObject.size(); i++) {
                    if (filteredObject.get(i).contains("o=")) {
                        filteredObject.remove(i);
                        if (position == 0) {
                            //this is because when the user switch from trending too all we need to update the videos
                            customiseSpangbangURL("1");
                        }
                    }
                }

                if (position != 0) {
                    filteredObject.add((String) filterMap.get(arrayFilter[position]));
                    Log.d(TAG, "filteredObject: " + filteredObject);
                    customiseSpangbangURL("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerQuality = findViewById(R.id.spinnerQuality);
        String[] arrayQuality = getResources().getStringArray(R.array.qualtiy);
        ArrayAdapter<String> adapterQuality = new ArrayAdapter<String>(this, R.layout.spinner, arrayQuality);
        adapterQuality.setDropDownViewResource(R.layout.spinneritem);
        spinnerQuality.setAdapter(adapterQuality);
        spinnerQuality.setSelection(0);


        spinnerQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurrentPageNum = "1";

                HashMap filterMap = new HashMap();
                filterMap.put("All", "q=all");
                filterMap.put("720p", "q=hd");
                filterMap.put("1080p", "q=fhd");
                filterMap.put("4K", "q=uhd");


                for (int i = 0; i < filteredObject.size(); i++) {
                    if (filteredObject.get(i).contains("q=")) {
                        filteredObject.remove(i);
                        if (position == 0) {
                            //this is because when the user switch from trending too all we need to update the videos
                            customiseSpangbangURL("1");
                        }
                    }
                }

                if (position != 0) {
                    filteredObject.add((String) filterMap.get(arrayQuality[position]));
                    Log.d(TAG, "SPANGBANG_URL_API: " + SPANGBANG_URL_API);
                    Log.d(TAG, "filteredObject: " + filteredObject);
                    customiseSpangbangURL("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerDuration = findViewById(R.id.spinnerDuration);
        String[] arrayDuration = getResources().getStringArray(R.array.duration);
        ArrayAdapter<String> adapterDuration = new ArrayAdapter<String>(this, R.layout.spinner, arrayDuration);
        adapterDuration.setDropDownViewResource(R.layout.spinneritem);
        spinnerDuration.setAdapter(adapterDuration);
        spinnerDuration.setSelection(0);

        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurrentPageNum = "1";

                HashMap filterMap = new HashMap();
                filterMap.put("All", "d=all");
                filterMap.put("10+min", "d=10");
                filterMap.put("20+min", "d=20");
                filterMap.put("40+min", "d=40");


                for (int i = 0; i < filteredObject.size(); i++) {
                    if (filteredObject.get(i).contains("d=")) {
                        filteredObject.remove(i);
                        if (position == 0) {
                            //this is because when the user switch from trending too all we need to update the videos
                            customiseSpangbangURL("1");
                        }
                    }
                }

                if (position != 0) {
                    filteredObject.add((String) filterMap.get(arrayDuration[position]));
                    Log.d(TAG, "SPANGBANG_URL_API: " + SPANGBANG_URL_API);
                    Log.d(TAG, "filteredObject: " + filteredObject);
                    customiseSpangbangURL("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerDate = findViewById(R.id.spinnerDate);
        String[] arrayDate = getResources().getStringArray(R.array.date);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(this, R.layout.spinner, arrayDate);
        adapterDate.setDropDownViewResource(R.layout.spinneritem);
        spinnerDate.setAdapter(adapterDate);
        spinnerDate.setSelection(0);

        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurrentPageNum = "1";

                HashMap filterMap = new HashMap();
                filterMap.put("All", "p=all");
                filterMap.put("Today", "p=d");
                filterMap.put("This Week", "p=w");
                filterMap.put("This Month", "p=m");
                filterMap.put("This Year", "p=y");


                for (int i = 0; i < filteredObject.size(); i++) {
                    if (filteredObject.get(i).contains("p=")) {
                        filteredObject.remove(i);
                        if (position == 0) {
                            //this is because when the user switch from trending too all we need to update the videos
                            customiseSpangbangURL("1");
                        }
                    }
                }

                if (position != 0) {
                    filteredObject.add((String) filterMap.get(arrayDate[position]));
                    Log.d(TAG, "SPANGBANG_URL_API: " + SPANGBANG_URL_API);
                    Log.d(TAG, "filteredObject: " + filteredObject);
                    customiseSpangbangURL("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        LinearLayout filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerFilter.performClick();
            }
        });

        LinearLayout quality = findViewById(R.id.quality);
        quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerQuality.performClick();
            }
        });

        LinearLayout duration = findViewById(R.id.duration);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDuration.performClick();
            }
        });

        LinearLayout date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDate.performClick();
            }
        });


    }


    private void getVideoData_API(String bodyURL) {
        loadAds();
        List<VideoModel> collectonData = new ArrayList<>();
        List<String> pageData = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(VideosList.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.chutlunds.live/api/spangbang/getvideos", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("finalDataArray");
                    JSONArray pagesArray = jsonObject.getJSONArray("pages");

                    noVideosFromAPI = jsonObject.getBoolean("noVideos");
                    if (noVideosFromAPI) {
                        setVideosNotFoundMessage();
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        collectonData.add(videoModel);
                    }

                    if (pageData.size() > 0) pageData.clear();
                    for (int i = 0; i < pagesArray.length(); i++) {
                        pageData.add(pagesArray.get(i).toString().trim().replaceAll(" ", ""));
                    }
                    Log.d(TAG, "pageData: " + pagesArray);
                    Log.d(TAG, "collectonData: " + collectonData.size());

                    LinearLayout pageNumberLayout = findViewById(R.id.pageNumberLayout);
                    if (pageData.size() != 0) {
                        setPageNumber(pageData);
                        pageNumberLayout.setVisibility(View.VISIBLE);

                    } else {
                        pageData.add("1");
                        pageData.add("1");
                        setPageNumber(pageData);
                        pageNumberLayout.setVisibility(View.VISIBLE);
                    }
                    progressbar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new VideosAdapter(VideosList.this, collectonData);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSONException: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", bodyURL);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void setVideosNotFoundMessage() {
        nestedScrollView.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        notFoundMessageLayout.setVisibility(View.VISIBLE);
        notFoundGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filteredObject.size() != 0) {
                    Log.d(TAG, "filteredObject: " + filteredObject);
                    if (filteredObject.get(filteredObject.size() - 1).contains("o="))
                        spinnerFilter.setSelection(0);
                    if (filteredObject.get(filteredObject.size() - 1).contains("q="))
                        spinnerQuality.setSelection(0);
                    if (filteredObject.get(filteredObject.size() - 1).contains("p="))
                        spinnerDate.setSelection(0);
                    if (filteredObject.get(filteredObject.size() - 1).contains("d="))
                        spinnerDuration.setSelection(0);

                    filteredObject.remove(filteredObject.size() - 1);
                    customiseSpangbangURL("1");
                    notFoundMessageLayout.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.VISIBLE);
                } else {
                    onBackPressed();
                }
            }
        });
    }

    private void customiseSpangbangURL(String gotoPage) {
        boolean baseFilterAvailable = false; // o=all
        Log.d(TAG, "filteredObject: " + filteredObject);
        String newURL = SPANGBANG_URL_API + gotoPage + "/?";
        if (filteredObject.size() == 0) {
            newURL = SPANGBANG_URL_API + gotoPage + "/?o=all";
        } else {

            for (int i = 0; i < filteredObject.size(); i++) {
                if (filteredObject.get(i).contains("o=")) {
                    baseFilterAvailable = true;
                }
                if (i == 0) {
                    newURL = newURL + filteredObject.get(i);
                } else {
                    newURL = newURL + "&" + filteredObject.get(i);
                }
            }
            if (!baseFilterAvailable) {
                newURL = newURL + "&o=all";
            }
        }
        Log.d(TAG, "SPANGBANG_URL_API: " + newURL);

        progressbar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        notFoundMessageLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
        //this is because the nested scrollview get scrolled little bit itself
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.scrollTo(0, 0);
        getVideoData_API(newURL);

    }

    private void setPageNumber(List<String> pageData) {


        TextView pageNumberTop = findViewById(R.id.pageNumber);
        TextView pageleft = findViewById(R.id.pageleft);
        TextView pageRight = findViewById(R.id.pageRight);
        TextView pageInfo = findViewById(R.id.pageInfoTextView);
        pageInfo.setText(CurrentPageNum + "/" + pageData.get(1) + " " + "Page");
        pageNumberTop.setText(CurrentPageNum + "/" + pageData.get(1));


        if (CurrentPageNum.equals("1")) {
            pageleft.setVisibility(View.GONE);
            pageRight.setVisibility(View.VISIBLE);

        }
        if (CurrentPageNum.equals(pageData.get(1))) {
            pageleft.setVisibility(View.VISIBLE);
            pageRight.setVisibility(View.GONE);
        }
        if (pageData.get(1).equals("1")) { //in case only one page is there 1/1 page
            pageleft.setVisibility(View.GONE);
            pageRight.setVisibility(View.GONE);
        }

        pageleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousPageNum = Integer.parseInt(CurrentPageNum) - 1;
                String GotoPage = String.valueOf(previousPageNum);
                CurrentPageNum = GotoPage;
                customiseSpangbangURL(GotoPage);
            }
        });

        pageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousPageNum = Integer.parseInt(CurrentPageNum) + 1;
                String GotoPage = String.valueOf(previousPageNum);
                CurrentPageNum = GotoPage;
                customiseSpangbangURL(GotoPage);
            }
        });
    }


    private void actionBar() {
        TextView title_collection = findViewById(R.id.title_collection);
        title_collection.setText(getIntent().getStringExtra("Title"));
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void loadAds() {

        if (SplashScreen.adsLoaded == 0 || SplashScreen.adsLoaded == 2) {
            if (SplashScreen.adsLoaded == 0) SplashScreen.adsLoaded = 1;
            if (SplashScreen.adsLoaded == 2) SplashScreen.adsLoaded = 0;
            return;
        }
        final AlertDialog[] dialog = {null};

        WebView webView = null;
        LinearLayout closelayout;
        TextView countDownText;

        String url = "https://passablejeepparliament.com/pvpcafc4kk?key=f25a9b13a509037a68a314ca0278f644";
        String url2 = "https://passablejeepparliament.com/uvm4sgmd?key=1a4c3d6da7024a80c0acf420460a907f";
        final String[] url3 = {"https://www.chutlunds.live/ads/ads.html"};


        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(VideosList.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.ads_dialog, null);
        closelayout = promptView.findViewById(R.id.closelayout);
        countDownText = promptView.findViewById(R.id.countDownText);
        builder.setView(promptView);
        builder.setCancelable(true);
        dialog[0] = builder.create();


        webView = (WebView) promptView.findViewById(R.id.webview);
        closelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].cancel();
            }
        });

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url3[0]);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("TAGA", "onPageStarted " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                Log.d("TAGA", "onPageFinished " + url);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dialog[0].show();
                        new CountDownTimer(5000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                countDownText.setText("Ad will end in  " + millisUntilFinished / 1000 + " seconds");
                            }

                            public void onFinish() {
                                SplashScreen.adsLoaded = 2;
                                dialog[0].cancel();
                            }
                        }.start();
                    }
                };
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(runnable, 2000);
            }
        });


    }


}
