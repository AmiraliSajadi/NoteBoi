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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    EditText my_user,my_pass;
    Button sing_in;
    android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_user = findViewById(R.id.ed_user);
        my_pass = findViewById(R.id.ed_pass);
        sing_in = findViewById(R.id.b_singin);
    }

    public void sign_in(View view){

        final String username = my_user.getText().toString();

        if(isNetworkAvailable()){

            //show logging in dialog
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Logging in")
                    .setTheme(R.style.loading_dialog)
                    .setCancelable(false)
                    .build();
            dialog.show();

            ParseUser.logInInBackground(my_user.getText().toString().trim(),my_pass.getText().toString(),new LogInCallback() {
               public void done(ParseUser user, ParseException e) {
                   if (user != null) {
                    Toast.makeText(MainActivity.this, String.format(Locale.getDefault(),"Hi %s", username), Toast.LENGTH_SHORT).show();
                    my_pass.getText().clear();
                    my_user.getText().clear();
                    Intent i = new Intent(MainActivity.this, note_rows.class);
                    finish();
                    startActivity(i);
                   } else {
                    Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                   }
                   dialog.dismiss();

               }
            });
        }

        else {

            Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_LONG).show();
        }

    }

    public void signup_page(View view){

        Intent i = new Intent(this, sign_up.class);
        startActivity(i);
    }

    private boolean isNetworkAvailable(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else return false;

    }

    //making the back button take you to home and add the activity to stack
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    //takes user to forgot pass activity
    public void to_forgot(View view){
        Intent i = new Intent(MainActivity.this,forgot.class);
        startActivity(i);
    }

}
