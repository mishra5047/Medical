package com.example.nirogo.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nirogo.Doctor.DocUploadInfo;
import com.example.nirogo.Profile.DocProfile;
import com.example.nirogo.Profile.UserProfile;
import com.example.nirogo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UpdateProfileUser extends AppCompatActivity {
    private EditText name;
    private EditText age;
    private EditText phone, city;
    private Button submitupdate;
    private ImageView image;
    String userUrl;

    Uri FilePathUri;
    int Image_Request_Code = 7;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference user_fetch, user_update;
    String Database_Path_Fetch = "User/";
    private String Storage_Path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_user);

        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DocProfile.class));
            }
        });

        name = (EditText) findViewById(R.id.nameupdate);
        age = (EditText) findViewById(R.id.ageupdate);
        phone = (EditText) findViewById(R.id.phoneupdate);
        image = findViewById(R.id.imageDoc);
        city = findViewById(R.id.cityupdate);


        user_update = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
        auth = FirebaseAuth.getInstance();
        user_fetch = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);
        user_fetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserUploadInfo userUploadInfo = postSnapshot.getValue(UserUploadInfo.class);
                    if ((userUploadInfo.id).equals(auth.getCurrentUser().getUid())) {
                        name.setText(userUploadInfo.name);
                        age.setText(userUploadInfo.age);
                        phone.setText(userUploadInfo.number);
                        city.setText(userUploadInfo.city);
                        Picasso.get().load(userUploadInfo.imageUrl).into(image);

                        userUrl = userUploadInfo.getImageUrl();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        submitupdate = (Button) findViewById(R.id.submitupdate);

        submitupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String namestring = name.getText().toString();
                final String agestring = age.getText().toString();
                final String phonestring = phone.getText().toString();
                final String cityString = city.getText().toString();

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

                                            user_update = FirebaseDatabase.getInstance().getReference(Database_Path_Fetch);

                                            UserUploadInfo userUploadInfo = new UserUploadInfo(auth.getCurrentUser().getUid(), namestring, agestring, cityString, userUrl, phonestring);
                                            user_update.child(auth.getCurrentUser().getUid()).setValue(userUploadInfo);
                                            startActivity(new Intent(getApplicationContext(), UserProfile.class));
                                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            });
                } else
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




