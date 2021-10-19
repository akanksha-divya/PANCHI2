package com.example.panchi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import com.example.panchi.databinding.ActivityProfileBinding;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseStorage st;
    Uri selectedImage;

    ProgressDialog dialog; //To show creating profile


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Profile...");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        st=FirebaseStorage.getInstance();

        binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
           //startActivityForResult(intent,45);
        });

        binding.SetProfileBtn.setOnClickListener(v -> {
            String name = binding.name.getText().toString();



            //if no name is entered
            if(name.isEmpty()){
                binding.name.setError("Please Enter your name");
                return;
            }

            dialog.show(); // Progress dialog to show Updating profile

            //if image is selected
            if(selectedImage !=null){
                //This will create  a folder in firebase database
                StorageReference reference = st.getReference().child("Profiles").child(Objects.requireNonNull(auth.getUid()));
                reference.putFile(selectedImage).addOnCompleteListener(task -> {

                    //is image is successfully uploaded
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                String imgUrl= uri.toString(); //profile image
                                String uid = auth.getUid();//user id
                                String phone=auth.getCurrentUser().getPhoneNumber(); //phonenumber
                                String name1 = binding.name.getText().toString();
                                Users user = new Users(uid , name1, phone, imgUrl);


                                //adding data to firebase database
                                db.getReference().child("Users").child(uid).setValue(user)
                                        .addOnSuccessListener(unused -> {
                                            dialog.dismiss();
                                            Intent intent= new Intent(ProfileActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        });
                            }
                        });
                    }
                });
            }else{
                //if the user doesn,t select any image
                String uid = auth.getUid();//user id
                String phone= Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber(); //phonenumber
               // String name = binding.name.getText().toString();
                Users user = new Users(uid , name , phone, "No Image");

                //adding data to firebase database
                assert uid != null;
                db.getReference().child("Users").child(uid).setValue(user)
                        .addOnSuccessListener(unused -> {
                            dialog.dismiss();
                            Intent intent= new Intent(ProfileActivity.this,ChatActivity.class);
                            startActivity(intent);
                            finish();
                        });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            if(data.getData()!=null){
                binding.imageView.setImageURI(data.getData());
                selectedImage =data.getData();
            }
        }
    }
}