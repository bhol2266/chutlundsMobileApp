package com.bhola.chutlundsmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import soup.neumorphism.NeumorphButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="TAGA" ;
    TextView dataText;
    ImageView image;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    AlertDialog dialog;
    private ReviewManager reviewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Trending_collectonData: "+SplashScreen.Trending_collectonData.size());
        Log.d(TAG, "Upcoming_collectonData: "+SplashScreen.Upcoming_collectonData.size());
        Log.d(TAG, "Popular_collectonData: "+SplashScreen.Popular_collectonData.size());
        Log.d(TAG, "New_collectonData: "+SplashScreen.New_collectonData.size());

        navigationDrawer();
        Button fetchBtn =findViewById(R.id.fetchBtn);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAPI_DATA();
            }
        });
   }

    private void getAPI_DATA() {
//        String API_URL = "https://www.chutlunds.live/api/spangbang/homepage";
//        API_CONFIG.HomepageVideoAPI(API_URL, MainActivity.this);
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

        if (!(SplashScreen.Login_Times < 4)){
            TextView exitMSG;
            exitMSG =promptView.findViewById(R.id.exitMSG);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav = (NavigationView) findViewById(R.id.navmenu);
        nav.setItemIconTintList(null);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
}


