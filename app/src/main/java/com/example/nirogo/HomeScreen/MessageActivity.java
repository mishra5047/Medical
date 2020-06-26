package com.example.nirogo.HomeScreen;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.Adapters.Chat.ChatMessages;
import com.example.nirogo.Adapters.Chat.MessageAdapter;
import com.example.nirogo.Adapters.Messages.Doc;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseUser user;

    CircleImageView image;
    TextView name;
    ImageButton send;
    EditText message;

    MessageAdapter messageAdapter;
    List<ChatMessages> chat;

    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    image = findViewById(R.id.imageDoc);
    name = findViewById(R.id.docName);
    send = findViewById(R.id.sendBtn);
    message = findViewById(R.id.txt_send);

    recyclerview = findViewById(R.id.recyclerview);

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
    linearLayoutManager.setStackFromEnd(true);
    recyclerview.setLayoutManager(linearLayoutManager);

    final String docid = getIntent().getStringExtra("docid");
    final String name_doc = getIntent().getStringExtra("docname");
    final String url =  getIntent().getStringExtra("url");
    user = FirebaseAuth.getInstance().getCurrentUser();

    send.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mess = message.getText().toString();
            if (!mess.isEmpty())
            {
                sendMessage(user.getUid(), docid, mess);
            }else
            {
                Toast.makeText(getApplicationContext(), "Message Can't Be Empty", Toast.LENGTH_SHORT).show();
            }
            message.setText("");
        }
    });
    databaseReference = FirebaseDatabase.getInstance().getReference("DocChat").child(docid);
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Doc doc = dataSnapshot.getValue(Doc.class);
            name.setText(name_doc);
            Picasso.get().load(url).into(image);

            readMessages(user.getUid(), docid, url);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        dbref.child("Chats").push().setValue(hashMap);
    }

    private void readMessages(final String myId, final String userId, final String imageUrl){

        chat = new ArrayList<>();



        databaseReference = FirebaseDatabase.getInstance().getReference("Chats/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatMessages chat_mes = snapshot.getValue(ChatMessages.class);
                    if (chat_mes.getReceiver().equals(myId) && chat_mes.getSender().equals(userId) ||
                        chat_mes.getReceiver().equals(userId) && chat_mes.getSender().equals(myId)){
                        chat.add(chat_mes);
                    }
                    messageAdapter = new MessageAdapter( chat, MessageActivity.this,imageUrl);
                    recyclerview.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
