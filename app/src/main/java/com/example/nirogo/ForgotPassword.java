package com.example.nirogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirogo.Activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText editEmail;
    String Email;
    TextView Send;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editEmail= (EditText)findViewById(R.id.editEmail);
        Send=(TextView)findViewById(R.id.SendEmail);
        mAuth=FirebaseAuth.getInstance();

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email= editEmail.getText().toString();
                if(Email.isEmpty()){
                    editEmail.setError("Enter Email");
                }
                else{
                        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ForgotPassword.this,"Reset Password link has been sent. Please check your Email.",Toast.LENGTH_LONG).show();
                                    Intent i= new Intent(ForgotPassword.this, LoginActivity.class);
                                    i.putExtra("type",getIntent().getStringExtra("type"));
                                    startActivity(i);
                                }
                                else{
                                    String message= task.getException().getMessage();
                                    Toast.makeText(ForgotPassword.this,"Error Occured:"+message,Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }




            }
        });


    }
}