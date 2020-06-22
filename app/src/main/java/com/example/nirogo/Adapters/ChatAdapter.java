package com.example.nirogo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.Adapters.Messages.Doc;
import com.example.nirogo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter {

    private List<Doc> list;
    Context context;

    public ChatAdapter(List<Doc> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Doc itemAdapter = list.get(position);

        ((ChatAdapter.ViewHolder) holder).name.setText(itemAdapter.getUsername());
        Picasso.get().load(itemAdapter.getImageUrl()).into(((ChatAdapter.ViewHolder) holder).image);
        ((ChatAdapter.ViewHolder) holder).id.setText(itemAdapter.getId());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name, id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.username);
            id = itemView.findViewById(R.id.id);
        }
    }
}
