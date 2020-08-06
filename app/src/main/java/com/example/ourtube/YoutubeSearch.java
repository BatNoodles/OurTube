package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class YoutubeSearch extends AppCompatActivity {
    class MyTask extends AsyncTask<Void, Void, String>{
        final EditText searchText = findViewById(R.id.inputNameTextEdit);
        final Button downloadButton = findViewById(R.id.downloadButton);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        @Override
        protected String doInBackground(Void... voids) {
            String id = null;
            if (!searchText.getText().toString().equals(null)){

                try {
                    Connection.Response response = Jsoup.
                            connect("https://www.youtube.com/results").
                            data("search_query", searchText.getText().toString())
                            .execute();
                    Document doc = response.parse();
                    String rx = "videoId\":\".*?\"";
                    Pattern p = Pattern.compile(rx);
                    Matcher m = p.matcher(doc.toString());
                    if(m.find()){
                        id = m.group().substring(10,21);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return id;
        }
        @Override
        protected  void onPostExecute(String id){


            if (searchText.getText().toString().equals(null)){
                Toast myToast = Toast.makeText(YoutubeSearch.this, "Please enter a search term", Toast.LENGTH_SHORT);
                myToast.show();
            }
            else if(id == (null)){
                Toast myToast = Toast.makeText(YoutubeSearch.this, "A video with that name could not be found", Toast.LENGTH_SHORT);
                myToast.show();
            }
            else{
                Intent downloadIntent = new Intent(YoutubeSearch.this, DownloadOptions.class);
                downloadIntent.putExtra("videoUrl", ("https://youtu.be/" + id));

                startActivity(downloadIntent);
                finish();

            }
            downloadButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);

        final Button downloadButton = findViewById(R.id.downloadButton);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final EditText linkText = findViewById(R.id.inputNameTextEdit);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!linkText.getText().toString().equals("")){
                    downloadButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    new MyTask().execute();
                }


            }
        });
    }



}