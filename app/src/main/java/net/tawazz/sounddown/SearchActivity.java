package net.tawazz.sounddown;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText searchField;
    private ListView songs;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        view = getWindow().getDecorView().findViewById(android.R.id.content);
        searchBtn = (Button) view.findViewById(R.id.button_search);
        searchField =(EditText) view.findViewById(R.id.editText_search);
        songs = (ListView) view.findViewById(R.id.listView_songs);

        final Track[] songsList = new Track[2];
        songsList[0] = new Track("Drama Feat. Drake",
                "https://i1.sndcdn.com/artworks-000124647570-nlo2r5-large.jpg",
                "http://tawazz.net/fasttube/download?title=Drama%20Feat.%20Drake&url=https://api.soundcloud.com/tracks/216772566/stream"
        );
        songsList[1] = new Track("Casey Veggies - Tied Up Ft. Dej Loaf",
                "",
                "http://tawazz.net/fasttube/download?title=Casey%20Veggies%20-%20Tied%20Up%20Ft.%20Dej%20Loaf&url=https://api.soundcloud.com/tracks/202972853/stream"
        );

        ListAdapter songsAdapter = new SongsAdapter(this,songsList);
        songs.setAdapter(songsAdapter);

        songs.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String song = String.valueOf(parent.getItemAtPosition(position));
                        //Toast.makeText(SearchActivity.this, songsList[position].getDetails(), Toast.LENGTH_SHORT).show();
                        try {
                            getSong(songsList[position]);
                            Toast.makeText(SearchActivity.this, "downloading", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    public void getSong(Track song){


        //new URL("http://tawazz.net/fasttube/download?title=Travis%20Scott%20-%20Antidote&url=https://api.soundcloud.com/tracks/211417319/stream").getContent();
        String filename = song.getDetails()+".mp3";
        String fileUrl = song.getStreamUrl();
        new DownloadFile().execute(fileUrl,filename);

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "SoundDown");
            folder.mkdir();

            File mp3 = new File(folder, fileName);

            try{
                mp3.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl,mp3);
            return null;
        }
    }

}
