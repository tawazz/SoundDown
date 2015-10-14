package net.tawazz.sounddown;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity {

    private TextView emptyText;
    private ListView songs;
    private View view;
    private String Json;
    private Track[] tracks;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = URLEncoder.encode(intent.getStringExtra(SearchManager.QUERY));
            searchSongs(query);
        }
        view = getWindow().getDecorView().findViewById(android.R.id.content);
        songs = (ListView) view.findViewById(R.id.listView_songs);
        emptyText = (TextView) view.findViewById(R.id.textView_empty);
        songs.setEmptyView(emptyText);
        tracks = null;

        if(tracks != null) {
            ListAdapter songsAdapter = new SongsAdapter(this, tracks);
            songs.setAdapter(songsAdapter);
        }
        songs.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            getSong(tracks[position]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    private void jsonToTracks() {
        if(!Json.isEmpty()) try {
            JSONObject jsonRootObject = new JSONObject(Json);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("tracks");

            tracks = new Track[jsonArray.length()];

            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.optString("title");
                String artworkUrl = jsonObject.optString("artwork_url").toString();
                String streamUrl = "http://tawazz.net/fasttube/download?title=" + URLEncoder.encode(title) + "&url=" + jsonObject.optString("stream_url").toString();
                String likes = jsonObject.optString("likes_count").toString();
                String time = jsonObject.optString("duration").toString();
                String user = jsonObject.getJSONObject("user").optString("username").toString();
                tracks[i] = new Track(user, title, artworkUrl, streamUrl, likes, time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void searchSongs(String query) {

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
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

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
        String filename = song.getTitle()+".mp3";
        String fileUrl = song.getStreamUrl();

        Toast.makeText(SearchActivity.this,"Downloading...",Toast.LENGTH_SHORT).show();
        new DownloadFile().execute(fileUrl, filename);

    }

    public void downloadManager(String filename, String fileUrl){

        Uri fileUri = Uri.parse(fileUrl);
        DownloadManager.Request r = new DownloadManager.Request(fileUri);

        // This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir("SoundDown",filename);

        // When downloading music and videos they will be listed in the player
        // (Seems to be available since Honeycomb only)
        r.allowScanningByMediaScanner();

        // Notify user when download is completed
        // (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Start download
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];

            downloadManager(fileName,fileUrl);
            return null;
        }

    }

}
