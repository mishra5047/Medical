package com.example.nirogo.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DocProfile extends Activity {

    private ImageView update;
    CircleImageView profilePic;
    private TextView name;
    private TextView age;
    private TextView speciality;
    private TextView phoneno, city;

    FirebaseAuth auth;
    DatabaseReference doctor_fetch;
    String docUrl;
    String Database_Path_Fetch = "Doctor/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name=(TextView) findViewById(R.id.nameProfile);
        age = findViewById(R.id.age);
        speciality = findViewById(R.id.speciaility);
        phoneno = findViewById(R.id.phoneProfile);
        city = findViewById(R.id.cityProfile);
        profilePic = findViewById(R.id.ProfilePicDoc);

        auth = FirebaseAuth.getInstance();
        doctor_fetch = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);
        doctor_fetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DocUploadInfo docUploadInfo = postSnapshot.getValue(DocUploadInfo.class);
                    if ((docUploadInfo.id).equals(auth.getCurrentUser().getUid())){
                        name.setText(docUploadInfo.name);
                        age.setText(docUploadInfo.age);
                        speciality.setText(docUploadInfo.speciality);
                        phoneno.setText(docUploadInfo.phone);
                        city.setText(docUploadInfo.city);
                        Picasso.get().load(docUploadInfo.imageURL).into(profilePic);

                    }
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        update= (ImageView) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(DocProfile.this, UpdateProfileDoc.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(DocProfile.this, HomeActivity.class);
        intent.putExtra("type","Doctor");
        startActivity(intent);
        Animatoo.animateFade(DocProfile.this);
    }
}

