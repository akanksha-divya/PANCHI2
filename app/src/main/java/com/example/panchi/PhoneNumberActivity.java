package com.example.panchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.panchi.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().hide();
        binding.phoneNo.requestFocus();

        binding.Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumberActivity.this,OTPActivity.class);

                String no = binding.phoneNo.getText().toString();
                if(no.length()==10){
                    no = "+91"+no;
                    intent.putExtra("phoneNumber",no);
                    startActivity(intent);
                }
                else if(no.length()==13){
                    no = no;
                    intent.putExtra("phoneNumber",no);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(PhoneNumberActivity.this, "Please Enter a valid Phone Number", Toast.LENGTH_LONG).show();
                }

            }

        });


    }

}