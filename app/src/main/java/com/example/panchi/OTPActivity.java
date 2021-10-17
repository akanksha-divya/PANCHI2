package com.example.panchi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.mukesh.OnOtpCompletionListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;

    FirebaseAuth auth;

    String vId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        auth= FirebaseAuth.getInstance();

        String phoneNumber=getIntent().getStringExtra("phoneNumber");
        binding.NumLabel.setText("Verify "+phoneNumber);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS).setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull @NotNull String verifyId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        vId=verifyId;
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vId,otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(OTPActivity.this, "Logged In",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(OTPActivity.this, "Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
}