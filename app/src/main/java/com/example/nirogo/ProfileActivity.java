package com.example.nirogo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity {

    private ImageView update;
    private TextView name;
    private TextView age;
    private TextView speciality;
    private TextView about;
    private TextView experience;
    private TextView education;
    private TextView email;
    private TextView phoneno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name=(TextView) findViewById(R.id.nameProfile);
        age=(TextView)findViewById(R.id.AgeProfile);
        speciality=(TextView)findViewById(R.id.spectialityprofile);
        about=(TextView)findViewById(R.id.aboutprofile);
        experience=(TextView)findViewById(R.id.experienceprofile);
        education=(TextView)findViewById(R.id.educationprofile);
        email=(TextView)findViewById(R.id.emailprofile);
        phoneno=(TextView)findViewById(R.id.phoneprofile);


        Intent intent= this.getIntent();
        if(intent!=null){
            if(intent.hasExtra("Activity")&&intent.getStringExtra("Activity").equals("UpdateProfileActivity")){
                Log.e("LOG_TAG","REACHEDdddddddddddddddddd hereeeeeeeeeeeeeeee");
                if(!(intent.getStringExtra("namestring").equals(""))){
                    name.setText(intent.getStringExtra("namestring"));
                }
                if(!(intent.getStringExtra("agestring").isEmpty())) {
                    age.setText(intent.getStringExtra("agestring"));
                }
                if(!(intent.getStringExtra("specialitystring").isEmpty())) {
                    speciality.setText(intent.getStringExtra("specialitystring"));
                }
                if(!(intent.getStringExtra("aboutstring").isEmpty())) {
                    about.setText(intent.getStringExtra("aboutstring"));
                }
                if(!(intent.getStringExtra("experiencestring").isEmpty())) {
                    experience.setText(intent.getStringExtra("experiencestring"));
                }
                if(!(intent.getStringExtra("educationstring").isEmpty())) {
                    education.setText(intent.getStringExtra("educationstring"));
                }
                if(!(intent.getStringExtra("emailstring").isEmpty())){
                    email.setText(intent.getStringExtra("emailstring"));
                }
                if(!(intent.getStringExtra("phonestring").isEmpty())){
                    phoneno.setText(intent.getStringExtra("phonestring"));
                }

            }
        }


       /* if (getIntent().hasExtra("docname")){
            String name = getIntent().getStringExtra("docname");
            TextView nameTxt = findViewById(R.id.textView);
            nameTxt.setText(name);
        }*/

        update= (ImageView) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(ProfileActivity.this,updateprofile.class);
                i.putExtra("namep",name.getText());
                i.putExtra("agep",age.getText());
                i.putExtra("specialityp",speciality.getText());
                i.putExtra("aboutp",about.getText());
                i.putExtra("experiencep",experience.getText());
                i.putExtra("educationp",experience.getText());
                i.putExtra("emailp",email.getText());
                i.putExtra("phonep",phoneno.getText());
                startActivity(i);


            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
    }
}