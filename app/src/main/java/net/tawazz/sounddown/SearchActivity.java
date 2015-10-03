package net.tawazz.sounddown;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity {

    private Button searchBtn;
    private EditText searchField;
    private ListView songs;
    private View view;
    private String Json;
    private Track[] tracks;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        view = getWindow().getDecorView().findViewById(android.R.id.content);
        searchBtn = (Button) view.findViewById(R.id.button_search);
        searchField =(EditText) view.findViewById(R.id.editText_search);
        songs = (ListView) view.findViewById(R.id.listView_songs);
        tracks = null;

        if(tracks != null) {
            ListAdapter songsAdapter = new SongsAdapter(this, tracks);
            songs.setAdapter(songsAdapter);
        }
        songs.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String song = String.valueOf(parent.getItemAtPosition(position));
                        //Toast.makeText(SearchActivity.this, songsList[position].getDetails(), Toast.LENGTH_SHORT).show();
                        try {
                            getSong(tracks[position]);
                            //Toast.makeText(SearchActivity.this, "downloading", Toast.LENGTH_SHORT).show();
                            pDialog = new ProgressDialog(SearchActivity.this);
                            pDialog.setMessage("Downloading " + tracks[position].getDetails() + " ....");
                            pDialog.setCancelable(false);
                            pDialog.setCanceledOnTouchOutside(false);
                            pDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchSongs();
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchSongs();
                    return true;
                }
                return false;
            }
        });

    }

    private void jsonToTracks() {
        if(!Json.isEmpty()){
            try {
                JSONObject jsonRootObject = new JSONObject(Json);

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("tracks");

                tracks = new Track[jsonArray.length()];

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String title = jsonObject.optString("title").toString();
                    String artworkUrl = jsonObject.optString("artwork_url").toString();
                    String streamUrl = "http://tawazz.net/fasttube/download?title="+URLEncoder.encode(title)+"&url="+jsonObject.optString("stream_url").toString();

                    tracks[i] = new Track(title,artworkUrl,streamUrl);
                }
            } catch (JSONException e) {e.printStackTrace();}
        }

    }

    private void searchSongs() {
        String query = URLEncoder.encode(searchField.getText().toString());
        if(!query.isEmpty()) {
            Json = "";
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Searching ....");
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://tawazz.net/fasttube/api/search/" + query;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Json = response;
                            jsonToTracks();
                            if (tracks != null) {
                                ListAdapter songsAdapter = new SongsAdapter(getApplicationContext(), tracks);
                                songs.setAdapter(songsAdapter);
                                songs.invalidateViews();
                                pDialog.dismiss();
                            } else {
                                Toast.makeText(SearchActivity.this, "No results", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Json = "";
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
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
            pDialog.dismiss();
            return null;
        }
    }

}
