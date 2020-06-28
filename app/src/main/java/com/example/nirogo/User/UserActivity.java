package com.example.nirogo.User;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nirogo.Activities.LoginActivity;
import com.example.nirogo.Doctor.DetailsDoctor;
import com.example.nirogo.Doctor.DoctorActivity;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.Activities.MainActivity;
import com.example.nirogo.Activities.OptionActivity;
import com.example.nirogo.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

import android.widget.TextView;

public class UserActivity extends Activity {

    private GoogleSignInClient mGoogleSignInClient;
    private ImageView googleimage;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String LOG_TAG= UserActivity.class.getSimpleName();
    private TextView signup;
    private EditText email;
    private EditText password;

    @Override
    public void onStart() {

        super.onStart();
        FirebaseUser user= mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(user!=null&& user.isEmailVerified()){
            Intent intent = new Intent(UserActivity.this, HomeActivity.class);
            intent.putExtra("type",getIntent().getStringExtra("type"));
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((MainActivity.size).equalsIgnoreCase("Small")) {
            setContentView(R.layout.activity_user_small);
            Log.i("Screen Return Value","Small");
        }
        else
            setContentView(R.layout.activity_user);

        Button back = findViewById(R.id.backUser);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });

        mAuth= FirebaseAuth.getInstance();
        signup= findViewById(R.id.signupUser);
        email= findViewById(R.id.EmailPatient);
        password= findViewById(R.id.passwordPatient);

        //setting up google request

        creategooglerequest();

        //setting up onclick listener for signup via email/pw
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext= email.getText().toString().trim();
                String passwordtext= password.getText().toString();

                //check constraints that email and password shouldnot be empty
                if(TextUtils.isEmpty(emailtext)){
                    Toast.makeText(getApplicationContext(),"Please Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordtext)){
                    Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Password too short",Toast.LENGTH_SHORT).show();
                    return;
                }
                createrequestusingEmailPassword(emailtext,passwordtext);
            }});

        //setting up onclick Listener for googlesignup
    }



    private  void creategooglerequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        // getting the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

         //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());

                firebaseAuthWithGoogle(account.getIdToken());

            }
            catch (ApiException e) {

                Toast.makeText(this, "signup failed", Toast.LENGTH_SHORT).show();

                Log.e (LOG_TAG,"failed status code:"+ e.getStatusCode());

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            PassIntent();
                        }

                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }




    private void createrequestusingEmailPassword(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                final FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "A verification email has been send to your gmail. please verify your account and signin again.", Toast.LENGTH_LONG).show();
                            Intent i= new Intent(UserActivity.this,LoginActivity.class);
                            i.putExtra("type","User");
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Some error signing you in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            else {
                    try {
                        throw task.getException();
                    }
                    // if user enters wrong email.

                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                        Log.d(TAG, "onComplete: malformed_email");
                        Toast.makeText(UserActivity.this, "Enter Correct Email", Toast.LENGTH_SHORT).show();
                        return;

                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        Log.d(TAG, "onComplete: exist_email");
                        Toast.makeText(UserActivity.this, "Email already Exist", Toast.LENGTH_SHORT).show();
                        return;


                    } catch (Exception e) {
                        Log.d(TAG, "onComplete: " + e.getMessage());
                    }
                }
            }
            });
    }

    private void PassIntent() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("User/");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    // The child doesn't exist
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();
                    Log.i("LOGIN USER UID", uid);
                    Toast.makeText(UserActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UserActivity.this, HomeActivity.class);
                    i.putExtra("type", "User");
                    i.putExtra("USER UID", uid);
                    startActivity(i);
                    return;
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();
                    Toast.makeText(UserActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UserActivity.this, DetailsUser.class);
                    i.putExtra("type", "User");
                    i.putExtra("USER UID", uid);
                    startActivity(i);
                    return;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
