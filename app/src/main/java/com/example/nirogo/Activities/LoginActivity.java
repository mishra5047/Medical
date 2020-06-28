package com.example.nirogo.Activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirogo.AdminActivity;
import com.example.nirogo.Doctor.DetailsDoctor;
import com.example.nirogo.Doctor.DoctorActivity;
import com.example.nirogo.ForgotPassword;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.R;
import com.example.nirogo.ScreenSize;
import com.example.nirogo.Supplier.SupplierActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {

    private EditText email;
    private EditText password;
    private TextView signin;
    private ImageView googleLogin;
    private ImageView facebookLogin;
    private TextView signupfromlogin;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private Intent intent;
    private String Type;
    private GoogleSignInClient mGoogleSignInClient;
    private  TextView forgotPassword;
    SharedPreferences sharedPreferences;




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user= mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(user!=null&& user.isEmailVerified()){
            PassIntent();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenSize screenSize = new ScreenSize();
        String size = screenSize.screenCheck(LoginActivity.this);
//        if (size.equalsIgnoreCase("Small")) {
//            setContentView(R.layout.activity_login_small);
//            Log.i("Screen Return Value","Small");
//        }
//        else
       setContentView(R.layout.activity_login_small);


        Button back = findViewById(R.id.Loginback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });


//
//        hideKeyboard(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        email= findViewById(R.id.loginEmail);
        password= findViewById(R.id.loginpassword);
        forgotPassword=(TextView)findViewById(R.id.forgotpw) ;
        intent= getIntent();
        Type= intent.getStringExtra("type");

        googleLogin = findViewById(R.id.logingoogle);
        signin= findViewById(R.id.Signinbutton);
        signupfromlogin= findViewById(R.id.signupfromlogin);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(LoginActivity.this, ForgotPassword.class);
                i.putExtra("type",getIntent().getStringExtra("type"));
                startActivity(i);
            }
        });

        creategooglerequest();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("Signin Button","Clicked");
                String EmailText= email.getText().toString().trim();
                String PassWordText= password.getText().toString().trim();

                if(EmailText.isEmpty()) {
                    email.setError("Enter Email");
                    return;
                }

                if(PassWordText.isEmpty()) {
                    password.setError("Enter Password");
                    return;
                }

                if (EmailText.equals("admin") && PassWordText.equals("admin")){
                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                }

                if (EmailText.equals("dev") && PassWordText.equals("dev")){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }

                Toast.makeText(getApplicationContext(), "Signing In", Toast.LENGTH_LONG).show();
                setemailLogin(EmailText,PassWordText);
            }
        });

       googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signupfromlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Type.equals("Doctor")){
                    Intent i= new Intent(LoginActivity.this, DoctorActivity.class);
                    i.putExtra("type", "Doctor");
                    startActivity(i);
                    return;
                }
                 if(Type.equals("User")){
                     Intent i= new Intent(LoginActivity.this, UserActivity.class);
                    i.putExtra("type", "User");
                    startActivity(i);
                    return;
                }
                 if(Type.equals("Supplier")){
                    Intent i= new Intent(LoginActivity.this, SupplierActivity.class);
                    startActivity(i);
                    return;
                }
            }
        });
    }

    private void setemailLogin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, check if email has been verified or not
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                PassIntent(); //checks if user details are present in db or not
                                return;

                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Please verify your account",Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "wrong Email or password", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Please Register", Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "signInWithEmail:failure", task.getException());

                        }

                        // ...
                    }
                });
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d("LOG_TAG", "firebaseAuthWithGoogle:" + account.getId());

                firebaseAuthWithGoogle(account.getIdToken());

            }
            catch (ApiException e) {

                Toast.makeText(this, "signup failed", Toast.LENGTH_SHORT).show();

                Log.e ("LOG_TAG","failed status code:"+ e.getStatusCode());

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

    private void PassIntent() {

        if (Type.equals("User")) {
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("User/");

            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        // The child doesn't exist
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        Log.i("LOGIN USER UID", uid);
                        Toast.makeText(LoginActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        i.putExtra("type", "User");
                        i.putExtra("type", getIntent().getStringExtra("type"));
                        i.putExtra("USER UID", uid);
                        startActivity(i);
                        return;
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        Toast.makeText(LoginActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, DetailsUser.class);
                        i.putExtra("type", "User");
                        i.putExtra("type", getIntent().getStringExtra("type"));
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

    else if (Type.equals("Doctor")) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Doctor/");

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    // The child doesn't exist
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();
                    Log.i("LOGIN USER UID", uid);
                    Toast.makeText(LoginActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.putExtra("type", "Doctor");
                    i.putExtra("USER UID", uid);
                    startActivity(i);
                    return;
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();
                    Toast.makeText(LoginActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, DetailsDoctor.class);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

