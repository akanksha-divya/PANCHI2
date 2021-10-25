package com.example.panchi.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.panchi.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.panchi.R;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;

    //To show loading while the otp is being sent
    ProgressDialog dialog,d2;
    FirebaseAuth auth;

    String vId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP.....");
        dialog.setCancelable(false);
        dialog.show();

        d2 = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        binding.NumLabel.setText("Verify " + phoneNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS).setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull @NotNull String verifyId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();
                        vId = verifyId;
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);





        //for continue button
        binding.ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed = (EditText)findViewById(R.id.editotp);
                String otp = ed.getText().toString();
                d2.setMessage("Verifying OTP...");
                d2.setCancelable(false);
                d2.show();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vId,otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {



                        if(task.isSuccessful()){

                            //Toast.makeText(OTPActivity.this, "Logged In",Toast.LENGTH_SHORT).show();

                            Intent intent= new Intent(OTPActivity.this,ProfileActivity.class);
                            startActivity(intent);
                            // finishAffinity();
                        }
                        else
                        {

                            Toast.makeText(OTPActivity.this, "Failed",Toast.LENGTH_SHORT).show();
                        }
                        d2.dismiss();
                        finishAffinity();
                    }
                });
                Toast.makeText(OTPActivity.this, "Enter Correct OTP",Toast.LENGTH_SHORT).show();
            }
        });


    }
}


