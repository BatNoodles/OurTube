package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


        class MyTask extends AsyncTask<Void,Void,Boolean>{
            final EditText linkEditText = findViewById(R.id.inputSpotifyLink);
            @Override
            protected Boolean doInBackground(Void... voids) {
                Connection.Response response = null;
                try {
                    response = Jsoup.connect(linkEditText.getText().toString())
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .execute();
                    Document doc = response.parse();
                    String docString = doc.toString();
                    Pattern p = Pattern.compile("https://open.spotify.com/track/[^\"]*");
                    Matcher m = p.matcher(docString);
                    return m.find();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

            }

            protected void onPostExecute(Boolean valid){
                if (valid){
                    Intent spotifyIntent = new Intent(SpotifyLink.this, SpotifyQueue.class);
                    spotifyIntent.putExtra("url", linkEditText.getText().toString());
                    startActivity(spotifyIntent);
                }
                else{
                    Toast myToast = Toast.makeText(SpotifyLink.this, "Error: Not a valid link", Toast.LENGTH_SHORT);
                    myToast.show();
                }
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