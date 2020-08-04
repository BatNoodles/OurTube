package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class SpotifyQueue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_queue);
        class MyTask extends AsyncTask<String, Void, List<String>> {
            final EditText linkEditText = findViewById(R.id.inputSpotifyLink);

            @Override
            protected List<String> doInBackground(String ...params) {
                final String url = params[0];
                List<String> songIDs = new ArrayList<String>();
                try {
                    Connection.Response response = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .execute();
                    Document doc = response.parse();
                    String docString = doc.toString();
                    Pattern p = Pattern.compile("https://open.spotify.com/track/[^\"]*");
                    Matcher m = p.matcher(docString);
                    while(m.find()){
                        String songUrl = m.group();
                        Connection.Response songResponse = Jsoup.connect(songUrl)
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .referrer("http://www.google.com")
                                .execute();
                        Document songDoc = songResponse.parse();

                        p = Pattern.compile(".+?(?=, a)");
                        Matcher songm = p.matcher(songDoc.title());
                        if (songm.find()){
                            String songName = songm.group();

                            Connection.Response ytresponse = Jsoup.
                                    connect("https://www.youtube.com/results").
                                    data("search_query", songName)
                                    .execute();
                            Document ytdoc = ytresponse.parse();
                            String rx = "videoId\":\".*?\"";
                            Pattern ytp = Pattern.compile(rx);
                            Matcher ytm = ytp.matcher(ytdoc.toString());
                            if(ytm.find()){
                                songIDs.add(ytm.group().substring(10,21));
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    finish();
                }

               return songIDs;
            }
            protected void onPostExecute(List<String> ids){
                for (int i = 0; i < ids.toArray(new String[0]).length; i++){
                    Log.d("SONG", ids.get(i));
                }
            }
        }

        Intent intent =  getIntent();
        if (intent == null){
            Toast myToast = Toast.makeText(this,"What the actual fuck have you done?",Toast.LENGTH_SHORT);
            myToast.show();
        }
        else{
            String url = intent.getStringExtra("url");
            new MyTask().execute(url);
        }
    }
}