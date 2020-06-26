package com.example.nirogo.Adapters.comments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.R;

import java.util.List;

public class commentAdapter extends RecyclerView.Adapter {
    Context context;
    private List<commentItem> list;

    public commentAdapter(List<commentItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("COMMENTS","Reached");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

        commentAdapter.ViewHolder viewHolder = new commentAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        final commentItem item = list.get(position);

        ((commentAdapter.ViewHolder) holder).name.setText(item.getUsername());

        ((commentAdapter.ViewHolder) holder).commenttext.setText(item.getusercomment());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView name, commenttext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment_username);
            commenttext= itemView.findViewById(R.id.comment_usercomment);

        }
    }
}
