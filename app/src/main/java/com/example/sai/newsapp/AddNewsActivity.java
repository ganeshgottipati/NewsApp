package com.example.sai.newsapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddNewsActivity extends AppCompatActivity{
    private ImageView imageView;
    private EditText editText;
    private Button button,gallery,camera;
    private int PICK_IMAGE_REQUEST = 1;
    private String tab;

    String item;
    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference, ref1;

    public ProgressDialog progressDialog;
    String ImageUploadId;
    private static final int CAMERA_REQUEST_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "open.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));



        imageView=(ImageView)findViewById(R.id.image);
        editText=(EditText)findViewById(R.id.desc);
        button=(Button)findViewById(R.id.upload);
        gallery=(Button)findViewById(R.id.gallery);
        camera=(Button)findViewById(R.id.camera);
        progressDialog = new ProgressDialog(AddNewsActivity.this);


        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference();


        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.category);

        // Spinner click listener
        //spinner.setOnItemSelectedListener(AddNewsActivity.this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Sports");
        categories.add("Education");
        categories.add("Business");
        categories.add("Movies");
        categories.add("Political");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0 :
                        Storage_Path = "All_Image_Uploads/Sports/";
                        ref1=databaseReference.child("All_Image_Uploads_Database").child("Sports");
                        break;
                    case 1 :
                        Storage_Path = "All_Image_Uploads/Education/";
                        ref1=databaseReference.child("All_Image_Uploads_Database").child("Education");
                        break;
                    case 2 :
                        Storage_Path = "All_Image_Uploads/Business/";
                        ref1=databaseReference.child("All_Image_Uploads_Database").child("Business");
                        break;
                    case 3 :
                        Storage_Path = "All_Image_Uploads/Movies/";
                        ref1=databaseReference.child("All_Image_Uploads_Database").child("Movies");
                        break;
                    case 4 :
                        Storage_Path = "All_Image_Uploads/Political/";
                        ref1=databaseReference.child("All_Image_Uploads_Database").child("Political");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageFileToFirebaseStorage();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                imageView.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                gallery.setText("Image Selected");

            } catch (IOException e) {

                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Please Wait..");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path+System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = editText.getText().toString().trim();

                            ImageUploadInfo details = new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());

                            ImageUploadId = ref1.push().getKey();

                            ref1.child(ImageUploadId).child("imageName").setValue(details.getImageName());
                            ref1.child(ImageUploadId).child("imageURL").setValue(details.getImageURL());

                            // Adding image upload id s child element into databaseReference.

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();

                            startActivity(new Intent(AddNewsActivity.this , MainActivity.class));
                        }

                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(AddNewsActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Please Wait..");

                        }
                    });
        }
        else
            Toast.makeText(AddNewsActivity.this, "Please Select Image!!", Toast.LENGTH_SHORT).show();
    }

}
