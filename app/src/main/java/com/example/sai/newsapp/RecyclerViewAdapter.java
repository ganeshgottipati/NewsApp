package com.example.sai.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by sai on 17-06-2018.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public String Database_Path = "All_Image_Wishlists/";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth firebaseAuth;
    Context context;
    private List<ImageUploadInfo> entries;
    private String userId,imageURL;

    public RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList) throws IOException {

        this.entries = TempList;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        RecyclerViewAdapter.MyViewHolder viewHolder = new RecyclerViewAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            holder.wishlist.setVisibility(View.GONE);
            holder.imageview.setVisibility(View.GONE);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String name = "Sports";
        final String user1 = preferences.getString("email", "");


        ImageUploadInfo UploadInfo = entries.get(position);

        holder.text.setText(UploadInfo.getImageName());
        Glide.with(context).load(UploadInfo.getImageURL()).into(holder.image);

        holder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = context.getPackageManager();
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, holder.text.getText().toString());

                    context.startActivity(Intent.createChooser(waIntent, "Share with"));

                }
                catch (PackageManager.NameNotFoundException e)
                {
                    Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        holder.wishlist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mFirebaseInstance = FirebaseDatabase.getInstance();
                // get reference to 'users' node
                mFirebaseDatabase = mFirebaseInstance.getReference(Database_Path);

                userId = mFirebaseDatabase.push().getKey();
                Toast.makeText(context,name, Toast.LENGTH_SHORT).show();

                FirebaseDatabase.getInstance().getReference().child("All_Image_Uploads_Database").child(name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ImageUploadInfo user = snapshot.getValue(ImageUploadInfo.class);

                                    if (holder.text.getText() == user.imageName)
                                    {
                                        imageURL = user.imageURL;

                                        ImageUploadInfo details = new ImageUploadInfo(holder.text.getText().toString(), imageURL, name, user1);

                                        mFirebaseDatabase.child(userId).child("imageName").setValue(details.getImageName());
                                        mFirebaseDatabase.child(userId).child("imageURL").setValue(details.getImageURL());
                                        mFirebaseDatabase.child(userId).child("category").setValue(details.getCategory());
                                        mFirebaseDatabase.child(userId).child("email").setValue(details.getEmail());

//                                            holder.cd.setVisibility(View.GONE);

                                        addUserChangeListener();

                                        holder.wishlist.setImageResource(R.drawable.add);
                                    }
                                }
                                //do what you want with the email
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseInstance = FirebaseDatabase.getInstance();

                // get reference to 'users' node
                mFirebaseInstance.getReference("All_Image_Uploads_Database").child(name).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        int count = (int) dataSnapshot.getChildrenCount();
                        String key[] = new String[count];
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            key[i] = ds.getKey();
                            i++;
                        }
                        mFirebaseInstance.getReference("All_Image_Uploads_Database").child(name).child(key[position]).removeValue();
                        Toast.makeText(context, "News Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void addUserChangeListener()
    {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageUploadInfo details = dataSnapshot.getValue(ImageUploadInfo.class);

                // Check for null
                if (details == null) {
                    Toast.makeText(context, "Data is Null..", Toast.LENGTH_SHORT).show();
                    return;
                }
                    Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private Bitmap getBitmapFromView(View view) {
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(returnedBitmap);
//        Drawable bgDrawable =view.getBackground();
//        if (bgDrawable!=null) {
//            //has background drawable, then draw it on the canvas
//            bgDrawable.draw(canvas);
//        }   else{
//            //does not have background drawable, then draw white background on the canvas
//            canvas.drawColor(Color.WHITE);
//        }
//        view.draw(canvas);
//        return returnedBitmap;
//    }


    @Override
    public int getItemCount() {

        return entries.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView image;
        ImageView imageview,wishlist,delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.tv_text);
            image=(ImageView)itemView.findViewById(R.id.iv_image);
            imageview=(ImageView)itemView.findViewById(R.id.shar);
            wishlist=(ImageView)itemView.findViewById(R.id.wish);
            delete=(ImageView)itemView.findViewById(R.id.delete);

            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),"open.ttf");

            text.setTypeface(custom_font);
        }
    }
}

