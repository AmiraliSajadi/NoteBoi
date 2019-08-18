package com.example.noteboi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noteboi.MainActivity;
import com.example.noteboi.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class sign_up extends AppCompatActivity {

    EditText new_user, new_pass;
    Button new_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        new_user = findViewById(R.id.ed_new_user);
        new_pass = findViewById(R.id.ed_new_pass);
        new_signup = findViewById(R.id.b_signup);
    }

    private boolean isNetworkAvailable(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else return false;

    }

    public void sign_up(View view){

        if(isNetworkAvailable()){
            if (new_user.getText().toString().length() >= 5
                    && new_pass.getText().toString().length() >= 5){
                ParseUser user = new ParseUser();
                user.setUsername(new_user.getText().toString().trim());
                user.setPassword(new_pass.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(sign_up.this ,"signed up successfully", Toast.LENGTH_LONG).show();
                            new_user.getText().clear();
                            new_pass.getText().clear();
                            Intent i = new Intent(sign_up.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(sign_up.this ,"sign up Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            else if (new_user.getText().toString().length() < 5
                        || new_pass.getText().toString().length() < 5){
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Fields need to be more than 5 Characters", Snackbar.LENGTH_LONG);
                Toast.makeText(this, "Fields need to be more than 5 Characters", Toast.LENGTH_SHORT).show();
            }

        }
        else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_LONG).show();
    }

}
