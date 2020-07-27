package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class YoutubeLink extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_link);

        final Button downloadButton = findViewById(R.id.downloadButton);
        final EditText linkEditText = findViewById(R.id.inputLinkTextEdit);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editContent = linkEditText.getText().toString();
                if (editContent.equals("") != true){
                    Intent downloadIntent = new Intent(YoutubeLink.this, DownloadOptions.class);
                    downloadIntent.putExtra("videoUrl", editContent);
                    startActivity(downloadIntent);
                    finish();
                }


            }
        });

    }
}