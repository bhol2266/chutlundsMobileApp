package com.bhola.chutlundsmobileapp;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class admin_panel extends AppCompatActivity {
    public static int counter = 0;

    DatabaseReference mref, notificationMref;
    FirebaseFirestore firestore;
    TextView totalInstallsAllTime, totalInstallsToday, totalSignUpsAlltime, totalSignUpsToday;
    Button selectStory, insertBTN, Refer_App_url_BTN, STory_Switch_Active_BTN;
    Switch switch_Exit_Nav, switch_Activate_Ads, switch_Sex_Story;
    Button Ad_Network;
    static String uncensored_title = "";
    String TAG = "TAGA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpanel);


        initViews();
        totalInstallsAlltime();

    }


    private void initViews() {

        firestore = FirebaseFirestore.getInstance();
        totalInstallsAllTime = findViewById(R.id.totalInstallsAllTime);
        totalInstallsToday = findViewById(R.id.totalInstallsToday);

        totalSignUpsAlltime = findViewById(R.id.totalSignUpsAlltime);
        totalSignUpsToday = findViewById(R.id.totalSignUpsToday);

        notificationMref = FirebaseDatabase.getInstance().getReference();
        switch_Activate_Ads = findViewById(R.id.Activate_Ads);
        switch_Sex_Story = findViewById(R.id.Sex_Story);
        switch_Exit_Nav = findViewById(R.id.switch_Exit_Nav);


    }

    private void totalInstallsAlltime() {
        firestore.collection("Devices").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> keywords = new ArrayList<>();
                    int totalInstallsAlltimeCount = 0;
                    int totalInstallsTodaycount = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int date_fromDb = 0;
                        int month_fromDb = 0;
                        totalInstallsAlltimeCount = totalInstallsAlltimeCount + 1;
                        Timestamp timestamp = (Timestamp) document.getData().get("Date");
                        Date date = new Date(String.valueOf(timestamp.toDate()));
                        date_fromDb = date.getDate();
                        month_fromDb = date.getMonth() + 1;

                        Date todaDate = new java.util.Date();
                        int currentDate= todaDate.getDate() ;
                        int currentMonth= todaDate.getMonth() + 1;

                        if(date_fromDb==currentDate && month_fromDb==currentMonth){
                            totalInstallsTodaycount = totalInstallsTodaycount + 1;
                        }

                    }
                    totalInstallsAllTime.setText("Total Installs All Time:    " + totalInstallsAlltimeCount);
                    totalInstallsToday.setText("Total Installs Today:       "+totalInstallsTodaycount);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        firestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> keywords = new ArrayList<>();
                    int totalSignUpsAlltimeCount = 0;
                    int totalSignUpsTodaycount = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int date_fromDb = 0;
                        int month_fromDb = 0;
                        totalSignUpsAlltimeCount = totalSignUpsAlltimeCount + 1;
                        Timestamp timestamp = (Timestamp) document.getData().get("date");
                        Date date = new Date(String.valueOf(timestamp.toDate()));
                        date_fromDb = date.getDate();
                        month_fromDb = date.getMonth() + 1;

                        Date todaDate = new java.util.Date();
                        int currentDate= todaDate.getDate() ;
                        int currentMonth= todaDate.getMonth() + 1;

                        if(date_fromDb==currentDate && month_fromDb==currentMonth){
                            totalSignUpsTodaycount = totalSignUpsTodaycount + 1;
                        }


                    }
                    totalSignUpsAlltime.setText("Total SignUp All Time:     "+ totalSignUpsAlltimeCount);
                    totalSignUpsToday.setText("Total SignUp Today:        "+totalSignUpsTodaycount);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

}


