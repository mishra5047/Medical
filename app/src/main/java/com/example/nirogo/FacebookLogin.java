package com.example.nirogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nirogo.Activities.LoginActivity;
import com.example.nirogo.Doctor.DetailsDoctor;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.User.DetailsUser;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class FacebookLogin extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    Button back;
    ImageView image;
    TextView continueTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        final String type = getIntent().getStringExtra("type");

        back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

            image = findViewById(R.id.imageLogin);

            continueTxt = findViewById(R.id.continue_txt);
            continueTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equalsIgnoreCase("doctor"))
                        startActivity(new Intent(getApplicationContext(), DetailsDoctor.class));

                    else if (type.equalsIgnoreCase("user"))
                        startActivity(new Intent(getApplicationContext(), DetailsUser.class));

                }
            });

            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);


            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Picasso.get().load("http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=square").into(image);
                }

                @Override
                public void onCancel() {
                    info.setText("Login cancelled");
                }

                @Override
                public void onError(FacebookException error) {
                    info.setText("login failed");
                }
            });
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
