package com.example.sai.newsapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.reverse;

/**
 * Created by sai on 21-06-2018.
 */
public class WishlistActivity extends AppCompatActivity {
    ListView lv;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ImageUploadInfo> arrayList;
    ArrayList<ImageUploadInfo> NewarrayList = new ArrayList<>();
    WishlistAdapter arrayAdapter;
    ImageUploadInfo uploadInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "open.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        lv=(ListView)findViewById(R.id.listitem);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("All_Image_Wishlists/");

        arrayList = new ArrayList<>();
        arrayAdapter = new WishlistAdapter(this,uploadInfo);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    uploadInfo = ds.getValue(ImageUploadInfo.class);
                    SharedPreferences preferences3 = PreferenceManager.getDefaultSharedPreferences(WishlistActivity.this);
                    final String name = ds.child("Category").getValue(String.class);

                    final String user1 = preferences3.getString("email", "");
                    uploadInfo.setCategory(name);
                    uploadInfo.setEmail(user1);
                    arrayList.add(new ImageUploadInfo(uploadInfo.getImageName(),uploadInfo.getImageURL(),uploadInfo.getCategory(),uploadInfo.getEmail()));
                }

                for (ImageUploadInfo event : arrayList) {
                    boolean isFound = false;
                    // check if the event name exists in noRepeat
                    for (ImageUploadInfo e : NewarrayList) {
                        if ((e.getImageName().equals(event.getImageName())) || (e.getImageURL().equals(event.getImageURL()))) {
                            isFound = true;
                            break;
                        }
                    }
                    if(!isFound) NewarrayList.add(event);
                }
                arrayAdapter = new WishlistAdapter(getApplicationContext(),NewarrayList);
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

