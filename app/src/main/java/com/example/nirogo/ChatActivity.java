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
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirogo.Chat.ChatMessages;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

}

//
//    RecyclerView recyclerView;
//    FloatingActionButton send;
//    FirebaseListAdapter<ChatMessages> adapter;
//    RelativeLayout activity;
//
//    String user, doc;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        if (getIntent().hasExtra("userId") && getIntent().hasExtra("docId")){
//            user = getIntent().getStringExtra("userId");
//            doc = getIntent().getStringExtra("docId");
//        }
//        activity = findViewById(R.id.activity_chat);
//        recyclerView = findViewById(R.id.messagesRecycler);
//
//        displayChatMessages();
//        send = findViewById(R.id.sendBtn);
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText input = findViewById(R.id.input);
//                FirebaseDatabase.getInstance().getReference("Chat/").push().setValue(new ChatMessages(input.getText().toString(),
//                        user));
//                input.setText("");
//
//            }
//        });
//
//        }
//
//    private void displayChatMessages() {
//
//        Query query = FirebaseDatabase.getInstance().getReference("Chat/");
//
//        FirebaseListOptions<ChatMessages> options = new FirebaseListOptions.Builder<ChatMessages>()
//        .setQuery(query, ChatMessages.class).setLayout(R.layout.item_chat)
//                .build();
//
//        adapter = new FirebaseListAdapter<ChatMessages>(options) {
//            @Override
//            protected void populateView(@NonNull View v, @NonNull ChatMessages model, int position) {
//
//                TextView messageText, messageUser,messageTime;
//
//                messageText = v.findViewById(R.id.messageText);
//                messageUser = v.findViewById(R.id.message_user);
//                messageTime = v.findViewById(R.id.message_time);
//
//                messageText.setText(model.getMessageText());
//                messageUser.setText(model.getMessageUser());
//                messageTime.setText(DateFormat.format("dd-MM-YYYY (HH:mm:ss)", model.getMessageTime()));
//
//            }
//        };
//        recyclerView.setAdapter(adapter);
//        }
//    }
