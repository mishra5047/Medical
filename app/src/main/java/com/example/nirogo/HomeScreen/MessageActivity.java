package com.example.nirogo.HomeScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.nirogo.Adapters.Messages.ItemMessages;
import com.example.nirogo.Adapters.Messages.MessageAdapter;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    List<ItemMessages> messagesList = new ArrayList<>();
    MessageAdapter messageAdapter;

    DatabaseReference databaseReference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        String name = null;
        if (getIntent().hasExtra("docname"))
            name = getIntent().getStringExtra("docname");



    }
}
