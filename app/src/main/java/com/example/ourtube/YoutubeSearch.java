package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class YoutubeSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);

        final Button downloadButton = findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast myToast = Toast.makeText(YoutubeSearch.this,  "This does not work yet", Toast.LENGTH_SHORT);
                myToast.show();
                finish();
            }
        });
    }
}