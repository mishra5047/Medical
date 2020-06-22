package com.example.nirogo.Profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.HomeScreen.HomeActivity;
import com.example.nirogo.R;
import com.example.nirogo.User.DetailsUser;
import com.example.nirogo.User.UserUploadInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class UpdateProfileDoc extends Activity {
    private EditText name;
    private EditText age;
    private EditText speciality;
    private EditText phone, city;
    private Button submitupdate;
    private ImageView image;
    String docUrl;

    Uri FilePathUri;
    FirebaseAuth auth;
    DatabaseReference doctor_fetch, doc_update;
    String Database_Path_Fetch = "Doctor/";
    int Image_Request_Code = 7;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String Storage_Path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DocProfile.class));
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog  = new ProgressDialog(this);

        name= (EditText) findViewById(R.id.nameupdate);
        age= (EditText) findViewById(R.id.ageupdate);
        speciality= (EditText) findViewById(R.id.specialityupdate);
        phone= (EditText) findViewById(R.id.phoneupdate);
        image = findViewById(R.id.imageDoc);
        city = findViewById(R.id.cityupdate);


        auth = FirebaseAuth.getInstance();
        doctor_fetch = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);
        doctor_fetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DocUploadInfo docUploadInfo = postSnapshot.getValue(DocUploadInfo.class);
                    if ((docUploadInfo.id).equals(auth.getCurrentUser().getUid())){
                        name.setText(docUploadInfo.name);
                        age.setText(docUploadInfo.age);
                        speciality.setText(docUploadInfo.speciality);
                        phone.setText(docUploadInfo.phone);
                        city.setText(docUploadInfo.city);
                        Picasso.get().load(docUploadInfo.imageURL).into(image);

                        docUrl = docUploadInfo.getImageURL();
                    }
                    }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });


        submitupdate=(Button)findViewById(R.id.submitupdate);

        submitupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String namestring = name.getText().toString();
                final String agestring = age.getText().toString();
                final String specialitystring = speciality.getText().toString();
                final String phonestring = phone.getText().toString();
                final String cityString = city.getText().toString();
                Log.i("Phone no.", phonestring);

                if (FilePathUri != null) {

                    // Setting progressDialog Title.
                    progressDialog.setTitle("Image is Uploading...");

                    // Showing progressDialog.
                    progressDialog.show();


                    final StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
                storageReference2nd.putFile(FilePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                // Hiding the progressDialog after done uploading.
                                progressDialog.dismiss();

                                storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String down = uri.toString();

                                        doc_update = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);

                                        DocUploadInfo docUploadInfo = new DocUploadInfo(auth.getCurrentUser().getUid(), phonestring, namestring, specialitystring, agestring, cityString, down);
                                        doc_update.child(auth.getCurrentUser().getUid()).setValue(docUploadInfo);
                                        startActivity(new Intent(getApplicationContext(), DocProfile.class));
                                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            });
                        }else
                    Toast.makeText(getApplicationContext(), "Select A Image", Toast.LENGTH_SHORT).show();

            }

        });
    }
            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {

                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

                    FilePathUri = data.getData();

                    try {

                        // Getting selected image into Bitmap.
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                        // Setting up bitmap selected image into ImageView.
                        image.setImageBitmap(bitmap);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }

            public String GetFileExtension(Uri uri) {

                ContentResolver contentResolver = getContentResolver();

                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

                // Returning the file Extension.
                return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

            }
        }


