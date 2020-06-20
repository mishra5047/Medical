package com.example.nirogo.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UpdateProfileDoc extends Activity {
    private EditText name;
    private EditText age;
    private EditText speciality;
    private EditText phone, city;
    private Button submitupdate;
    private ImageView image;
    String docUrl;

    FirebaseAuth auth;
    DatabaseReference doctor_fetch, doc_update;
    String Database_Path_Fetch = "Doctor/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DocProfile.class));
            }
        });

        name= (EditText) findViewById(R.id.nameupdate);
        age= (EditText) findViewById(R.id.ageupdate);
        speciality= (EditText) findViewById(R.id.specialityupdate);
        phone= (EditText) findViewById(R.id.phoneupdate);
        image = findViewById(R.id.imageDoc);
        city = findViewById(R.id.cityupdate);

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
                        phone.setText(docUploadInfo.phone);
                        city.setText(docUploadInfo.city);
                        Picasso.get().load(docUploadInfo.imageURL).into(image);

                        docUrl = docUploadInfo.getImageURL();
                    }
                    }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        submitupdate=(Button)findViewById(R.id.submitupdate);

        submitupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namestring = name.getText().toString();
                String agestring= age.getText().toString();
                String specialitystring= speciality.getText().toString();
                String phonestring= phone.getText().toString();
                String cityString = city.getText().toString();
                    Log.i("Phone no.",phonestring);


                doc_update = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);

                DocUploadInfo docUploadInfo = new DocUploadInfo(auth.getCurrentUser().getUid(), phonestring, namestring, specialitystring, agestring, cityString, docUrl);
                doc_update.child(auth.getCurrentUser().getUid()).setValue(docUploadInfo);
                startActivity(new Intent(getApplicationContext(), DocProfile.class));
                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
