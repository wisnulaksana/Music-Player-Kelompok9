package com.kelompok9.musicplayer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.kelompok9.musicplayer.R;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

//        ImageView imageView = findViewById(R.id.back);
//        Picasso.with(this)
//                .load(R.drawable.back_ground)
//                .into(imageView);

        new Handler().postDelayed(new Runnable() {


            @Override

            public void run() {

                Intent i = new Intent(SplashScreen.this, MainActivity.class);

                startActivity(i);

                // close this activity

                finish();

            }

        }, 3*1000); // wait for 5 seconds



    }
}
