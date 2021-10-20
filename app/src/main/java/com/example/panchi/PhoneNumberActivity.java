package com.example.panchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.example.panchi.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null)
        {
            Intent intent = new Intent(PhoneNumberActivity.this,MainActivity.class);

            intent.putExtra("phoneNumber",binding.phoneNo.getText().toString());
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();
        binding.phoneNo.requestFocus();
        binding.Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumberActivity.this,OTPActivity.class);

                intent.putExtra("phoneNumber",binding.phoneNo.getText().toString());
                startActivity(intent);
            }

        });


    }

  }