package com.example.panchi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.panchi.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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


        dialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        st=FirebaseStorage.getInstance();

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
               startActivityForResult(intent,45);
            }
        });

        binding.SetProfileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = binding.name.getText().toString();


                dialog.setMessage("Creating Profile...");
                dialog.setCancelable(false);dialog.show();


                //if no name is entered
                if(name.isEmpty()){
                    binding.name.setError("Please Enter your name");
                    return;
                }

                //if image is selected
                if(selectedImage !=null){
                    //This will create  a folder in firebase database
                    StorageReference reference = st.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            //is image is successfully uploaded
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                                    @Override
                                    public void onSuccess(@NonNull Uri uri) {
                                        String imgUrl= uri.toString(); //profile image
                                        String uid = auth.getUid();//user id
                                        String phone=auth.getCurrentUser().getPhoneNumber(); //phonenumber
                                        String name = binding.name.getText().toString();
                                        Users user = new Users(uid , name , phone, imgUrl);




                                        //adding data to firebase database
                                        db.getReference().child("Users").setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                    @Override
                                                    public void onSuccess(@NonNull Void unused) {
                                                        dialog.dismiss();
                                                        Intent intent= new Intent(ProfileActivity.this,MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
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