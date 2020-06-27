package com.example.nirogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.nirogo.Activities.AmbulanceActivity;
import com.example.nirogo.Adapters.Chat.ChatAdapter;
import com.example.nirogo.Adapters.Messages.Doc;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference reference, reference2;
    String docId, id;
    RecyclerView recyclerView;

    ChatAdapter adapter;
    List<Doc> list;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.messageActRec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth= FirebaseAuth.getInstance();

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChatActivity.this, HomeActivity.class);
                intent.putExtra("type",getIntent().getStringExtra("type"));
                startActivity(intent);
                Animatoo.animateFade(ChatActivity.this);
            }
        });

        TextView txtInto = findViewById(R.id.textIntro);
        type = getIntent().getStringExtra("type");


        if (getIntent().hasExtra("docId")){
            docId = getIntent().getStringExtra("docId");
            id = getIntent().getStringExtra("userId");
        }
        if (getIntent().hasExtra("type")) {
            if (getIntent().getStringExtra("type").equalsIgnoreCase("User")) {

                txtInto.setText("Your Appointments");
                String path_user = "UserChat/" + firebaseAuth.getCurrentUser().getUid() + "/";
                reference = FirebaseDatabase.getInstance().getReference(path_user);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
            } else if (type.equalsIgnoreCase("doctor")&&(getIntent().hasExtra("docId"))) {

                txtInto.setText("The List Of Users That Have Booked Appointment with You");
                String path_user_new = "ChatDoc/" + firebaseAuth.getCurrentUser().getUid().toString() + "/";
                reference2 = FirebaseDatabase.getInstance().getReference(path_user_new);
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
        else
            Toast.makeText(getApplicationContext(), "Not Found", Toast.LENGTH_SHORT).show();
    }

}
