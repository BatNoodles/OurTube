package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;



public class SpotifyQueue extends AppCompatActivity {




    public void addButton(final String id, final Boolean lastButton) {
        new YouTubeExtractor(SpotifyQueue.this){


            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, final VideoMeta videoMeta) {
                System.out.println(id);
                final LinearLayout linearLayout = findViewById(R.id.buttonLinearLayout);
                final ProgressBar progressBar = findViewById(R.id.progressBar);
                final TextView loadingTextView = findViewById(R.id.loadingTextView);
                try{

                    for (int i = 0, itag; i < ytFiles.size(); i++){
                        itag = ytFiles.keyAt(i);
                        if (ytFiles.get(itag) == null){
                            Log.d("ERROR", "File is null");
                        }
                        if(ytFiles.get(itag).getFormat().getHeight() == -1 && !ytFiles.get(itag).getFormat().getExt().equals("webm")){
                            final YtFile file = ytFiles.get(itag);
                            final Button button = new Button(SpotifyQueue.this);
                            button.setVisibility(View.GONE);
                            button.setText(videoMeta.getTitle());
                            button.setBackgroundResource(R.drawable.red_button);
                            button.setTransformationMethod(null);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String filename = videoMeta.getTitle() + "."  + file.getFormat().getExt();
                                    final Uri uri = Uri.parse(file.getUrl());
                                    final DownloadManager.Request request = new DownloadManager.Request(uri);
                                    final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "OurTube/" + filename);

                                    manager.enqueue(request);

                                    Toast myToast = Toast.makeText(SpotifyQueue.this, "Downloading: " + filename, Toast.LENGTH_SHORT);
                                    myToast.show();

                                }
                            });

                            linearLayout.addView(button);

                        }
                    }

                    if (lastButton){

                        for (int i = 0; i < linearLayout.getChildCount(); i++){
                            linearLayout.getChildAt(i).setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                        loadingTextView.setVisibility(View.GONE);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Log.d("ERROR", id);
                }}
        }.extract(id, true, true);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_queue);
        class MyTask extends AsyncTask<String, Void, List<String>> {
            final EditText linkEditText = findViewById(R.id.inputSpotifyLink);
            VideoData data;
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
                                songIDs.add("https://youtube.com/watch?v=" + ytm.group().substring(10,21));
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
                        addButton(ids.get(i), i + 1 == ids.size()?true:false);
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
        final TextView loadingTextView = findViewById(R.id.loadingTextView);
        final String[] array = {"Loading...", "This might take a while", "Getting songs", "Finding urls", "Connecting to Youtube", "Thinking...", "Calculating...", "Generating download buttons"};
        final String[] altArray = {"It's better to cum in the sink than sink in the cum", "It ain't gay if the balls aren't touching", "Call your friends. They miss you", "So, you come here often", "Yar har", "I'm calling the police"};
        final Random generator = new Random();

        loadingTextView.post(new Runnable() {
            @Override
            public void run() {
                loadingTextView.setText(generator.nextInt(50) == 0?altArray[generator.nextInt(altArray.length)]:array[generator.nextInt(array.length)]);
                loadingTextView.postDelayed(this, 5000);
            }
        });
    }
}