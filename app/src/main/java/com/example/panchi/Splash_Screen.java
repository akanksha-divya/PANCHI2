package com.example.panchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.panchi.databinding.ActivitySplashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Splash_Screen extends AppCompatActivity {


    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                auth=FirebaseAuth.getInstance();
                if(auth.getCurrentUser()!=null)
                {
                    Intent intent = new Intent(Splash_Screen.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(Splash_Screen.this, PhoneNumberActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);

    }
}