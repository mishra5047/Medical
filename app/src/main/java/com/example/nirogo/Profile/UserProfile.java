package com.example.nirogo.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.R;
import com.example.nirogo.User.UpdateProfileUser;
import com.example.nirogo.User.UserUploadInfo;
import com.example.nirogo.User.showUserAppointments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends Activity {

    LinearLayout pastAppointments;

    private ImageView update;
    CircleImageView profilePic;
    private TextView name;
    private TextView age;
    private TextView phoneno, city;
    FirebaseAuth auth;
    DatabaseReference user_fetch;
    String Database_Path_Fetch = "User/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        pastAppointments = (LinearLayout) findViewById(R.id.userprofileappointment);

        pastAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, showUserAppointments.class);
                startActivity(i);
            }
        });

        name = (TextView) findViewById(R.id.userprofilename);
        age = findViewById(R.id.userprofileage);
        phoneno = findViewById(R.id.phoneProfile);
        city = findViewById(R.id.cityProfile);
        profilePic = findViewById(R.id.userprofilepic);

        auth = FirebaseAuth.getInstance();
        user_fetch = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);
        user_fetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserUploadInfo user = postSnapshot.getValue(UserUploadInfo.class);
                    if ((user.id).equals(auth.getCurrentUser().getUid())) {
                        name.setText(user.name);
                        age.setText(user.age);
                        phoneno.setText(user.number);
                        city.setText(user.city);
                        Picasso.get().load(user.imageUrl).into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        update = findViewById(R.id.userupdate);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UpdateProfileUser.class));
            }
        });

    }

        @Override
    public void onBackPressed () {
        Intent intent = new Intent(UserProfile.this, HomeActivity.class);
        intent.putExtra("type", "User");
        startActivity(intent);
        Animatoo.animateFade(UserProfile.this);
    }
}

