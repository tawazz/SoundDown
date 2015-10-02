package net.tawazz.sounddown;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.InputStream;
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

        String[] songsList = {"dont wann do there","candy shop", "3500","back to back","love me","problems","good for you"};
        ListAdapter songsAdapter = new SongsAdapter(this,songsList);
        songs.setAdapter(songsAdapter);

        songs.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String song = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(SearchActivity.this, song, Toast.LENGTH_SHORT).show();
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


}
