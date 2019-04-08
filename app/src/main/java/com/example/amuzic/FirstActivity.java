package com.example.amuzic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initViews();
    }

    private void initViews() {
        button = findViewById(R.id.button2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                Intent intent = new Intent(this, Play_Spotify.class);
                startActivity(intent);
        }
    }
}
