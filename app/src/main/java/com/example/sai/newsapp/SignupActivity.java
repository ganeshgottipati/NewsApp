package com.example.sai.newsapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignupActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private EditText ed1, ed2;
    private Button b, b1, b2;
    private TextView t1;
    private ImageView img;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "open.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        checkConnection();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
        }
        ed1 = (EditText) findViewById(R.id.email);
        ed2 = (EditText) findViewById(R.id.password);
        t1 = (TextView) findViewById(R.id.skip);
        b = (Button) findViewById(R.id.sign_up_button);
        b1 = (Button) findViewById(R.id.sign_in_button);
        progressDialog = new ProgressDialog(getApplicationContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Registering...");

        t1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("skip", t1.getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = ed1.getText().toString().trim();
                String password = ed2.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressDialog.setMessage("Almost Done!!");
                                if (!task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("Username", email);
                                    intent.putExtras(b);
                                    startActivity(intent);
                                }
                            }
                        });

            }
        });
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            Toast.makeText(SignupActivity.this,message, Toast.LENGTH_SHORT).show();
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Toast.makeText(SignupActivity.this,message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}



