package com.example.nirogo.Adapters.Feed;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.HomeScreen.CommentsActivity;
import com.example.nirogo.ImageOpener;
import com.example.nirogo.Profile.DoctorProfileViewOnly;
import com.example.nirogo.Activities.AppointmentOption;
import com.example.nirogo.Post.PostUploadInfo;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PostUploadInfo> list;
    Context context;

    public FeedAdapter(List<PostUploadInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PostUploadInfo itemAdapter = list.get(position);

        Picasso.get().load(itemAdapter.getDocImage()).into(((ViewHolder) holder).docImage);
        ((ViewHolder) holder).nameUser.setText(itemAdapter.getDocName());
        ((ViewHolder) holder).descUser.setText(itemAdapter.getDocSpec());
        ((ViewHolder) holder).descPost.setText(itemAdapter.getDesc());
        ((ViewHolder) holder).timePost.setText(itemAdapter.getTime());
        ((ViewHolder) holder).phone.setText(itemAdapter.getNumberDoc());
        ((ViewHolder) holder).DocId.setText(itemAdapter.getDocId());
        ((ViewHolder) holder).PostDBid.setText(itemAdapter.getPostDBid());

        Picasso.get().load(itemAdapter.getUrl()).into(((ViewHolder) holder).imgPost);
        ((ViewHolder) holder).url.setText(itemAdapter.getUrl());


        //calling method to display if post is already liked
        initialLikeStatus(((ViewHolder) holder).btnLike,((ViewHolder) holder).txtLike,((ViewHolder) holder).PostDBid.getText().toString());

        //calling method to display no. of likes
        numberoflikes(((ViewHolder) holder).numLikes,((ViewHolder) holder).PostDBid.getText().toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameUser, descUser, descPost, timePost, DocId, PostDBid;
        ImageView docImage, imgPost;
        TextView phone, url;

        LinearLayout likelay, appoint, share;
        ImageView btnLike;
        TextView txtLike, numLikes;
        LinearLayout Commenticon;

        public ViewHolder(final View itemView) {
            super(itemView);

            phone = itemView.findViewById(R.id.noDoc);
            nameUser = itemView.findViewById(R.id.nameUser);
            descUser = itemView.findViewById(R.id.positionUser);
            descPost = itemView.findViewById(R.id.descPost);
            timePost = itemView.findViewById(R.id.timePost);
            docImage = itemView.findViewById(R.id.imageUser);
            imgPost = itemView.findViewById(R.id.imagePost);
            DocId = itemView.findViewById(R.id.IdDoc);
            PostDBid = itemView.findViewById(R.id.postDBid);
            url = itemView.findViewById(R.id.urlImage);

            likelay = itemView.findViewById(R.id.likeLayout);
            btnLike = itemView.findViewById(R.id.btnLike);
            txtLike = itemView.findViewById(R.id.likeTxt);
            numLikes = itemView.findViewById(R.id.noLikes);
            Commenticon=itemView.findViewById(R.id.CommentLayout);

            share = itemView.findViewById(R.id.shareLayout);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Nirogo");
                    intent.putExtra(Intent.EXTRA_TEXT, "Post by " + nameUser.getText() + " on Nirogo");
                    v.getContext().startActivity(Intent.createChooser(intent, "Share Using"));
                }
            });

            appoint = itemView.findViewById(R.id.shareAppointment);

            appoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AppointmentOption.class);
                    intent.putExtra("docname", nameUser.getText());
                    intent.putExtra("phone", phone.getText());
                    intent.putExtra("DocId", DocId.getText());
                    v.getContext().startActivity(intent);
                }
            });

            final Context context = itemView.getContext();


            likelay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtLike.getCurrentTextColor() == context.getResources().getColor(R.color.Black)) {
                        FirebaseDatabase.getInstance().getReference("likes/"+ PostDBid.getText().toString()+"/").
                                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true");
                    } else {
                        FirebaseDatabase.getInstance().getReference("likes/"+ PostDBid.getText().toString()+"/").
                                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }

                }
            });

            nameUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Opening User Profile", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(v.getContext(), DoctorProfileViewOnly.class);
                    intent.putExtra("docname", nameUser.getText());
                    v.getContext().startActivity(intent);
                }
            });

            imgPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImageOpener.class);
                    intent.putExtra("imgurl", url.getText());
                    v.getContext().startActivity(intent);

                }
            });

            Commenticon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CommentsActivity.class);
                    intent.putExtra("POST ID",PostDBid.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });


        }


    }

    public void initialLikeStatus(final ImageView btnlike, final TextView txtLike, String postDbid) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String useruid = mAuth.getCurrentUser().getUid();

        String dbPath = "likes/" + postDbid + "/";

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(dbPath);

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(useruid).exists()) {

                    btnlike.setImageResource(R.drawable.like_blue);
                    txtLike.setTextColor(context.getResources().getColor(R.color.blue_like));
                } else {
                    txtLike.setTextColor(context.getResources().getColor(R.color.Black));
                    btnlike.setImageResource(R.drawable.like_thumb);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Error in generating initial like status",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void numberoflikes(final TextView txtlike, String postDbId){
        String dbPath = "likes/" + postDbId + "/";

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(dbPath);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtlike.setText(dataSnapshot.getChildrenCount()+" ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Error in getting no. of likes",Toast.LENGTH_SHORT).show();
            }
        });
    }
}




