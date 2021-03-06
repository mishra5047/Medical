package com.example.nirogo.Doctor;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirogo.Activities.LoginActivity;
import com.example.nirogo.Activities.OptionActivity;
import com.example.nirogo.Adapters.Messages.Doc;
import com.example.nirogo.R;
import com.example.nirogo.ScreenSize;
import com.example.nirogo.User.DetailsUser;
import com.example.nirogo.User.UserActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.example.nirogo.HomeScreen.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;


public class DoctorActivity extends Activity {

    private GoogleSignInClient mGoogleSignInClient;
    private ImageView googleimage;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String LOG_TAG = DoctorActivity.class.getSimpleName();
    private TextView signup;
    private EditText email;
    private EditText password;
    private String size;
    private  String uid;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ScreenSize screenSize = new ScreenSize();
        String size = screenSize.screenCheck(DoctorActivity.this);
//
//        if (size.equalsIgnoreCase("Small")) {
//            setContentView(R.layout.activity_doctor_small);
//            Log.i("Screen Return Value","Small");
//        }
//        else
            setContentView(R.layout.activity_doctor);

        mAuth = FirebaseAuth.getInstance();

        Button back = findViewById(R.id.backDoc);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });


        signup = findViewById(R.id.signupDoctor);
        email = findViewById(R.id.usernameDoc);
        password = findViewById(R.id.passwordDoc);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext = email.getText().toString().trim();
                String passwordtext = password.getText().toString();

                //check constraints that email and password shouldnot be empty
                if (TextUtils.isEmpty(emailtext)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordtext)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "PAssword too short", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
                createrequestusingEmailPassword(emailtext, passwordtext);

            }
        });


    }

    private void creategooglerequest() {
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());

                firebaseAuthWithGoogle(account.getIdToken());
                FirebaseUser user = mAuth.getCurrentUser();
                uid= user.getUid();
                Log.i("USER UID",uid);

            } catch (ApiException e) {

                Toast.makeText(this, "signup failed", Toast.LENGTH_SHORT).show();

                Log.e(LOG_TAG, "failed status code:" + e.getStatusCode());

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
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

        TextView signup = findViewById(R.id.signupDoctor);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this, DetailsDoctor.class);
                intent.putExtra("USER UID",uid);
                startActivity(intent);
            }
        });

    }

    private void createrequestusingEmailPassword(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("TAG","REached");

                            final FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "A verification email has been send to your gmail. please verify your account and signin again.", Toast.LENGTH_LONG).show();
                                        Intent i= new Intent(DoctorActivity.this, LoginActivity.class);
                                        i.putExtra("type","Doctor");
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Some error signing you in", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            try {
                                throw task.getException();
                            }
                            // if user enters wrong email.

                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                progressDialog.dismiss();
                                Log.d(TAG, "onComplete: malformed_email");
                                Toast.makeText(DoctorActivity.this, "Enter Correct Email", Toast.LENGTH_SHORT).show();
                                return;

                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                Log.d(TAG, "onComplete: exist_email");
                                Toast.makeText(DoctorActivity.this, "Email already Exist", Toast.LENGTH_SHORT).show();
                                return;


                            } catch (Exception e) {
                                Toast.makeText(DoctorActivity.this,"Some error in signing up",Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                        } }
                });
    }

    private void PassIntent() {
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Doctor/");

            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        // The child doesn't exist
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        Log.i("LOGIN USER UID", uid);
                        Toast.makeText(DoctorActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(DoctorActivity.this, HomeActivity.class);
                        i.putExtra("type", "Doctor");
                        i.putExtra("USER UID", uid);
                        startActivity(i);
                        return;
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        Toast.makeText(DoctorActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(DoctorActivity.this, DetailsDoctor.class);
                        i.putExtra("type", "Doctor");
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

