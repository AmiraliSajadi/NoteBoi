package com.example.noteboi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;
import dmax.dialog.SpotsDialog;

public class note_rows extends AppCompatActivity {

    RecyclerView my_rv;
    List<RecyclerViewModel> data;
    MyAdapter adapter;
    FloatingActionButton newnote_button;
    TextView tv,my_no_note_tv;
    SwipeRefreshLayout swipe;
    ParseUser currentUser;
    android.app.AlertDialog dialog;
    SwipeToAction swipeToAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_rows);
        data = new ArrayList<>();
        data.clear();
        newnote_button = findViewById(R.id.newnote_b);
        my_rv = findViewById(R.id.rv);
        tv = findViewById(R.id.tv);
        my_no_note_tv = findViewById(R.id.no_note_tv);

        //making the "no note view" go away until we need to make it visible
        my_no_note_tv.setVisibility(View.GONE);

        //swipe to refresh
        swipe = findViewById(R.id.srLayout);
        swipe.setColorSchemeResources(R.color.colorAccent);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        ParseACL.setDefaultACL(new ParseACL(), true);


        //checking current user
            currentUser = ParseUser.getCurrentUser();
            if (currentUser == null){
                Intent i = new Intent(note_rows.this, MainActivity.class);
                startActivity(i);
            }

        //Here i make an object form my adapter (for the Recycler view)
        adapter = new MyAdapter(data);
        my_rv.setAdapter(adapter);
        my_rv.setLayoutManager( new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
              false
                ));

        //recycler view onClick / onSwipe
        swipeToAction = new SwipeToAction(my_rv, new SwipeToAction.SwipeListener<RecyclerViewModel>() {
            @Override
            public boolean swipeLeft(final RecyclerViewModel itemData) {
               if(isNetworkAvailable()){
                   AlertDialog.Builder builder = new AlertDialog.Builder(note_rows.this);
                   builder.setMessage("Are you sure you want to delete this note?")
                           .setTitle("Delete")
                           .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
                                   query.getInBackground(itemData.getId(), new GetCallback<ParseObject>() {
                                       @Override
                                       public void done(ParseObject object, ParseException e) {
                                           if (e == null) {
                                               object.deleteInBackground();
                                               View parentLayout = findViewById(android.R.id.content);
                                               Snackbar.make(parentLayout,"Note Deleted",Snackbar.LENGTH_SHORT).show();
                                               refresh();
                                           } else {
                                               View parentLayout = findViewById(android.R.id.content);
                                               Snackbar.make(parentLayout,"Failed to Delete Note",Snackbar.LENGTH_SHORT).show();
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
               else Toast.makeText(note_rows.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean swipeRight(final RecyclerViewModel itemData) {
                if (isNetworkAvailable()){
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
                    query.getInBackground(itemData.getId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(e == null) {
                                //this isn't working maybe because of the permission
                                object.put("fav", true);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            View parentLayout = findViewById(android.R.id.content);
                                            Snackbar.make(parentLayout, "Note added to Favorites", Snackbar.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(note_rows.this, "Failed to add note to favorites", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }
                else Toast.makeText(note_rows.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onClick(RecyclerViewModel itemData) {
                if(isNetworkAvailable()){
                    String clicked_id = itemData.getId();
                    finish();
                    Intent intent = new Intent(note_rows.this, note_page.class);
                    intent.putExtra("id", clicked_id);
                    startActivity(intent);
                }else {
                    Toast.makeText(note_rows.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(RecyclerViewModel itemData) {
            }

        });

        //setting the no connection tv invisible/visible
        if (isNetworkAvailable()) {
            tv.setVisibility(View.GONE);
        }
        else {
            my_rv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }

        //filling the Recycler View with objects containing title, memo and id
        refresh();
    }

    public void refresh(){

        if (isNetworkAvailable()){
            my_rv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("notes");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        data.clear();
                        for( ParseObject obj : objects){
                            data.add(new RecyclerViewModel(
                                    obj.getString("title"),
                                    obj.getString("memo"),
                                    obj.getBoolean("fav"),
                                    obj.getObjectId()
                                   ));
                        }
                        adapter.notifyDataSetChanged();
                        if(data.isEmpty()){
                            //make no note view visible
                            my_no_note_tv.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Toast.makeText(note_rows.this, "Failed to Load Notes", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }else{
            if(currentUser != null){
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout,"No Internet Connection\nCheck Your Connection and Swipe to Refresh",Snackbar.LENGTH_LONG).show();
            }
        }
        swipe.setRefreshing(false);

    }

    public void make_note (View view) {
        if (isNetworkAvailable()) {
            Intent n = new Intent(note_rows.this, note_page.class);
            finish();
            startActivity(n);
        } else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_row_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
                dialog = new SpotsDialog.Builder()
                        .setContext(this)
                        .setMessage("Loading note")
                        .setTheme(R.style.loading_dialog)
                        .setCancelable(false)
                        .build();
                dialog.show();

                currentUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Toast.makeText(note_rows.this, "Failed to log out", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent i = new Intent(note_rows.this, MainActivity.class);
                finish();
                startActivity(i);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(ParseUser.getCurrentUser() != null){
        invalidateOptionsMenu();
        menu.findItem(R.id.current_user).setTitle( "User: " + ParseUser.getCurrentUser().getUsername());
        }
        return true;
    }

    //making the back button take you to home and add the activity to stack
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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