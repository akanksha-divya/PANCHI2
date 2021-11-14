package com.example.panchi.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.panchi.Activities.ChatActivity;
import com.example.panchi.Activities.OTPActivity;
import com.example.panchi.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.panchi.Models.Message;
import com.example.panchi.databinding.ReceiverBinding;
import com.example.panchi.databinding.SenderBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.sql.Timestamp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        String uri = messages.get(position).getImageUrl();
        String uri2=message.getImageUrl();
        //position is used so that message that arrives first will be displayed first

        //Adding time

        //Here we dont know which viewHolder does it belong sender or receiver
        if(holder.getClass()==SentViewHolder.class)
        {
            SentViewHolder viewHolder = (SentViewHolder)holder;
            if(message.getMessage().equals("photo")){
                viewHolder.binding.Simage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .into(viewHolder.binding.Simage);

            }

            else if(message.getMessage().equals("pdf")){
                viewHolder.binding.Simage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.pdf_placeholder)
                        .into(viewHolder.binding.Simage);

                viewHolder.binding.Simage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri2));
                       // intent.setDataAndType(Uri.parse("https://firebasestorage.googleapis.com/v0/b/panchi-bf16c.appspot.com/o/chats%2F"+uri+"?alt=media&token=91bceaa5-c97b-42fc-b4d1-54bbdf9c524d"),"application/pdf");
                        intent.setDataAndType(Uri.parse(uri),"application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        viewHolder.itemView.getContext().startActivity(intent);

                        //Toast.makeText(context, uri,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            else
            {
                viewHolder.binding.message.setText(message.getMessage());
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.Simage.setVisibility(View.GONE);


            }

            long timestamp = message.getTimestamp();
            Timestamp ts = new Timestamp(timestamp);
            Date date=new Date(ts.getTime());
            //= DateFormat.getInstance().format(date)
            String dateToStr ;
            dateToStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);

            viewHolder.binding.cTime.setText(dateToStr);
        }
        else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
           if(message.getMessage().equals("photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.image_placeholder)
               .into(viewHolder.binding.image);
            }

           else
           {
               viewHolder.binding.message.setText(message.getMessage());
               viewHolder.binding.message.setVisibility(View.VISIBLE);
               viewHolder.binding.image.setVisibility(View.GONE);


           }

            //viewHolder.binding.message.setText(message.getMessage());
            long timestamp = message.getTimestamp();
            Timestamp ts = new Timestamp(timestamp);
            Date date=new Date(ts.getTime());
            String dateToStr;
            dateToStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);

            viewHolder.binding.cTime.setText(dateToStr);


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
