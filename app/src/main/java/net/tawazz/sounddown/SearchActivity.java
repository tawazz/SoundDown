package net.tawazz.sounddown;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.joanzapata.iconify.widget.IconTextView;

import net.tawazz.sounddown.helpers.ApplicationData;
import net.tawazz.sounddown.helpers.WebRequest;
import net.tawazz.sounddown.mp3agic.ID3Wrapper;
import net.tawazz.sounddown.mp3agic.ID3v1Tag;
import net.tawazz.sounddown.mp3agic.ID3v23Tag;
import net.tawazz.sounddown.mp3agic.InvalidDataException;
import net.tawazz.sounddown.mp3agic.Mp3File;
import net.tawazz.sounddown.mp3agic.NotSupportedException;
import net.tawazz.sounddown.mp3agic.UnsupportedTagException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity implements SongsAdapter.AdapterListener {

    private ListView songs;
    private View view;
    private String Json;
    private Track[] tracks;
    ProgressDialog pDialog;
    private Context context;
    private ApplicationData appData;
    private final Handler handler = new Handler();
    private WebRequest request;
    private ActivityListener activityListener;
    private SongsAdapter songsAdapter;
    private FloatingActionButton playBtn,nextBtn,prevBtn;
    private IconTextView songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();

        view = getWindow().getDecorView().findViewById(android.R.id.content);
        songs = (ListView) view.findViewById(R.id.listView_songs);
        TextView emptyText = (TextView) view.findViewById(R.id.textView_empty);

        songName = (IconTextView) view.findViewById(R.id.song_name);
        playBtn = (FloatingActionButton) view.findViewById(R.id.play_btn);
        nextBtn = (FloatingActionButton) view.findViewById(R.id.next_btn);
        prevBtn = (FloatingActionButton) view.findViewById(R.id.prev_btn);
        songs.setEmptyView(emptyText);

        context = this;
        appData = (ApplicationData) getApplication();
        request = WebRequest.getInstance();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = URLEncoder.encode(intent.getStringExtra(SearchManager.QUERY));
            searchSongs(query);
        }else if(intent.getAction().equals("ACTION_JSON")){
            Json = intent.getStringExtra("Json");
            jsonToTracks();
            if (tracks != null) {
                songsAdapter = new SongsAdapter(context, tracks);
                songsAdapter.setAdapterListener(this);
                songs.setAdapter(songsAdapter);
                songs.invalidateViews();


            } else {
                Toast.makeText(SearchActivity.this, "No results", Toast.LENGTH_SHORT).show();
            }
        } else{
            this.explore();
        }

        if(tracks != null) {

            songsAdapter = new SongsAdapter(this, tracks);
            songsAdapter.setAdapterListener(this);
            songs.setAdapter(songsAdapter);
            activityListener = songsAdapter;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                songs.invalidateViews();
            }
        },1500);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = appData.mediaPlayer;
                if(mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    } else if(mediaPlayer.getTrackInfo().length > 0) {
                        mediaPlayer.start();
                        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                    }
                }

            }
        });

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
                String artworkUrl = jsonObject.optString("artwork_url");
                String streamUrl = "http://tawazz.net/fasttube/download?title=" + URLEncoder.encode(title) + "&url=" + jsonObject.optString("uri");
                String previewUrl = "http://tawazz.net/fasttube/download?title=" + URLEncoder.encode(title) + "&url=" + jsonObject.optString("uri");
                String likes = jsonObject.optString("likes_count");
                String time = jsonObject.optString("duration");
                String user = jsonObject.getJSONObject("user").optString("username");
                tracks[i] = new Track(user, title, artworkUrl, streamUrl, likes, time,previewUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void searchSongs(String query) {

        if(!query.isEmpty()) {
            tracks=null;
            Json = "";
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Searching ....");
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            String url = "http://tawazz.net/fasttube/api/search/" + query;

            // Request a string response from the provided URL.
            request(url);
        }
    }

    private void explore(){
        Json = "";
        tracks=null;
        pDialog = new ProgressDialog(SearchActivity.this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        String url = "http://tawazz.net/fasttube/api/explore";

        // Request a string response from the provided URL.
        request(url);

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
            Intent intent = new Intent(SearchActivity.this,SettingsActivity.class);
            startActivityForResult(intent, RESULT_OK);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getSong(Track song,int pos){
        String filename = song.getTitle()+".mp3";
        String fileUrl = song.getStreamUrl();
        String trackId = pos+"";

        Toast.makeText(SearchActivity.this,"Downloading "+filename+"...",Toast.LENGTH_SHORT).show();
        new DownloadFile().execute(fileUrl, filename, trackId);

    }
    public void downloadManager(final String filename, String fileUrl, String pos){

        Uri fileUri = Uri.parse(fileUrl);
       final DownloadManager.Request r = new DownloadManager.Request(fileUri);

        // This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir("SoundDown", filename);

        // Notify user when download is completed
        // (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Start download
        final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
        final String trackId = pos;
        BroadcastReceiver onComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                String filePath =Environment.getExternalStorageDirectory()+"/SoundDown/";
                try {
                    Mp3File mp3File = new Mp3File(filePath+filename);
                    ID3Wrapper newId3Wrapper = new ID3Wrapper(new ID3v1Tag(), new ID3v23Tag());
                    newId3Wrapper.setTitle(filename);
                    newId3Wrapper.setAlbumImage(getImage(trackId), "image/png");
                    newId3Wrapper.setArtist(tracks[Integer.parseInt(trackId)].getUser());
                    newId3Wrapper.setAlbum("SoundDown");
                    newId3Wrapper.getId3v2Tag().setPadding(true);
                    mp3File.removeId3v1Tag();
                    mp3File.removeId3v2Tag();
                    mp3File.setId3v1Tag(newId3Wrapper.getId3v1Tag());
                    mp3File.setId3v2Tag(newId3Wrapper.getId3v2Tag());
                    mp3File.save(filePath+"SD"+filename);
                    File originalFile = new File(filePath+filename);
                    File backup =  new File(filePath+filename+".bak");
                    File retaggedFile = new File(filePath+"SD"+filename);
                    originalFile.renameTo(backup);
                    retaggedFile.renameTo(originalFile);
                    backup.delete();
                    // When downloading music and videos they will be listed in the player
                    // (Seems to be available since Honeycomb only)
                    r.allowScanningByMediaScanner();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnsupportedTagException e) {
                    e.printStackTrace();
                } catch (InvalidDataException e) {
                    e.printStackTrace();
                } catch (NotSupportedException e) {
                    e.printStackTrace();
                }

            }
        };
        registerReceiver(onComplete, new IntentFilter(dm.ACTION_DOWNLOAD_COMPLETE));
    }

    private byte[] getImage(String trackId) {
        Bitmap bmp = tracks[Integer.parseInt(trackId)].getArtwork();
        byte[] bytes;
        if(bmp!= null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytes = stream.toByteArray();
        }else
        {
            bmp = BitmapFactory.decodeResource(getResources(),R.drawable.music);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytes = stream.toByteArray();
        }

        return bytes;
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String pos = strings[2];

            downloadManager(fileName,fileUrl,pos);
            return null;
        }

    }


    public void request(String url){
        // Instantiate the RequestQueue.
        RequestQueue queue = request.getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Json = response;
                        jsonToTracks();
                        if (tracks != null) {
                            songsAdapter = new SongsAdapter(context, tracks);
                            songsAdapter.setAdapterListener(SearchActivity.this);
                            activityListener = songsAdapter;
                            songs.setAdapter(songsAdapter);
                            songs.invalidateViews();

                        } else {
                            Toast.makeText(SearchActivity.this, "No results", Toast.LENGTH_SHORT).show();
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Json = "";
            }
        });
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(request.retryPolicy());
        queue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(activityListener != null) {
            activityListener.destroy();
        }
    }

    @Override
    public void playPause(final Track song, int pos) {
        MediaPlayer mediaPlayer = appData.mediaPlayer;
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.setDataSource(song.getStreamUrl()); // URL to mediaplayer data source
                    mediaPlayer.prepareAsync(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                    songName.setText(R.string.buffering);
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(song.getStreamUrl()); // URL to mediaplayer data source
                    mediaPlayer.prepareAsync(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                    songName.setText(R.string.buffering);
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            mediaPlayer = new MediaPlayer();
        }


        final MediaPlayer finalMediaPlayer = mediaPlayer;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finalMediaPlayer.reset();
                songName.setText("");
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                finalMediaPlayer.start();
                songName.setText(song.getTitle());
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            }
        });
    }
}
