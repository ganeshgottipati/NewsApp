package com.example.sai.newsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static View.OnClickListener myOnClickListener;
    private FrameLayout t1;
    private FirebaseAuth firebaseAuth;
    private TextView name;
    private com.github.clans.fab.FloatingActionButton f1,f2,f3;
    private FloatingActionMenu f;
    private RelativeLayout loggedin;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "open.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        t1=(FrameLayout)findViewById(R.id.resignin);
        name=(TextView)findViewById(R.id.name);
        loggedin=(RelativeLayout)findViewById(R.id.welcome);

        f=(com.github.clans.fab.FloatingActionMenu) findViewById(R.id.menu);
        f1=(com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu1);
        f2=(com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu2);
        f3=(com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu3);

//        Typeface font = Typeface.createFromAsset(getAssets(), "open.ttf");
//        name.setTypeface(font);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            t1.setVisibility(View.GONE);
            loggedin.setVisibility(View.VISIBLE);
            String user=firebaseAuth.getCurrentUser().getEmail();
            name.setText(user);
            SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putString("email",user);
            editor.commit();
        }
        else
        {
            Bundle b=getIntent().getExtras();
            String skip=b.getString("skip");
            if(skip==null)
            {
                t1.setVisibility(View.GONE);
                f.setVisibility(View.VISIBLE);
                f3.setVisibility(View.VISIBLE);
                f1.setVisibility(View.VISIBLE);
                f2.setVisibility(View.VISIBLE);
                loggedin.setVisibility(View.GONE);
            }
            else
            {
                f.setVisibility(View.GONE);
                t1.setVisibility(View.VISIBLE);
                loggedin.setVisibility(View.GONE);
            }
        }


        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Create an adapter that knows which fragment should be shown on each page
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setCustomFont();

        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,WishlistActivity.class));
            }
        });

        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                Toast.makeText(getApplicationContext(), "LoggedOut", Toast.LENGTH_SHORT).show();
            }
        });

        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddNewsActivity.class));
            }
        });
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(i);
            }
        });


        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            } 
        };

    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "open.ttf"));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to Exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }
}
