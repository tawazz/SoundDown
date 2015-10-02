package net.tawazz.sounddown;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by tawanda on 2/10/15.
 */
class SongsAdapter extends ArrayAdapter<String> {

    Bitmap bitmap;

    public SongsAdapter(Context context, String[] songs) {
        super(context,R.layout.custom_row, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflator = LayoutInflater.from(getContext());
        View customView = inflator.inflate(R.layout.custom_row, parent, false);

        String song = getItem(position);
        TextView details = (TextView) customView.findViewById(R.id.textView_details);
        ImageView artwork = (ImageView) customView.findViewById(R.id.imageView_artwork);
        new LoadImage().execute("https://i1.sndcdn.com/artworks-000124647570-nlo2r5-large.jpg");
        details.setText(song);
        artwork.setImageBitmap(bitmap);

        return customView;
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

        }
    }
}
