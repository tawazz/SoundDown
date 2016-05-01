package net.tawazz.sounddown.helpers;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
 * Created by tawanda on 2/03/2016.
 */
public class WebRequest {

    private static WebRequest instance = null;
    private final RequestQueue requestQueue;
    private final ImageLoader imageLoader;

    private WebRequest() {

        requestQueue = Volley.newRequestQueue(ApplicationData.getContext());
        imageLoader = new ImageLoader(this.requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    public static WebRequest getInstance() {

        if (instance == null) {
            instance = new WebRequest();
        }

        return instance;
    }
}