package com.example.nirogo.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.MyAppointAdapter;
import com.example.nirogo.MyAppointments;
import com.example.nirogo.NearbyDoctors.AppointmentAdapter;
import com.example.nirogo.NearbyDoctors.UploadInfo;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class showUserAppointments extends AppCompatActivity {

    private ArrayList<MyAppointments> list= new ArrayList<>();
    private RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth mAth;
    String userID;
    private MyAppointAdapter myAppointAdapter;
    String Database_Path = "UserApt/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_appointments);
        mAth = FirebaseAuth.getInstance();
        userID = mAth.getCurrentUser().getUid();

        recyclerView = (RecyclerView) findViewById(R.id.user_appointmentlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAppointAdapter = new MyAppointAdapter(list, this);
        myAppointAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(myAppointAdapter);

        Database_Path = Database_Path + userID + "/";

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MyAppointments userappt = postSnapshot.getValue(MyAppointments.class);
                    list.add(userappt);

                }
                myAppointAdapter = new MyAppointAdapter(list, getApplicationContext());
                recyclerView.setAdapter(myAppointAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}