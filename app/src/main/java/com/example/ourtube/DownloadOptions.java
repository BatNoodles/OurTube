package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class DownloadOptions extends AppCompatActivity {

    public void addButton(final YtFile file, final String title){
        String btnText = file.getFormat().getExt() + " ";
        Button btn = new Button(this);
        btnText += (file.getFormat().getHeight() == -1) ? file.getFormat().getAudioBitrate() + "kb/s" : file.getFormat().getHeight() + "p";
        btn.setText(btnText);

        final LinearLayout recyclerView = findViewById(R.id.buttonLinearLayout);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                String downloadUrl = file.getUrl();
                String filename = title + "." + file.getFormat().getExt();

                Toast myToast = Toast.makeText(DownloadOptions.this, "Downloading " + filename, Toast.LENGTH_SHORT);
                myToast.show();

                Uri uri = Uri.parse(downloadUrl);

                DownloadManager.Request request = new DownloadManager.Request(uri);

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "OurTube/" + filename);

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                manager.enqueue(request);
            }
        });

        recyclerView.addView(btn);
    }

    public void downloadVideo(String url){
        new YouTubeExtractor(DownloadOptions.this){

            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {

                if (ytFiles != null){
                    YtFile file = null;
                    List<Integer> videoFormats = new ArrayList<>();
                    List<Integer> videoItags = new ArrayList<>();
                    List<Integer> audioFormats = new ArrayList<>();
                    List<Integer> audioItags = new ArrayList<>();

                    final TextView titleText = findViewById(R.id.videoTitleView);
                    final ProgressBar progressBar = findViewById(R.id.progressBar);
                    for(int i=0, itag; i < ytFiles.size(); i++){
                        itag = ytFiles.keyAt(i);
                        YtFile tempFile =  ytFiles.get(itag);
                        if (tempFile.getFormat().getHeight() != -1){
                            if (!videoFormats.contains(tempFile.getFormat().getHeight())){
                                videoFormats.add(tempFile.getFormat().getHeight());
                                videoItags.add(itag);
                            }
                        }
                        else{
                            if (!audioFormats.contains(tempFile.getFormat().getHeight()) && !tempFile.getFormat().getExt().equals("webm")){
                                audioFormats.add(tempFile.getFormat().getHeight());
                                audioItags.add(itag);

                            }
                        }

                    }
                    for (int i = 0; i < videoItags.size(); i++){
                        addButton(ytFiles.get(videoItags.get(i)), videoMeta.getTitle());
                    }
                    for (int i = 0; i < audioItags.size(); i++){
                        addButton((ytFiles.get(audioItags.get(i))), videoMeta.getTitle());
                    }
                    titleText.setText("File formats for: " + videoMeta.getTitle());
                    progressBar.setVisibility(View.GONE);

                }
                else{
                    Toast myToast = Toast.makeText(DownloadOptions.this, "That link is invalid", Toast.LENGTH_SHORT);
                    myToast.show();
                    finish();
                }

            }
        }.extract(url, true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_options);

        Intent intent = getIntent();

        if (intent == null){
            Toast myToast = Toast.makeText(this,"What the actual fuck have you done?",Toast.LENGTH_SHORT);
            myToast.show();
        }
        else{
            final String url = intent.getStringExtra("videoUrl");
            downloadVideo(url);


        }

    }
}