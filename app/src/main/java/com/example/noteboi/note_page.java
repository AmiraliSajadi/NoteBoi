package com.example.noteboi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noteboi.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class note_page extends AppCompatActivity {

    Button my_save;
    EditText my_memo, my_title;
    String selected_id, current_title, current_memo;

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

        //Filling the title and memo for already existing notes
        ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
        query.whereEqualTo("objectId", selected_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                  for(ParseObject obj : objects) {
                      my_title.setText(obj.getString("title"));
                      my_memo.setText(obj.getString("memo"));
                  }
                }
                else {
                    Toast.makeText(note_page.this, "Failed to read and fill", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void save_note(View view){

        current_title= my_title.getText().toString();
        current_memo = my_memo.getText().toString();

        //make an object in parse server (new note)
        if (selected_id == null) {
            ParseObject new_object = new ParseObject("notes");
            new_object.put("title", current_title);
            new_object.put("memo", current_memo);
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
        }
        //updating a note
        else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
            query.getInBackground(selected_id, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // Now let's update the object with some new data.
                        object.put("title", current_title);
                        object.put("memo", current_memo);
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

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        if (selected_id != null) return true;
        else return false;
    }

    // Deleting note
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_button:
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

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            Intent intent = new Intent(note_page.this, note_rows.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
