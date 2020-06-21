package com.example.nirogo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirogo.AdminShow;
import com.example.nirogo.ChatActivity;
import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.HomeScreen.MessageActivity;
import com.example.nirogo.HomeScreen.MessagePreview;
import com.example.nirogo.MyAppointments;
import com.example.nirogo.R;
import com.example.nirogo.User.UserUploadInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AppointmentOption extends AppCompatActivity {

    LinearLayout offline, online;
    final int UPI_PAYMENT = 0;
    String mode ;
    String docName , docPhone, docId;

    DatabaseReference databaseReference, databaseReference2 ;
    String Database_Path = "UserAppointment/";
    String Database_Path_Nearby = "Admin/";

    DatabaseReference databaseReference_fetch;
    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog ;

    // DB for admin and userAppoint
    DatabaseReference dbrefUser, dbrefAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_option);

        progressDialog  = new ProgressDialog(this);

        offline = findViewById(R.id.offline);
        online = findViewById(R.id.online);

        firebaseAuth= FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseReference2 = FirebaseDatabase.getInstance().getReference(Database_Path_Nearby);

        TextView name = findViewById(R.id.drname);
         docName = getIntent().getStringExtra("docname");
         docPhone = getIntent().getStringExtra("phone");
         docId = getIntent().getStringExtra("docId");
        name.setText(docName);

        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   payUsingUpi("Nirogo", "9931959949@paytm", "Appoinment for " + docName, "1");
                mode = "offline";
                addToDB();
            }
        });

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    payUsingUpi("Nirogo", "9931959949@paytm", "Appoinment for " + docName, "1");
                mode = "online";
                addToDB();
            }
        });

    }


    void payUsingUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(getApplicationContext() ,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(AppointmentOption.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(getApplicationContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();
                if (mode.equals("offline")){
                    Toast.makeText(getApplicationContext(), "You have chosen offline mode, visit the Dr. ", Toast.LENGTH_SHORT).show();
                }
                else {
                   // add another type of messages activity
                    Toast.makeText(getApplicationContext(), "You can chat with Dr. in Messages", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    startActivity(intent);
                }

                //addToDB();

                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(getApplicationContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);

            }
            else {
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);

            }
        } else {
            Log.e("UPI", "Internet issue: ");

            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToDB() {
        final String Database_Path_Fetch = "User/";

        databaseReference_fetch = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);
        final String id = firebaseAuth.getCurrentUser().getUid();

        progressDialog.setTitle("Saving To DB...");

        // Showing progressDialog.
        progressDialog.show();

        databaseReference_fetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserUploadInfo userData = postSnapshot.getValue(UserUploadInfo.class);
                    String idCheck = userData.getId();

                    String path_admin = "Admin/";
                    String path_user = "UserApt/" + id +"/";
                    dbrefUser = FirebaseDatabase.getInstance().getReference(path_user);
                    dbrefAdmin = FirebaseDatabase.getInstance().getReference(path_admin);

                    if (idCheck.equalsIgnoreCase(id))
                    {
                        String name = userData.getName();
                        String phone = userData.getNumber();

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        String time = sdf.format(new Date());

                        SimpleDateFormat sdf_1 = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());
                        String date = sdf_1.format(new Date());

                        AdminShow adminShow = new AdminShow(docName, docPhone, name, phone, date, time);

                        dbrefAdmin.child(UUID.randomUUID().toString()).setValue(adminShow);

                        //user appointment
                        MyAppointments userAppoint = new MyAppointments(docName, date, time);

                        dbrefUser.child(UUID.randomUUID().toString()).setValue(userAppoint);


                        Toast.makeText(getApplicationContext(), "Visit My Appointments in Profile to View", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("docId", docId);
                        intent.putExtra("userId", id);
                        startActivity(intent);

                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void uploadToUserDB() {


    }

    private void uploadToAdminDB() {

    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
