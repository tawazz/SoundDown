package net.tawazz.sounddown;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by tawanda on 2/10/15.
 */
public class Track {

    private String details,artworkUrl,streamUrl;
    private Bitmap artwork;

    public Track(String songDetails,String artwork, String mp3){
        details = songDetails;
        artworkUrl = artwork;
        streamUrl = mp3;
        new GetArtwork().execute(artworkUrl);
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
