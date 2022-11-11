package com.bhola.chutlundsmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soup.neumorphism.NeumorphButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAGA";
    TextView dataText;
    ImageView image;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    AlertDialog dialog;
    private ReviewManager reviewManager;

    //Google login
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    MenuItem menu_login;
    TextView email;
    LinearLayout loggedInLayout;
    public static boolean userLoggedIn = false;
    public static String authProviderName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationDrawer();

        trendingVideos();
        upcomingVideos();
        popularVideos();
        newVideos();
        searchBar();
        categorySlider();
        checkForAppUpdate();
        getUserLocaitonUsingIP();
        checkLogin();

    }

    private void getUserLocaitonUsingIP() {
        String API_URL = " https://api.db-ip.com/v2/free/self";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            SplashScreen.countryLocation = jsonObject.getString("countryName");
                            SplashScreen.countryCode = jsonObject.getString("countryCode");
                            showLocationVideos();

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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showLocationVideos() throws JSONException {
        JSONArray jsonArrayyy = new JSONArray(loadJSONFromAsset("CountryList" + ".json"));
        for (int j = 0; j < jsonArrayyy.length(); j++) {
            JSONObject obj = (JSONObject) jsonArrayyy.get(j);
            if (obj.getString("CountryName").toString().toLowerCase().trim().equals(SplashScreen.countryLocation.trim().toLowerCase())) {
                getVideoData_API("https://spankbang.com/s/" + obj.getString("language").trim().toLowerCase() + "/?o=all");
            }
        }

    }

    private void checkForAppUpdate() {
        if (SplashScreen.Firebase_Version_Code != BuildConfig.VERSION_CODE) {

            Button updateBtn;
            TextView yourVersion, latestVersion;
            final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View promptView = inflater.inflate(R.layout.appupdate, null);
            builder.setView(promptView);
            builder.setCancelable(true);


            updateBtn = promptView.findViewById(R.id.UpdateBtn);
            yourVersion = promptView.findViewById(R.id.currentVersion);
            yourVersion.setText("Your Version: " + BuildConfig.VERSION_CODE);
            latestVersion = promptView.findViewById(R.id.NewerVersion);
            latestVersion.setText("Latest Version: " + SplashScreen.Firebase_Version_Code);
            updateBtn = promptView.findViewById(R.id.UpdateBtn);

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(SplashScreen.apk_Downloadlink));
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception: " + e.getMessage());
                    }
                }
            });


            AlertDialog dialog2 = builder.create();
            dialog2.show();
        }
    }

    private void categorySlider() {
        ArrayList<HashMap<String, String>> Category_List = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> m_li;
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("category.json"));
            JSONArray m_jArry = obj.getJSONArray("category");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject json_obj = m_jArry.getJSONObject(i);

                String name = json_obj.getString("name");
                String url = json_obj.getString("url");

                //Add your values in your `ArrayList` as below:
                m_li = new HashMap<String, String>();
                m_li.put("name", name);
                m_li.put("url", url);
                Category_List.add(m_li);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "categorySlider: " + e.getMessage());

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView_categorySlider = findViewById(R.id.recyclerView_categorySlider);
        recyclerView_categorySlider.setLayoutManager(layoutManager);
        CategorySliderAdapter adapter = new CategorySliderAdapter(MainActivity.this, Category_List);
        recyclerView_categorySlider.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void searchBar() {
        ImageView searchIcon = findViewById(R.id.searchIcon);
        LinearLayout searchBar = findViewById(R.id.searchBar);
        TextView goSearch = findViewById(R.id.goSearch);
        EditText searchKeyword = findViewById(R.id.searchKeyword);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBar.getVisibility() == View.VISIBLE) {
                    searchBar.setVisibility(View.GONE);
                    searchIcon.setImageResource(R.drawable.search);

                } else {
                    searchIcon.setImageResource(R.drawable.close);
                    searchBar.setVisibility(View.VISIBLE);
                }
            }
        });

        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchKeyword.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter keyword", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(v.getContext(), VideosList.class);
                    intent.putExtra("Title", searchKeyword.getText().toString().trim());
                    intent.putExtra("url", "https://spankbang.com/s/" + searchKeyword.getText().toString().trim() + "/");
                    startActivity(intent);
                }
            }
        });


        searchKeyword.setImeActionLabel("Search", EditorInfo.IME_ACTION_UNSPECIFIED);
        searchKeyword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    goSearch.performClick();
                    return true;
                }
                return false;
            }
        });

        searchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    suggestedTagsShow(s.toString().trim().toLowerCase(), searchKeyword);
                } catch (JSONException e) {
                    Log.d(TAG, "afterTextChanged: " + e.getMessage());
                }
            }
        });


    }

    private void suggestedTagsShow(String searchKey, EditText searchKeyword) throws JSONException {
        LinearLayout suggestedtagsLayout = findViewById(R.id.suggestedtagsLayout);
        suggestedtagsLayout.removeAllViews();

        char[] tagsArrayFilename = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        ArrayList alltags = new ArrayList();

        if (searchKey.length() < 3) {
            return;
        }
        String FIRST_LETTER = searchKey.substring(0, 1).toUpperCase();

        JSONArray spangbangTags_array = new JSONArray(loadJSONFromAsset("tags/spangbang_tags.json"));
        for (int j = 0; j < spangbangTags_array.length(); j++) {
            if (spangbangTags_array.get(j).toString().contains(searchKey)) {
                alltags.add(spangbangTags_array.get(j));
            }
        }


        JSONArray jsonArray = new JSONArray(loadJSONFromAsset("tags/" + FIRST_LETTER + ".json"));
        for (int j = 0; j < jsonArray.length(); j++) {
            if (jsonArray.get(j).toString().contains(searchKey)) {
                alltags.add(jsonArray.get(j));
            }
        }

        for (int i = 0; i < tagsArrayFilename.length; i++) {
            if (i == 10 || String.valueOf(tagsArrayFilename[i]).equals(FIRST_LETTER)) {
            } else {

                JSONArray jsonArrayyy = new JSONArray(loadJSONFromAsset("tags/" + tagsArrayFilename[i] + ".json"));
                for (int j = 0; j < jsonArrayyy.length(); j++) {
                    if (jsonArrayyy.get(j).toString().contains(searchKey)) {
                        alltags.add(jsonArrayyy.get(j));
                    }
                }
            }
        }

        for (int i = 0; i < alltags.size(); i++) {

            View view = getLayoutInflater().inflate(R.layout.suggested_textview, null);
            TextView suggestedText = view.findViewById(R.id.spinnerText);
            String tagText = alltags.get(i).toString().trim().toLowerCase();
            suggestedText.setText(tagText);
            view.setBackgroundResource(R.drawable.tagshovereffect);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(5, 5, 15, 5);
            suggestedText.setLayoutParams(params);

            suggestedText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    searchKeyword.setText(tagText);
                    Intent intent = new Intent(v.getContext(), VideosList.class);
                    intent.putExtra("Title", tagText);
                    intent.putExtra("url", "https://spankbang.com/s/" + tagText + "/");
                    startActivity(intent);
                }
            });

            suggestedtagsLayout.addView(view);
        }
    }

    private void trendingVideos() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager = new GridLayoutManager(this, 1);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_Trending);

        recyclerView.setLayoutManager(layoutManager);
        Adapter adapter = new Adapter(MainActivity.this, SplashScreen.Trending_collectonData);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        LinearLayout trendingVideos = findViewById(R.id.trendingVideos);
        trendingVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VideosList.class);
                intent.putExtra("Title", "Trending Videos");
                intent.putExtra("url", "https://spankbang.com/trending_videos/");
                startActivity(intent);
            }
        });


    }

    private void upcomingVideos() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_Upcoming);

        recyclerView.setLayoutManager(layoutManager);
        Adapter adapter = new Adapter(MainActivity.this, SplashScreen.Upcoming_collectonData);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LinearLayout trendingVideos = findViewById(R.id.upcomingVideos);
        trendingVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VideosList.class);
                intent.putExtra("Title", "Upcoming Videos");
                intent.putExtra("url", "https://spankbang.com/upcoming/");

                startActivity(intent);
            }
        });
    }

    private void popularVideos() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_Popular);

        recyclerView.setLayoutManager(layoutManager);
        Adapter adapter = new Adapter(MainActivity.this, SplashScreen.Popular_collectonData);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LinearLayout trendingVideos = findViewById(R.id.popularVideos);
        trendingVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VideosList.class);
                intent.putExtra("Title", "Popular Videos");
                intent.putExtra("url", "https://spankbang.com/most_popular/");

                startActivity(intent);
            }
        });
    }

    private void newVideos() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_New);

        recyclerView.setLayoutManager(layoutManager);
        Adapter adapter = new Adapter(MainActivity.this, SplashScreen.New_collectonData);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LinearLayout trendingVideos = findViewById(R.id.newVideos);
        trendingVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VideosList.class);
                intent.putExtra("Title", "New Videos");
                intent.putExtra("url", "https://spankbang.com/new_videos/");

                startActivity(intent);
            }
        });
    }


    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = MainActivity.this.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onBackPressed() {
        exit_dialog();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }


    private void exit_dialog() {

        NeumorphButton exit, exit2;
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(nav.getContext());
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.exit_dialog, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        if (!(SplashScreen.Login_Times < 4)) {
            TextView exitMSG;
            exitMSG = promptView.findViewById(R.id.exitMSG);
            exitMSG.setVisibility(View.VISIBLE);
            init(); // Show PLay store Review option
        }


        exit = promptView.findViewById(R.id.exit_button2);
        exit2 = promptView.findViewById(R.id.exit_button1);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishAffinity();
                finish();
                System.exit(0);
                finish();
                dialog.dismiss();
            }
        });

        exit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        dialog = builder.create();
        dialog.show();

    }

    private void navigationDrawer() {
        ImageView openDrawer = (ImageView) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT); //Edit Gravity.START need API 14

            }
        });

        nav = (NavigationView) findViewById(R.id.navmenu);
        nav.setItemIconTintList(null);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {


                    case R.id.menu_contacts:
                        TextView whatsapp, email;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                        View promptView = inflater.inflate(R.layout.navigation_menu_contacts, null);
                        builder.setView(promptView);
                        builder.setCancelable(true);
                        whatsapp = promptView.findViewById(R.id.whatsappnumber);
                        whatsapp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", "+919108825914");
                                clipboard.setPrimaryClip(clip);
                                navigationDrawer();
                                Toast.makeText(v.getContext(), "COPIED NUMBER", Toast.LENGTH_SHORT).show();
                            }
                        });
                        email = promptView.findViewById(R.id.email);
                        email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", "bhola2266@gmail.com");
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(v.getContext(), "COPIED EMAIL", Toast.LENGTH_SHORT).show();
                            }
                        });


                        dialog = builder.create();
                        dialog.show();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;

                    case R.id.menu_rating:


                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(SplashScreen.Main_App_url1));
                        startActivity(i);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_notificaton:

                        startActivity(new Intent(MainActivity.this, Category.class));
                        break;


                    case R.id.menu_share_app:
                        String share_msg = "Hi I have downloaded Hindi Desi Kahani App.\n" +
                                "It is a best app for Real Desi Bed Stories.\n" +
                                "You should also try\n" +
                                SplashScreen.Main_App_url1;
                        Intent intent1 = new Intent();
                        intent1.setAction(Intent.ACTION_SEND);
                        intent1.putExtra(Intent.EXTRA_TEXT, share_msg);
                        intent1.setType("text/plain");
                        Intent intent = Intent.createChooser(intent1, "Share By");
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_second_app:

                        if (SplashScreen.Refer_App_url2.length() > 10) {
                            Intent j = new Intent(Intent.ACTION_VIEW);
                            j.setData(Uri.parse(SplashScreen.Refer_App_url2));
                            startActivity(j);
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        break;

                    case R.id.Privacy_Policy:

                        try {
                            Intent i5 = new Intent(Intent.ACTION_VIEW);
                            i5.setData(Uri.parse("https://sites.google.com/view/desikhaniya"));
                            startActivity(i5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.About_Us:

                        final androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(nav.getContext());
                        LayoutInflater inflater2 = LayoutInflater.from(getApplicationContext());
                        View promptView2 = inflater2.inflate(R.layout.about_us, null);
                        builder2.setView(promptView2);
                        builder2.setCancelable(true);


                        dialog = builder2.create();
                        dialog.show();

                        break;


                    case R.id.Terms_and_Condition:
                        Intent intent27 = new Intent(getApplicationContext(), TermsAndConditions.class);
                        startActivity(intent27);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });
    }

    private void init() {
        reviewManager = ReviewManagerFactory.create(this);
        // Referencing the button
        showRateApp();
    }


    // Shows the app rate dialog box using In-App review API
    // The app rate dialog box might or might not shown depending
    // on the Quotas and limitations
    public void showRateApp() {
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                });
            }
        });
    }

    private void getVideoData_API(String bodyURL) {
        List<VideoModel> collectonData = new ArrayList<>();
        List<String> pageData = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.chutlunds.live/api/spangbang/getvideos", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("finalDataArray");
                    JSONArray pagesArray = jsonObject.getJSONArray("pages");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        collectonData.add(videoModel);
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    TextView textView = findViewById(R.id.countryNameTextviews);
                    int flagOffset = 0x1F1E6;
                    int asciiOffset = 0x41;

                    String country = SplashScreen.countryCode;

                    int firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset;
                    int secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset;

                    String flag = new String(Character.toChars(firstChar))
                            + new String(Character.toChars(secondChar));
                    textView.setText("Popular videos in " + SplashScreen.countryLocation + " " + flag);
                    TextView NavbarChutlundsTitle = findViewById(R.id.NavbarChutlundsTitle);
                    NavbarChutlundsTitle.setText("Chutlunds.live " + flag);


                    RecyclerView recyclerView_Country = findViewById(R.id.recyclerView_Country);
                    LinearLayout countryVideoTopbar = findViewById(R.id.countryVideoTopbar);
                    countryVideoTopbar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), VideosList.class);
                            intent.putExtra("Title", "Popular in " + SplashScreen.countryLocation + flag);
                            intent.putExtra("url", bodyURL);
                            startActivity(intent);
                        }
                    });
                    LinearLayout countryVideo = findViewById(R.id.countryVideo);
                    countryVideo.setVisibility(View.VISIBLE);
                    Adapter adapter = new Adapter(MainActivity.this, collectonData);
                    recyclerView_Country.setLayoutManager(layoutManager);
                    recyclerView_Country.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

//                    startActivity(new Intent(MainActivity.this,AdsterraAds.class));

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


    private void checkLogin() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navmenu);
        View headerView = navigationView.getHeaderView(0);
        loggedInLayout = headerView.findViewById(R.id.loggedInLayout);
        Menu menu = navigationView.getMenu();

        menu_login = menu.findItem(R.id.menu_login);
        email = headerView.findViewById(R.id.email);

        menu_login.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!userLoggedIn) {
                    startActivity(new Intent(MainActivity.this, login.class));
                } else {
                    if (authProviderName.equals("google.com")) {
                        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                        gsc = GoogleSignIn.getClient(MainActivity.this, gso);
                        gsc.signOut().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                FirebaseAuth.getInstance().signOut();
                                loggedInLayout.setVisibility(View.GONE);
                                drawerLayout.closeDrawer(Gravity.LEFT);
                                finish();
                                menu_login.setTitle("Log In");
                                startActivity(getIntent());
                            }
                        });

                    }
                    if (authProviderName.equals("facebook.com")) {


                    }
                    if (authProviderName.equals("password")) {
                        FirebaseAuth.getInstance().signOut();
                        loggedInLayout.setVisibility(View.GONE);
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        finish();
                        menu_login.setTitle("Log In");
                        startActivity(getIntent());
                    }
                    userLoggedIn=false;

//                    String LoginWith = "google";
//                    if (LoginWith.equals("google")) {
//                        loggedInLayout.setVisibility(View.GONE);
//                        menu_login.setTitle("Log In");
//
//                    } else if (LoginWith.equals("facebook")) {
//                        LoginManager.getInstance().logOut();
//                        drawerLayout.closeDrawer(Gravity.LEFT);
//                        finish();
//                        startActivity(getIntent());
//                    }
                }
                return false;
            }
        });


    }

    private void facebookLoginStuffs() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.d(TAG, "onCompleted: " + object);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture.type(large");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            authProviderName = user.getProviderData().get(user.getProviderData().size() - 1).getProviderId();
            Log.d(TAG, "AuthProvider: " + authProviderName);
            userLoggedIn = true;
            menu_login.setTitle("Log Out");
            loggedInLayout.setVisibility(View.VISIBLE);
            String personName = user.getDisplayName();
            String personEmail = user.getEmail();
//            name.setText(personName);
            email.setText(personEmail);
        }

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.d(TAG, "FirebaseUser: " + user);
        Log.d(TAG, "GoogleSignInAccount: " + acct);


    }
}


class Adapter extends RecyclerView.Adapter<Adapter.viewholder> {

    Context context;
    List<VideoModel> videoData;


    public Adapter(Context context, List<VideoModel> videoData) {
        this.context = context;
        this.videoData = videoData;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.video_thumnail, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onViewRecycled(viewholder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        VideoModel item = videoData.get(position);

        Picasso.with(context).load(item.getThumbnail()).into(holder.thumbnail);
        holder.title.setText(item.getTitle());
        holder.duration.setText(item.getDuration());
        holder.views.setText(item.getViews());
        holder.likes.setText(item.getLikedPercent());

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FullscreenVideoPLayer.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("href", item.getHref());
                intent.putExtra("thumbnail", item.getThumbnail());
                v.getContext().startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return videoData.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView duration;
        TextView views;
        TextView likes;


        public viewholder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnailImage);
            title = itemView.findViewById(R.id.video_title);
            duration = itemView.findViewById(R.id.duration);
            views = itemView.findViewById(R.id.views);
            likes = itemView.findViewById(R.id.likes);


        }


    }
}


class CategorySliderAdapter extends RecyclerView.Adapter<CategorySliderAdapter.viewholder> {

    Context context;
    ArrayList<HashMap<String, String>> collectionData;


    public CategorySliderAdapter(Context context, ArrayList<HashMap<String, String>> collectionData) {
        this.context = context;
        this.collectionData = collectionData;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.category_slider_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onViewRecycled(viewholder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        String url = collectionData.get(position).get("url");
        String title = collectionData.get(position).get("name").replace(".png", "").toUpperCase();

        Picasso.with(context).load(url).into(holder.thumbnail);
        holder.title.setText(title);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VideosList.class);
                intent.putExtra("Title", title.trim().toLowerCase());
                intent.putExtra("url", "https://spankbang.com/s/" + title.trim().toLowerCase() + "/");
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return collectionData.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageview);
            title = itemView.findViewById(R.id.categorytextview);
        }
    }
}




