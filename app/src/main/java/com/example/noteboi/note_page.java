package com.example.noteboi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class note_page extends AppCompatActivity {

    boolean fav_stat, new_fav_stat;
    Button my_save;
    EditText my_memo, my_title;
    String selected_id, current_title, current_memo, past_title, past_memo, rotate_memo, rotate_title;
    android.app.AlertDialog dialog;
    MenuItem heart, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);

        my_save = findViewById(R.id.savenote_b);
        my_memo = findViewById(R.id.ed_memo);
        my_title = findViewById(R.id.ed_title);
        Intent n = getIntent();
        selected_id = n.getStringExtra("id");

        //setting the ACL to specific user
        ParseACL.setDefaultACL(new ParseACL(), true);

        if(savedInstanceState == null){
          //show loading dialog for pre existing notes
          if (selected_id != null){
              dialog = new SpotsDialog.Builder()
                      .setContext(this)
                      .setMessage("Loading note")
                      .setTheme(R.style.loading_dialog)
                      .setCancelable(false)
                      .build();
              dialog.show();
              //dismiss the dialog after 10s automatically
              final Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      dialog.dismiss();
                  }
              }, 10000);

              //Filling the title and memo for already existing notes
              ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
              query.whereEqualTo("objectId", selected_id);
              query.findInBackground(new FindCallback<ParseObject>() {
                  @Override
                  public void done(List<ParseObject> objects, ParseException e) {
                      if(e == null){
                          for(ParseObject obj : objects) {
                              past_memo = obj.getString("memo");
                              past_title = obj.getString("title");
                              my_title.setText(past_title);
                              my_memo.setText(past_memo);
                              dialog.dismiss();
                          }
                      }
                      else {
                          Toast.makeText(note_page.this, "Try again later", Toast.LENGTH_SHORT).show();
                          dialog.dismiss();
                      }
                  }
              });
          }
      }
    }

    public void save_note(){

        current_title= my_title.getText().toString();
        current_memo = my_memo.getText().toString();

        //show saving dialog
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Saving note")
                .setTheme(R.style.loading_dialog)
                .setCancelable(false)
                .build();
        dialog.show();
        //dismiss the dialog after 10s automatically
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 10000);

        //make an object in parse server (new note)
        if (selected_id == null) {
            ParseObject new_object = new ParseObject("notes");
            new_object.put("title", current_title);
            new_object.put("memo", current_memo);
//            you have to change the false of fav below to what the user wants
            new_object.put("fav",new_fav_stat);
            new_object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(note_page.this, "Note Saved", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(note_page.this, note_rows.class);
                        finish();
                        startActivity(i);

                    } else {
                        Toast.makeText(note_page.this, "Failed to Save the Note", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.dismiss();
        }
        //updating a note
        else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
            //show saving dialog
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Saving note")
                    .setTheme(R.style.loading_dialog)
                    .setCancelable(false)
                    .build();
            dialog.show();
            query.getInBackground(selected_id, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // Now let's update the object with some new data.
                        object.put("title", current_title);
                        object.put("memo", current_memo);
                        object.put("fav",new_fav_stat);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    Intent i = new Intent(note_page.this, note_rows.class);
                                    finish();
                                    startActivity(i);
                                    Toast.makeText(note_page.this, "Note Saved", Toast.LENGTH_SHORT).show();
                                }
                                else Toast.makeText(note_page.this, "Failed to Save the Note", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else Toast.makeText(note_page.this, "Failed to Save the Note", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();

        }

    }

    public void click_save_note(View view){
       if(isNetworkAvailable()){
           save_note();
       }
       else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
    }

    //save or discard or cancel dialog
    public void sd_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save your changes or discard them?")
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        save_note();
                    }
                })
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(note_page.this, note_rows.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        heart = menu.findItem(R.id.fav_in_page);
        delete = menu.findItem(R.id.delete_button);
        if (selected_id != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
            query.whereEqualTo("objectId", selected_id);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    for(ParseObject obj : objects) {
                        fav_stat = obj.getBoolean("fav");
                        new_fav_stat = fav_stat;
                    }
                    if(!fav_stat) heart.setIcon(R.drawable.ic_favorite_border_white_24dp);
                }
            });
            return true;
        }
        else {
            delete.setVisible(false);
            heart.setIcon(R.drawable.ic_favorite_border_white_24dp);
             return true;
        }
    }

    // Deleting/fav note
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav_in_page:
                if(isNetworkAvailable()){
                    if(selected_id != null){
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
                        query.getInBackground(selected_id, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null){
                                    if(!new_fav_stat){
                                        new_fav_stat = true;
                                        heart.setIcon(R.drawable.ic_favorite_white_24dp);
                                    }else {
                                        new_fav_stat = false;
                                        heart.setIcon(R.drawable.ic_favorite_border_white_24dp);
                                    }
                                }
                                else Toast.makeText(note_page.this, "Failed to add note to favorites", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        if(!new_fav_stat) {
                            heart.setIcon(R.drawable.ic_favorite_white_24dp);
                            new_fav_stat = true;
                        }
                        else {
                            heart.setIcon(R.drawable.ic_favorite_border_white_24dp);
                            new_fav_stat = false;
                        }
                    }
                } else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                break;

            case R.id.delete_button:
                if(isNetworkAvailable()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to delete this note?")
                            .setTitle("Delete")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
                                    query.getInBackground(selected_id, new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null) {
                                                object.deleteInBackground();
                                                Toast.makeText(note_page.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(note_page.this, note_rows.class);
                                                finish();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(note_page.this, "Failed to Delete Note", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
    return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            current_title= my_title.getText().toString();
            current_memo = my_memo.getText().toString();

            //show the dialog if they changed a pre existing note
            if(selected_id != null && !my_memo.getText().toString().equals(current_memo) ) sd_dialog();
            else if(selected_id != null && !my_title.getText().toString().equals(current_title)) sd_dialog();

            //show the dialog if they made a new note && put something in it
            else if(selected_id == null && !current_title.isEmpty()) sd_dialog();
            else if(selected_id == null && !current_memo.isEmpty()) sd_dialog();
            else if(selected_id != null && new_fav_stat!=fav_stat) sd_dialog();
            //otherwise don't show the dialog and take them back
            else {
                Intent intent = new Intent(note_page.this, note_rows.class);
                startActivity(intent);
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetworkAvailable(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else return false;
    }
}




