package com.example.panchi.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.panchi.Adapters.MessageAdapter;
import com.example.panchi.Models.Message;
import com.example.panchi.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessageAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom,receiverRoom;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter= new MessageAdapter(this,messages);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        //Getting name and user id
        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String senderUid= FirebaseAuth.getInstance().getUid();

        senderRoom=senderUid + receiverUid;
        receiverRoom=receiverUid+senderUid;

        database = FirebaseDatabase.getInstance();



        //To get messages from database and display it on thr screen
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


                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        //when our message has successfully transfered
                        //Do same for receiver
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
}