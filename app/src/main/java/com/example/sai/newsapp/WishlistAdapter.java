package com.example.sai.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sai.newsapp.ImageUploadInfo;
import com.example.sai.newsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.Glide.clear;

/**
 * Created by sai on 26-12-2017.
 */
public class WishlistAdapter extends BaseAdapter {
    private Context context;
    
    private List<ImageUploadInfo> imageUploadInfos;
    private String value,user1;
    private DatabaseReference databaseReference;

    public WishlistAdapter(Context context, ArrayList<ImageUploadInfo> words) {
        this.context=context;
        this.imageUploadInfos=words;
    }


    @Override
    public int getCount() {
        return imageUploadInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return imageUploadInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        final View listitem = View.inflate(context, R.layout.activity_showwishlist, null);

        clear(listitem);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user1 = preferences.getString("email", "");
        Toast.makeText(context,user1, Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance().getReference().child("All_Image_Wishlists")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ImageUploadInfo user = snapshot.getValue(ImageUploadInfo.class);

                        if (user1 == user.email)
                        {
                            Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
                                ImageView img = (ImageView)listitem.findViewById(R.id.iv_image);
                                Glide.with(context).load(imageUploadInfos.get(position).getImageURL()).into(img);

                                TextView txt = (TextView) listitem.findViewById(R.id.tv_text);
                                txt.setText(imageUploadInfos.get(position).getImageName());

                                TextView txt1 = (TextView) listitem.findViewById(R.id.category);
                                txt1.setText(imageUploadInfos.get(position).getCategory());
                        }
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return listitem;
    }
}
