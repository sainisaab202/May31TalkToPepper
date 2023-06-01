package com.pepperdialogflow.may31talktopepper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class OpenAiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_ai);
    }

    public void onHomeButtonClicked(View view) {
        this.finish();
    }
}