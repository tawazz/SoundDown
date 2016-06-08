package net.tawazz.sounddown;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import net.tawazz.sounddown.helpers.WebRequest;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by tawanda on 2/10/15.
 */
public class Track {

    private String title;
    private String artworkUrl;
    private String streamUrl;
    private String previewUrl;
    private String user;
    private String like;
    private String time;
    private Bitmap artwork;
    private WebRequest request;

    public Track(String user, String title, String artwork, String mp3, String likes, String time, String previewUrl) {
        this.user = user;
        this.title = title;
        artwork = artwork.replace("-large","-crop");
        /** method deprecated
        new GetArtwork().execute(artwork);
        **/

        streamUrl = mp3;
        this.like = likes;
        this.time = time;
        this.previewUrl = previewUrl;
        request = WebRequest.getInstance();
        RequestQueue queue = request.getRequestQueue();
        artwork.replace("large","crop");
        ImageRequest imageRequest = new ImageRequest(artwork, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Track.this.artwork = response;
            }
        },400,400, ImageView.ScaleType.CENTER_CROP,null,null);

        queue.add(imageRequest);
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public Bitmap getArtwork() {

        return artwork;
    }

    public String getTime() {
        int time = Integer.parseInt(this.time);
        int mins = (time / 60000) % 60;
        int secs = (time % 60000) / 1000;

        if (secs < 10) {
            return mins + ":0" + secs;
        } else {
            return mins + ":" + secs;
        }
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    private class GetArtwork extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                artwork = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return artwork;
        }

        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
        }
    }
}
