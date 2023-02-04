package com.bhola.chutlundsmobileapp;


import static com.google.android.ads.mediationtestsuite.utils.DataStore.getContext;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "TAGA";
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView textView;
    LottieAnimationView lottie;

    public static String Notification_Intent_Firebase = "inactive";
    public static String Ad_Network_Name = "facebook";
    public static String Main_App_url1 = "https://play.google.com/store/apps/details?id=com.bhola.HindidesiKahaniya2";
    public static String Refer_App_url2 = "https://play.google.com/store/apps/developer?id=UK+DEVELOPERS";
    public static String Ads_State = "active";
    public static boolean update_Mandatory = true;
    public static int admin_panel_passcode = -1;
    public static String Notification_ImageURL = "https://hotdesipics.co/wp-content/uploads/2022/06/Hot-Bangla-Boudi-Ki-Big-Boobs-Nangi-Selfies-_002.jpg";
    public static int Login_Times = 0;
    public static int Firebase_Version_Code = 0;
    public static String apk_Downloadlink = "";
    public static String countryLocation = "";
    public static String countryCode = "";
    boolean internetAvailable = false;

    public static List<VideoModel> Trending_collectonData, Upcoming_collectonData, Popular_collectonData, New_collectonData;

    boolean API_LOAD_FINISHED = false;
    boolean LOTTIE_ANIM_FINISHED = false;
    boolean FIREBASE_LOADED = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static int adsLoaded = 1; //0,2=dont load   1= load

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreenMode();
        setContentView(R.layout.splash_screen);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        allUrl();
//        readJSON();
        sharedPrefrences();

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        textView = findViewById(R.id.textView_splashscreen);
        lottie = findViewById(R.id.lottie);


        textView.setAnimation(bottomAnim);
        lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LinearLayout progressbar = findViewById(R.id.progressbar);
                progressbar.setVisibility(View.VISIBLE);
                LOTTIE_ANIM_FINISHED = true;

                if (API_LOAD_FINISHED && FIREBASE_LOADED) {
                    gotoMainActivity();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if (isInternetAvailable(SplashScreen.this)) {
            internetAvailable = true;
            String API_URL = "https://www.chutlunds.com/api/spangbang/homepage";
            HomepageVideoAPI(API_URL, this);
        } else {
            internetAvailable = false;
            Button noInternet = findViewById(R.id.noInternet);
            TextView loadingText = findViewById(R.id.loadingText);
            loadingText.setText("No internet...");
            noInternet.setVisibility(View.VISIBLE);
            noInternet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(new Intent(SplashScreen.this, SplashScreen.class));
                }
            });
        }


        generateNotification();
        generateFCMToken();

    }


    private void allUrl() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Firebase_Version_Code = dataSnapshot.child("version_code").getValue(Integer.class);
                admin_panel_passcode = dataSnapshot.child("admin_panel_passcode").getValue(Integer.class);
                apk_Downloadlink = dataSnapshot.child("apk_Downloadlink").getValue().toString();
                Ads_State = dataSnapshot.child("Ads_State").getValue().toString();
                update_Mandatory = (boolean) dataSnapshot.child("update_Mandatory").getValue();

                FIREBASE_LOADED = true;
                if (API_LOAD_FINISHED && LOTTIE_ANIM_FINISHED) {
                    gotoMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void sharedPrefrences() {

        //Reading Login Times
        SharedPreferences sh = getSharedPreferences("UserInfo", MODE_PRIVATE);
        int a = sh.getInt("loginTimes", 0);
        Login_Times = a + 1;

        // Updating Login Times data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("loginTimes", a + 1);
        myEdit.commit();

    }


    private void generateFCMToken() {

        if (getIntent() != null && getIntent().hasExtra("KEY1")) {
            if (getIntent().getExtras().getString("KEY1").equals("Notification_Story")) {
                Notification_Intent_Firebase = "active";
            }
        }
    }


    private void generateNotification() {


        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                String msg = "Failed";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void HomepageVideoAPI(String API_URL, Context context) {

        Trending_collectonData = new ArrayList<>();
        Upcoming_collectonData = new ArrayList<>();
        Popular_collectonData = new ArrayList<>();
        New_collectonData = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
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
                                Trending_collectonData.add(videoModel);
                            }

                            for (int i = 0; i < jsonArray2_Upcoming.length(); i++) {
                                JSONObject obj = jsonArray2_Upcoming.getJSONObject(i);
                                VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                                Upcoming_collectonData.add(videoModel);
                            }

                            for (int i = 0; i < jsonArray3_Popular.length(); i++) {
                                JSONObject obj = jsonArray3_Popular.getJSONObject(i);
                                VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                                Popular_collectonData.add(videoModel);
                            }

                            for (int i = 0; i < jsonArray4_New.length(); i++) {
                                JSONObject obj = jsonArray4_New.getJSONObject(i);
                                VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                                New_collectonData.add(videoModel);
                            }

                            API_LOAD_FINISHED = true;
                            if (LOTTIE_ANIM_FINISHED && FIREBASE_LOADED) {
                                gotoMainActivity();
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    boolean isInternetAvailable(Context context) {
        if (context == null) return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ");
        return false;
    }


    private void fullscreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void gotoMainActivity() {
        if (!internetAvailable) {
            return;
        }
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
    }


}