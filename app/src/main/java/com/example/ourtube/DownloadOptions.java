package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.widget.Toast;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class DownloadOptions extends AppCompatActivity {


    public void downloadVideo(String url){
        new YouTubeExtractor(DownloadOptions.this){

            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {

                if (ytFiles != null){
                    YtFile file = null;
                    for(int i=0, itag; i < ytFiles.size(); i++){
                        itag = ytFiles.keyAt(i);
                        YtFile tempFile =  ytFiles.get(itag);
                        System.out.println(tempFile.getFormat().getExt());
                        if (tempFile.getFormat().getExt().equals("m4a")){
                            file = tempFile;
                            System.out.println("file has been found");
                        }
                    }

                    if (file != null){
                        String downloadUrl = file.getUrl();
                        String title = videoMeta.getTitle();
                        String filename = title + "." + file.getFormat().getExt();
                        Toast myToast = Toast.makeText(DownloadOptions.this, "Downloading " + filename, Toast.LENGTH_LONG);
                        myToast.show();

                        Uri uri = Uri.parse(downloadUrl);

                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "OurTube/" + filename);

                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                        manager.enqueue(request);
                        finish();

                    }
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