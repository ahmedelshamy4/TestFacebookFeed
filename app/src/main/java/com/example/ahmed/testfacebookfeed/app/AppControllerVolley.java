package com.example.ahmed.testfacebookfeed.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.ahmed.testfacebookfeed.volley.LruBitmapCache;

/**
 * Created by Ahmed on 3/12/2018.
 */

public class AppControllerVolley extends Application {
   /*This is a singleton class which initializes global instances of required classes.
     All the objects related to volley are initialized here.*/

    public static final String TAG = AppControllerVolley.class.getSimpleName();
    RequestQueue queue;
    private static AppControllerVolley application;
    ImageLoader imageLoader;
    LruBitmapCache cache;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static synchronized AppControllerVolley getInstance() {
        return application;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        return queue;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (cache == null)
            cache = new LruBitmapCache();
        return this.cache;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            getLruBitmapCache();
            imageLoader = new ImageLoader(this.queue, cache);
        }
        return this.imageLoader;
    }

    public void addToRequestQueue(Request request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(Request request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (queue != null) {
            queue.cancelAll(tag);
        }

    }
}
