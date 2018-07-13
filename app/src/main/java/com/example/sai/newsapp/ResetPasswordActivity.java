package com.example.sai.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sai on 16-06-2018.
 */
public class ResetPasswordActivity extends AppCompatActivity{
    private EditText email;
    private Button reset;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwdreset);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "open.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        email= (EditText) findViewById(R.id.email);
        reset=(Button)findViewById(R.id.reset);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString().trim();

                if (TextUtils.isEmpty(e)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(e)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent a Mail regarding instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPasswordActivity.this,ResetPasswordActivity.class));
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}
