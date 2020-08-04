package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyLink extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_link);




        class MyTask extends AsyncTask<Void, Void, String>{
            final EditText linkEditText = findViewById(R.id.inputSpotifyLink);

            @Override
            protected String doInBackground(Void... voids) {
                final String url = linkEditText.getText().toString();

                try {
                    Connection.Response response = Jsoup.connect(url).execute();
                    Document doc = response.parse();
                    String docString = doc.toString();
                    Pattern p = Pattern.compile("https://open.spotify.com/track/[^\"]*");
                    Matcher m = p.matcher(docString);
                    while(m.find()){
                        String songUrl = m.group();

                        Connection.Response songResponse = Jsoup.connect(songUrl).execute();
                        Document songDoc = songResponse.parse();

                        p = Pattern.compile(".+?(?= - )");
                        m = p.matcher(songDoc.title());
                        Log.d("SONG", m.group());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    finish();
                }


                return  url;
            }
            protected void onPostExecute(String id){


            }
        }


        final Button downloadButton = findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute();
            }
        });

    }
}