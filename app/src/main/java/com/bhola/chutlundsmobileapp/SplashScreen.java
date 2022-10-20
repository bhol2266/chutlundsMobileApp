package com.bhola.chutlundsmobileapp;


import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    public static String Ads_State = "inactive";
    public static String DB_NAME = "MCB_Story";
    public static String Notification_ImageURL = "https://hotdesipics.co/wp-content/uploads/2022/06/Hot-Bangla-Boudi-Ki-Big-Boobs-Nangi-Selfies-_002.jpg";
    DatabaseReference url_mref;
    public static int Login_Times = 0;
    public static List<Object> Trending_collectonData, Upcoming_collectonData, Popular_collectonData, New_collectonData;

    Handler handlerr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


        allUrl();
//        readJSON();
        sharedPrefrences();
        getAPI_DATA();

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
//                startActivity(new Intent(SplashScreen.this,MainActivity.class));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        generateNotification();
        generateFCMToken();

    }


    private void allUrl() {
        if (!isInternetAvailable(SplashScreen.this)) {

            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 2000);

            return;
        } else {
            handlerr = new Handler();
            handlerr.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 9000);

        }
        url_mref = FirebaseDatabase.getInstance().getReference().child("Hindi_desi_Kahani-2");
        url_mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Refer_App_url2 = (String) snapshot.child("Refer_App_url2").getValue();
                Ads_State = (String) snapshot.child("Ads").getValue();
                Ad_Network_Name = (String) snapshot.child("Ad_Network").getValue();
                Notification_ImageURL = (String) snapshot.child("Notification_ImageURL").getValue();

                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handlerr.removeCallbacksAndMessages(null);
                    }
                }, 500);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        String msg = "Failed";
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAPI_DATA() {
        Trending_collectonData = new ArrayList<>();
        Upcoming_collectonData = new ArrayList<>();
        Popular_collectonData = new ArrayList<>();
        New_collectonData = new ArrayList<>();

        String API_URL = "https://www.chutlunds.live/api/spangbang/homepage";
        boolean api_loaded = API_CONFIG.HomepageVideoAPI(API_URL, SplashScreen.this);

        if (api_loaded) {
            Log.d(TAG, "Trending_collectonData: "+Trending_collectonData.size());
            Log.d(TAG, "Upcoming_collectonData: "+Upcoming_collectonData.size());
            Log.d(TAG, "Popular_collectonData: "+Popular_collectonData.size());
            Log.d(TAG, "New_collectonData: "+New_collectonData.size());
        }
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

}