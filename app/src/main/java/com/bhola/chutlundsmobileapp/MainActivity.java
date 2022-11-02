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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
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

    }

    private void categorySlider() {
        ArrayList<HashMap<String, String>> Category_List = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> m_li;
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
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
            Collections.shuffle(Category_List);

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
                } else {
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


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = MainActivity.this.getAssets().open("category.json");
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

                       startActivity(new Intent(MainActivity.this,Category.class));
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




