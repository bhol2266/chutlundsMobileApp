package com.bhola.chutlundsmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Category extends AppCompatActivity {

    String TAG="taga";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        categorySlider();
        actionBar();
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
            Log.d(TAG, "categorySlider: "+e.getMessage());

        }
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);

        RecyclerView recyclerView_categorySlider =findViewById(R.id.recyclerView_category);
        recyclerView_categorySlider.setLayoutManager(layoutManager);
        CategoryAdapter adapter = new CategoryAdapter(Category.this,Category_List);
        recyclerView_categorySlider.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = Category.this.getAssets().open("category.json");
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

    private void actionBar() {

        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {

    Context context;
    ArrayList<HashMap<String, String>> collectionData;


    public CategoryAdapter(Context context, ArrayList<HashMap<String, String>> collectionData) {
        this.context = context;
        this.collectionData = collectionData;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.category, parent, false);
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

        Picasso.get().load(url).into(holder.thumbnail);
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
