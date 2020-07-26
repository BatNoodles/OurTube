package com.example.ourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button ytLinkBtn = findViewById(R.id.ytLinkButton);
        final Button ytSearchBtn = findViewById(R.id.ytSearchButton);

        ytLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeIntent = new Intent(MainActivity.this, YoutubeLink.class);
                startActivity(changeIntent);
            }
        });

        ytSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeIntent = new Intent(MainActivity.this, YoutubeSearch.class);
                startActivity(changeIntent);
            }
        });

    }
}