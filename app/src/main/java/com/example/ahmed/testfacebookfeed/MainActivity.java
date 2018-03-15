package com.example.ahmed.testfacebookfeed;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ahmed.testfacebookfeed.adapter.FeedListAdapter;
import com.example.ahmed.testfacebookfeed.app.AppControllerVolley;
import com.example.ahmed.testfacebookfeed.data.FeedItem;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<FeedItem> feedItems;
    FeedListAdapter adapter;
    private String URL_FEED = "https://api.androidhive.info/feed/feed.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Facebook Feed");

        listView = findViewById(R.id.list);
        feedItems = new ArrayList<>();
        adapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(adapter);

        Cache cache = AppControllerVolley.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_FEED, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            parseJsonFeed(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            // Adding request to volley request queue
            AppControllerVolley.getInstance().addToRequestQueue(request);
        }

    }

    // Parsing json reponse and passing the data to feed view list adapter
    private void parseJsonFeed(JSONObject response) throws JSONException {
        JSONArray array = response.getJSONArray("feed");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            FeedItem item = new FeedItem();
            item.setId(object.getInt("id"));
            item.setName(object.getString("name"));

            // Image might be null sometimes
            String image = object.isNull("image") ? null : object.getString("image");
            item.setImage(image);

            item.setStatus(object.getString("status"));
            item.setProfilePic(object.getString("profilePic"));
            item.setTimeStamp(object.getString("timeStamp"));

            // url might be null sometimes
            String url = object.isNull("url") ? null : object.getString("url");
            item.setUrl(url);

            feedItems.add(item);

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
