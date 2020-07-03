package com.example.nirogo.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.nirogo.Adapters.Feed.FeedAdapter;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.NearbyDoctors.AppointmentAdapter;
import com.example.nirogo.NearbyDoctors.UploadInfo;
import com.example.nirogo.Post.PostUploadActivity;
import com.example.nirogo.R;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppointmentsActivity extends Activity {

    List<UploadInfo> list = new ArrayList<>();

    AppointmentAdapter postAdapter;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String Database_Path = "DocNearby/";
    EditText cityEnter;
    ImageView searchCity;
    ImageButton location;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    Double lattitude, longitude;
    String city_user;
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        recyclerview = findViewById(R.id.recAppointment);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new AppointmentAdapter(list, this);
        postAdapter.notifyDataSetChanged();
        recyclerview.setAdapter(postAdapter);

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        cityEnter = findViewById(R.id.cityEnter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        searchCity = findViewById(R.id.searchIcon);
        searchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String city = cityEnter.getText().toString();
                    list.clear();
                    fetchFromDB(city);
            }
        });

        location = findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocation();
            }
        });
        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AppointmentsActivity.this, HomeActivity.class);
                intent.putExtra("type",getIntent().getStringExtra("type"));
                startActivity(intent);
                Animatoo.animateFade(AppointmentsActivity.this);
            }
        });

        final BubbleNavigationConstraintView bubblenavigation = findViewById(R.id.bottomNavApp);
        bubblenavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                if (position == 0) {
                    Intent intent= new Intent(AppointmentsActivity.this, HomeActivity.class);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    startActivity(intent);
                    Animatoo.animateFade(AppointmentsActivity.this);
                }
                else if (position == 2) {
                    if(getIntent().getStringExtra("type").equals("Doctor")) {
                        Intent intent = new Intent(AppointmentsActivity.this, PostUploadActivity.class);
                        intent.putExtra("type", getIntent().getStringExtra("type"));
                        startActivity(intent);
                        Animatoo.animateFade(AppointmentsActivity.this);
                    }
                    else {
                        Toast.makeText(AppointmentsActivity.this,"User cannot upload post",Toast.LENGTH_LONG).show();
                    }
                }
                else if (position == 3) {
                    Intent intent= new Intent(AppointmentsActivity.this, AmbulanceActivity.class);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    startActivity(intent);
                    Animatoo.animateFade(AppointmentsActivity.this);

                }

                else if (position == 4) {
                    Intent intent= new Intent(AppointmentsActivity.this, CartActivity.class);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    startActivity(intent);
                    Animatoo.animateFade(AppointmentsActivity.this);

                }

            }});
    }

    private void fetchFromDB(final String city) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadInfo docNearby = postSnapshot.getValue(UploadInfo.class);
                    String userCity = docNearby.getCity();

                    if (userCity.equalsIgnoreCase(city)){
                        list.add(docNearby);
                    }

                }
                postAdapter = new AppointmentAdapter(list, getApplicationContext());
                recyclerview.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AppointmentsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(AppointmentsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(AppointmentsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                lattitude = location.getLatitude();
                                longitude = location.getLongitude();

                                Toast.makeText(getApplicationContext(), lattitude.toString() + longitude.toString(), Toast.LENGTH_LONG).show();

                                Geocoder geocoder;
                                geocoder = new Geocoder(AppointmentsActivity.this, Locale.getDefault());
                                List<android.location.Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(lattitude, longitude, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (addresses != null) {

                                    city_user = addresses.get(0).getLocality();
                                    Toast.makeText(getApplicationContext(), "Your City Is " + city_user.trim(), Toast.LENGTH_LONG).show();
                                    list.clear();
                                    fetchFromDB(city_user);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Not Detected", Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(AppointmentsActivity.this, HomeActivity.class);

        intent.putExtra("type",getIntent().getStringExtra("type"));
        startActivity(intent);
        Animatoo.animateFade(AppointmentsActivity.this);
    }
}
