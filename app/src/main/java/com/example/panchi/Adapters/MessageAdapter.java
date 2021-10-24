package com.example.panchi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.panchi.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.panchi.Models.Message;
import com.example.panchi.databinding.ReceiverBinding;
import com.example.panchi.databinding.SenderBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;

    public MessageAdapter(Context context, ArrayList<Message> messages){
       this.context=context;
       this.messages=messages;

    }
    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sender,parent,false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver,parent,false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }
        else{
            return ITEM_RECEIVE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        //Here we dont know which viewHolder does it belong sender or receiver
        if(holder.getClass()==SentViewHolder.class)
        {
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.message.setText(message.getMessage());
        }
        else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            viewHolder.binding.message.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    //two viewholders for two sample layout send and receive

    //1
    public class SentViewHolder extends RecyclerView.ViewHolder{


        SenderBinding binding;

        public SentViewHolder(@NonNull View itemView) {

            super(itemView);
            binding=SenderBinding.bind(itemView);

        }
    }

    //2
    public  class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ReceiverBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {

            super(itemView);
            binding=ReceiverBinding.bind(itemView);
        }
    }
}
