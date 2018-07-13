package com.example.sai.newsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchActivity extends AppCompatActivity implements Animation.AnimationListener{

    private TextView t1;
    private ImageView logo;
    private Button start;
    private Animation text , pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "open.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        text = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        text.setAnimationListener(this);

        pic = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        pic.setAnimationListener(this);


        t1 = (TextView) findViewById(R.id.text);
        logo = (ImageView) findViewById(R.id.logo);
        start = (Button) findViewById(R.id.start);
        t1.startAnimation(text);
        logo.startAnimation(pic);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this,SignupActivity.class));
            }
        });

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
