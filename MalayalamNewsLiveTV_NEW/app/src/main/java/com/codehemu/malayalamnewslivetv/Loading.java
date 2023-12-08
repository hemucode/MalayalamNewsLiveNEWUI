package com.codehemu.malayalamnewslivetv;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codehemu.malayalamnewslivetv.models.Common;
import com.codehemu.malayalamnewslivetv.services.ShortDataAsync;



import java.util.Objects;


public class Loading extends AppCompatActivity {
    Handler handler;

    TextView textView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PackageManager manager = this.getPackageManager();
        textView = findViewById(R.id.textView);
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            String versionName = info.versionName;

            textView.setText("Version "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }


        handler = new Handler();
        handler.postDelayed(() -> {
            Loading.this.startActivity(new Intent(Loading.this, MainActivity.class));
            finish();
        },1500);

        if (Common.isConnectToInternet(Loading.this)) {
            new ShortDataAsync(Loading.this).execute();
        }
    }


}