package com.example.nirogo;

import android.text.format.DateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.Adapters.ChatAdapter;
import com.example.nirogo.Adapters.Messages.Doc;
import com.example.nirogo.Chat.ChatMessages;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    String docId, id;
    RecyclerView recyclerView;

    ChatAdapter adapter;
    List<Doc> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.messagesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth= FirebaseAuth.getInstance();

        if (getIntent().hasExtra("docId")){
            docId = getIntent().getStringExtra("docId");
            id = getIntent().getStringExtra("userId");
        }

        String path_user = "DocChat/" + docId + "/";
        reference = FirebaseDatabase.getInstance().getReference(path_user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Doc doc = snapshot.getValue(Doc.class);
                        list.add(doc);
                }
            adapter = new ChatAdapter(list, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}