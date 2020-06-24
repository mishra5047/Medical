package com.example.nirogo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.R;
import com.example.nirogo.ScreenSize;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity {

    TextView txt;
    public static String size;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences pref = getSharedPreferences("UserType", 0); // 0 - for private mode
        ScreenSize size_check = new ScreenSize();
        size = size_check.screenCheck(MainActivity.this);

        if (size.equalsIgnoreCase("Small")) {
            setContentView(R.layout.activity_main_small);
            Log.i("Screen Return Value","Small");
        }
        else
            setContentView(R.layout.activity_main);

    txt = findViewById(R.id.txtStart);
    txt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!pref.contains("Type")) {
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                startActivity(intent);
            }
            else if(pref.getString("Type","NONE").equals("Doctor")){
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("type","Doctor");
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("type","User");
                startActivity(intent);

            }
        }
    });

    }
}
