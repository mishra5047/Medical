package com.example.nirogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.nirogo.Adapters.Feed.FeedAdapter;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.squareup.picasso.Picasso;

public class ImageOpener extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_opener);

        ImageView img = findViewById(R.id.image);
        Button back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

    if (getIntent().hasExtra("imgurl")){
        String url = getIntent().getStringExtra("imgurl");
        Picasso.get().load(url).into(img);
    }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}