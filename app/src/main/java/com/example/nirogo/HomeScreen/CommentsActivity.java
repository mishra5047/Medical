package com.example.nirogo.HomeScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirogo.Adapters.comments.commentAdapter;
import com.example.nirogo.Adapters.comments.commentItem;
import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.Post.PostUploadInfo;
import com.example.nirogo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private List<commentItem> list;
    private commentAdapter cAdaptor;
    RecyclerView recyclerView;
    private EditText editText;
    private ImageView send;
    private FirebaseAuth mAuth;
    Intent intent;
    Button back;
    String PostId;
    DatabaseReference dbref;
    String name;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        PostId = getIntent().getStringExtra("POST ID");
        list = new ArrayList<>();
        editText = (EditText) findViewById(R.id.addNewComment);
        send = (ImageView) findViewById(R.id.sendcomment);
        back= (Button) findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.recyclercomments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAuth= FirebaseAuth.getInstance();

        SharedPreferences pref = getSharedPreferences("UserType", 0);
        final String type = pref.getString("Type", "none");

        if (type.equals("User")) {
            uid = mAuth.getCurrentUser().getUid();
            dbref = FirebaseDatabase.getInstance().getReference("User/" + uid + "/name");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

           else  if (type.equals("Doctor")) {
                uid = mAuth.getCurrentUser().getUid();
                dbref = FirebaseDatabase.getInstance().getReference("Doctor/" + uid + "/name");
                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            dbref = FirebaseDatabase.getInstance().getReference("comments/" + PostId + "/");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                        commentItem commentInfo = commentSnapshot.getValue(commentItem.class);
                        list.add(commentInfo);
                    }

                    cAdaptor = new commentAdapter(list, getApplicationContext());
                    recyclerView.setAdapter(cAdaptor);
                    if (list.isEmpty()) {
                        TextView emptyTextview = (TextView) findViewById(R.id.noComments);
                        emptyTextview.setVisibility(View.VISIBLE);
                    } else {
                        TextView emptyTextview = (TextView) findViewById(R.id.noComments);
                        emptyTextview.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error in displaying comments", Toast.LENGTH_SHORT);

                }
            });

            //Add comment in database
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newComment = editText.getText().toString();
                    dbref = FirebaseDatabase.getInstance().getReference("comments/" + PostId + "/").push();
                    commentItem item = new commentItem(name, newComment);
                    dbref.setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            editText.setText("");
                        }
                    });
                }
            });


            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    new LinearLayoutManager(this).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i= new Intent(CommentsActivity.this,HomeActivity.class);
                    i.putExtra("type",type);
                    startActivity(i);
                }
            });
    }


}