package com.example.nirogo.HomeScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.nirogo.Activities.AmbulanceActivity;
import com.example.nirogo.Activities.AppointmentsActivity;
import com.example.nirogo.Activities.CartActivity;
import com.example.nirogo.Activities.OptionActivity;
import com.example.nirogo.ChatActivity;
import com.example.nirogo.Profile.DocProfile;
import com.example.nirogo.Adapters.Feed.FeedAdapter;
import com.example.nirogo.Post.PostUploadActivity;
import com.example.nirogo.Post.PostUploadInfo;
import com.example.nirogo.Profile.UserProfile;
import com.example.nirogo.R;
import com.example.nirogo.ScreenSize;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    List<PostUploadInfo> list = new ArrayList<>();
    FeedAdapter postAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView recyclerview;
    SwipeRefreshLayout swipeRefreshLayout;
    ScrollView scrollView;
    BubbleToggleView bubbleToggleView;
    //db
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String Database_Path = "Post/";
    EditText search;
    ImageView searchImg;

    // @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(HomeActivity.this, OptionActivity.class);
//        signOut();
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenSize screenSize = new ScreenSize();
        String size = screenSize.screenCheck(HomeActivity.this);
        if (size.equalsIgnoreCase("Small")) {
            setContentView(R.layout.activity_home_small);
            Log.i("Screen Return Value", "Small");
        } else
            setContentView(R.layout.activity_home);

        SharedPreferences pref= getSharedPreferences("UserType",0);
        SharedPreferences.Editor mEditor= pref.edit();
        if(!pref.contains("Type")) {
            //Log.i("TAG",getIntent().getStringExtra("type"));
            mEditor.putString("Type", getIntent().getStringExtra("type"));
            mEditor.commit();
        }
        Log.i("TYPE SHARED",pref.getString("Type","NONE"));

        scrollView=(ScrollView)findViewById(R.id.ScrollviewHome);
        bubbleToggleView=(BubbleToggleView) findViewById(R.id.c_item_menu);
        bubbleToggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference();

        //setting up navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

        //set up email in nav drawer
        View headerView= navigationView.getHeaderView(0);
        TextView userEmailHeader= (TextView)headerView.findViewById(R.id.nav_header_email);
        final ImageView propic=(ImageView)headerView.findViewById(R.id.image_nav_header);
        final TextView DocnameNav=(TextView) headerView.findViewById(R.id.nav_header_name);
        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(getIntent().getStringExtra("type").equals("Doctor"))
                startActivity(new Intent(getApplicationContext(), DocProfile.class));

                else
                    startActivity(new Intent(getApplicationContext(), UserProfile.class));
            }
        });


        search = findViewById(R.id.searchItem);
        searchImg = findViewById(R.id.icon_search);

        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String in = search.getText().toString();
                list.clear();
                if (in.isEmpty())
                    settingfeedadapter();

                else
                settingSearchfeed(in);

            }
        });

        if (mAuth.getCurrentUser()!=null)
        userEmailHeader.setText(mAuth.getCurrentUser().getEmail());
        navigationView.setNavigationItemSelectedListener(this);

        //setting up user name and profile pic in nav drawer
        DatabaseReference dbref2= FirebaseDatabase.getInstance().getReference("Doctor/"+mAuth.getCurrentUser().getUid()+"/imageURL/");
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageURL= dataSnapshot.getValue(String.class);
                Picasso.get().load(imageURL).into(propic);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"unable to download userImage",Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference dbref3= FirebaseDatabase.getInstance().getReference("Doctor/"+mAuth.getCurrentUser().getUid()+"/name/");
        dbref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name= dataSnapshot.getValue(String.class);
                DocnameNav.setText(name);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"unable to set userName",Toast.LENGTH_SHORT).show();
            }
        });

        //seting up refresh layout
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.RefreshHome);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                settingfeedadapter();
            }
        });

       settingfeedadapter();

        ImageView chatbtn = findViewById(R.id.chatBtn);
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
                Animatoo.animateSwipeRight(HomeActivity.this);
            }
        });

//        Toast.makeText(HomeActivity.this, type_user,Toast.LENGTH_LONG).show();

        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START, true);
            }
        });



        final BubbleNavigationConstraintView bubblenavigation = findViewById(R.id.bottomNav);
        bubblenavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                if (position == 1) {
                    Intent intent= new Intent(HomeActivity.this, AppointmentsActivity.class);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    startActivity(intent);
                    Animatoo.animateFade(HomeActivity.this);
                } else if (position == 2) {
                    if(getIntent().getStringExtra("type").equals("Doctor")) {
                        Intent intent = new Intent(HomeActivity.this, PostUploadActivity.class);
                 //       intent.putExtra("type", getIntent().getStringExtra("type"));
                        startActivity(intent);
                        Animatoo.animateFade(HomeActivity.this);
                    }
                    else {
                        Toast.makeText(HomeActivity.this,"User cannot upload post",Toast.LENGTH_LONG).show();
                    }
                } else if (position == 3) {

                    Intent intent= new Intent(HomeActivity.this, AmbulanceActivity.class);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    startActivity(intent);
                    Animatoo.animateFade(HomeActivity.this);

                }

                else if (position == 4) {

                    Intent intent= new Intent(HomeActivity.this, CartActivity.class);
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    startActivity(intent);
                    Animatoo.animateFade(HomeActivity.this);

                }

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_one:  drawerLayout.closeDrawer(GravityCompat.START,true);
                                        return true;
            case R.id.nav_item_two :  Intent intent= new Intent(HomeActivity.this, CartActivity.class);
                                        intent.putExtra("type",getIntent().getStringExtra("type"));
                                        startActivity(intent);
                                        return true;
            case R.id.nav_item_three :  intent= new Intent(HomeActivity.this, AmbulanceActivity.class);
                                        intent.putExtra("type",getIntent().getStringExtra("type"));
                                        startActivity(intent);
                                        return true;
            case R.id.nav_item_four : if(getIntent().getStringExtra("type").equals("Doctor")) {
                                        Log.i("TYPE","DOctor");
                                        intent = new Intent(HomeActivity.this, DocProfile.class);
                                        intent.putExtra("type", getIntent().getStringExtra("type"));
                                        startActivity(intent);
                                        return true;
                                        }
                                        else{
                                            intent = new Intent(HomeActivity.this, UserProfile.class);
                                            intent.putExtra("type", getIntent().getStringExtra("type"));
                                            startActivity(intent);
                                            return true; }
            case R.id.nav_item_five :   new AlertDialog.Builder(this)
                                        .setIcon(R.drawable.alert)
                                        .setTitle("Are you sure")
                                        .setMessage("do you want to Sign Out?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                signOut(); return ;
                                            }
                                        }).setNegativeButton("No",null)
                                        .show();

        }
        return true;
    }


    private void settingfeedadapter(){
        swipeRefreshLayout.setRefreshing(true);
        recyclerview = findViewById(R.id.recyclerView);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    progressBar.setVisibility(View.INVISIBLE);
                    PostUploadInfo postUploadInfo = postSnapshot.getValue(PostUploadInfo.class);
                    list.add(postUploadInfo);
                }
                postAdapter = new FeedAdapter(list, getApplicationContext());
                recyclerview.setAdapter(postAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void settingSearchfeed(final String name){
        swipeRefreshLayout.setRefreshing(true);
        recyclerview = findViewById(R.id.recyclerView);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    PostUploadInfo postUploadInfo = postSnapshot.getValue(PostUploadInfo.class);
                    if (postUploadInfo.getDocName().equals(name))
                    list.add(postUploadInfo);
                }
                postAdapter = new FeedAdapter(list, getApplicationContext());
                recyclerview.setAdapter(postAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void signOut() {
        SharedPreferences pref= getSharedPreferences("UserType",0);
        SharedPreferences.Editor mEditor= pref.edit();
        mEditor.remove("Type");
        mEditor.commit();
        mAuth.signOut();
        finish();
                //Add Alert dialogue Box
                startActivity(new Intent(HomeActivity.this,OptionActivity.class));
    }
}

