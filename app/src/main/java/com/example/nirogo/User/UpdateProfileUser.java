package com.example.nirogo.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nirogo.Profile.DocProfile;
import com.example.nirogo.Profile.UserProfile;
import com.example.nirogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UpdateProfileUser extends AppCompatActivity {
    private EditText name;
    private EditText age;
    private EditText phone, city;
    private Button submitupdate;
    private ImageView image;
    String userUrl;

    FirebaseAuth auth;
    DatabaseReference user_fetch, user_update;
    String Database_Path_Fetch = "User/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_user);

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DocProfile.class));
            }
        });

        name= (EditText) findViewById(R.id.nameupdate);
        age= (EditText) findViewById(R.id.ageupdate);
        phone= (EditText) findViewById(R.id.phoneupdate);
        image = findViewById(R.id.imageDoc);
        city = findViewById(R.id.cityupdate);


        user_update = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);

        auth = FirebaseAuth.getInstance();
        user_fetch = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);
        user_fetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserUploadInfo userUploadInfo = postSnapshot.getValue(UserUploadInfo.class);
                    if ((userUploadInfo.id).equals(auth.getCurrentUser().getUid())){
                        name.setText(userUploadInfo.name);
                        age.setText(userUploadInfo.age);
                        phone.setText(userUploadInfo.number);
                        city.setText(userUploadInfo.city);
                        Picasso.get().load(userUploadInfo.imageUrl).into(image);

                        userUrl = userUploadInfo.getImageUrl();
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
                String phonestring= phone.getText().toString();
                String cityString = city.getText().toString();

               UserUploadInfo userUploadInfo = new UserUploadInfo(auth.getCurrentUser().getUid(), namestring, agestring, cityString, userUrl, phonestring);
                user_update.child(auth.getCurrentUser().getUid()).setValue(userUploadInfo) ;
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });




    }
}