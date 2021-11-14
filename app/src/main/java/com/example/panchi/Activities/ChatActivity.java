package com.example.panchi.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.example.panchi.Adapters.MessageAdapter;
import com.example.panchi.Models.Message;
import com.example.panchi.R;
import com.example.panchi.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private Uri fileUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private String checker;

    ActivityChatBinding binding;
    MessageAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom,receiverRoom,senderUid,receiverUid;

    FirebaseDatabase database;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //dialog to show uploading image
        dialog= new ProgressDialog(this);
        dialog.setMessage("Sending File...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();
        adapter= new MessageAdapter(this,messages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);


        //Getting name and user id
        String name = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        senderUid= FirebaseAuth.getInstance().getUid();

        senderRoom=senderUid + receiverUid;
        receiverRoom=receiverUid+senderUid;

        database = FirebaseDatabase.getInstance();



        //To get messages from database and display it on the screen
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String messageTxt = binding.msgBox.getText().toString();
                Date date= new Date();   //get current time


                Message message = new Message(messageTxt,senderUid,date.getTime());
                binding.msgBox.setText("");



                //First we have to update last message and then add messages to database
                String randomKey = database.getReference().push().getKey();

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lstMsg",message.getMessage());
                lastMsgObj.put("lstMsgTime", date.getTime());

                //updating last message and time in database
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                int position =binding.recyclerView.getAdapter().getItemCount()-1;
                if(position>0)
                binding.recyclerView.smoothScrollToPosition(position);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        //when our message has successfully transfered
                        //Do same for receiver
                        //shift recycler view focus on last element added
                       int position =binding.recyclerView.getAdapter().getItemCount()-1;
                        if(position>0)
                        binding.recyclerView.smoothScrollToPosition(position);

                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {

                            }
                        });
                       HashMap<String, Object> lastMsgObj = new HashMap<>();
                        lastMsgObj.put("lstMsg",message.getMessage());
                        lastMsgObj.put("lstMsgTime", date.getTime());

                        //updating last message and time in database
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                    }
                });
            }
        });

        //Creating AlertDialog for Sharing Files or images
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select Option");
                String[] options ={"Images","Files"};
               builder.setItems(options, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       switch (which) {
                           case 0: { Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent,40);
                                checker="photo";
                               } break;
                           case 1: //Toast.makeText(ChatActivity.this, "Files Selected",Toast.LENGTH_SHORT).show();
                                { Intent intent = new Intent();
                               intent.setAction(Intent.ACTION_GET_CONTENT);
                               intent.setType("application/pdf");
                               startActivityForResult(intent,40);
                               checker="pdf";
                                 } break;
                       }


                   }
               });

                AlertDialog dialog = builder.create();
                dialog.show();
                }
        });

        //Display name on actionBar(Top)
        Objects.requireNonNull(getSupportActionBar()).setTitle(name);


        //setDisplayHomeAsUpEnabled makes an icon clickable and add  a back button on it
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==40 && data!=null && data.getData()!=null){
            fileUri=data.getData();
            Calendar calendar = Calendar.getInstance();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chats").child(calendar.getTimeInMillis()+"");
            dialog.show();
            //uploading image/pdf file
            storageReference.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    dialog.dismiss();
                    if(task.isSuccessful())
                    {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                String filePath = uri.toString();
                                /////////////
                                if(checker.equals("photo"))
                                send_data(filePath,"photo");
                                else if(checker.equals("pdf"))
                                    send_data(filePath,"pdf");
                                ////////////
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }// if ends
                }
            });

        }//IF ENDS


    }

    private void send_data(String filePath,String msg){

        //String messageTxt = binding.msgBox.getText().toString();
        Date date= new Date();   //get current time

        //Message message = new Message(messageTxt,senderUid,date.getTime());
        Message message = new Message(msg,senderUid,date.getTime());
        //message.setMessage("photo");
        if(msg.equals("photo"))
        message.setImageUrl(filePath);
        else if(msg.equals("pdf"))
            message.setImageUrl(filePath);
        binding.msgBox.setText("");
        //First we have to update last message and then add messages to database
        String randomKey = database.getReference().push().getKey();

        HashMap<String, Object> lastMsgObj = new HashMap<>();
        lastMsgObj.put("lstMsg",message.getMessage());
        lastMsgObj.put("lstMsgTime", date.getTime());

        //updating last message and time in database
        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

        int position =binding.recyclerView.getAdapter().getItemCount()-1;
        if(position>0)
            binding.recyclerView.smoothScrollToPosition(position);

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .child(randomKey)
                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                //when our message has successfully transfered
                //Do same for receiver
                //shift recycler view focus on last element added
                int position =binding.recyclerView.getAdapter().getItemCount()-1;
                if(position>0)
                    binding.recyclerView.smoothScrollToPosition(position);
                database.getReference().child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {

                    }
                });
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lstMsg",message.getMessage());
                lastMsgObj.put("lstMsgTime", date.getTime());

                //updating last message and time in database
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

            }
        });

    }
}