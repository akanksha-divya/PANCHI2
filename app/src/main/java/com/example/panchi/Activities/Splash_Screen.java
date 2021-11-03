package com.example.panchi.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.panchi.R;

import com.google.firebase.auth.FirebaseAuth;

public class Splash_Screen extends AppCompatActivity {


    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                auth=FirebaseAuth.getInstance();
                if(auth.getCurrentUser()!=null)
                {
                    Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                    //Intent intent = new Intent(Splash_Screen.this, PhoneNumberActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(Splash_Screen.this, PhoneNumberActivity.class);
                    //Intent intent = new Intent(Splash_Screen.this, PhoneNumberActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1000);

    }
}