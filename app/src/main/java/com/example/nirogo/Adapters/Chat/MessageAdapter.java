package com.example.nirogo.Adapters.Chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.Adapters.Messages.Doc;
import com.example.nirogo.HomeScreen.MessageActivity;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private List<ChatMessages> list;
    Context context;
    String imageUrl;

    FirebaseUser user;

    public MessageAdapter(List<ChatMessages> list, Context context, String imageUrl) {
        this.list = list;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(v);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ChatMessages itemAdapter = list.get(position);

        ((MessageAdapter.ViewHolder) holder).name.setText(itemAdapter.getMessage());
        Picasso.get().load(imageUrl).into(((MessageAdapter.ViewHolder) holder).image);
        }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(user.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }
}
