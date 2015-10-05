package net.tawazz.sounddown;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by tawanda on 2/10/15.
 */
public class Track {

    private String title;
    private String artworkUrl;
    private String streamUrl;
    private String user;
    private String like;
    private String time;
    private Bitmap artwork;

    public Track(String user,String title,String artwork, String mp3, String likes, String time){
        this.user = user;
        this.title = title;
        new GetArtwork().execute(artwork);
        streamUrl = mp3;
        this.like = likes;
        this.time = time;
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

    public Bitmap getArtwork() {

        return artwork;
    }
    public String getTime() {
        int time = Integer.parseInt(this.time);
        int mins = (time/60000)%60;
        int secs = (time%60000)/1000;

        if(secs < 10){
            return mins+":0"+secs;
        }else{
            return mins+":"+secs;
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
