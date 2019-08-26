package com.example.noteboi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class forgot extends AppCompatActivity {

    EditText forgot_e;
    Button reset_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        forgot_e = findViewById(R.id.ed_forgot);
        reset_b = findViewById(R.id.forgot_b);

    }

    private boolean isNetworkAvailable(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else return false;

    }

    public void reset_pass(View view){

        if(isNetworkAvailable()){

            ParseUser.requestPasswordResetInBackground(forgot_e.getText().toString(), new RequestPasswordResetCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(forgot.this);
                        builder.setMessage("An email has been sent to you. You can now reset your password")
                                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent a = new Intent(forgot.this, MainActivity.class);
                                        startActivity(a);
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        Intent a = new Intent(forgot.this, MainActivity.class);
                                        startActivity(a);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout,"Email not found. make sure you have verified your email address",Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
        else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_LONG).show();

    }

}
