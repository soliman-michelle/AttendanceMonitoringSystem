package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;
    Animation topAnim, botAnim;
    ImageView Image;
    TextView logo, slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.bot);

        Image = findViewById(R.id.image);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);

        Image.setAnimation(topAnim);
        logo.setAnimation(botAnim);
        slogan.setAnimation(botAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splashScreen.this, welcomeScreen.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}