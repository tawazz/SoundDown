package net.tawazz.sounddown;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.tawazz.sounddown.helpers.ApplicationData;

public class SplashScreen extends AppCompatActivity {

    private ApplicationData applicationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        applicationData = (ApplicationData) getApplication();

        // Instantiate the RequestQueue.
        RequestQueue queue = applicationData.getWebRequest().getRequestQueue();
        String url = "http://tawazz.net/fasttube/api/explore";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {

                            Intent intent = new Intent(SplashScreen.this, SearchActivity.class);
                            intent.setAction("ACTION_JSON");
                            intent.putExtra("Json", response);
                            startActivity(intent);
                            finish();
                        } else {
                            error("webservices error");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error(error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void error(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreen.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Something Went Wrong");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
