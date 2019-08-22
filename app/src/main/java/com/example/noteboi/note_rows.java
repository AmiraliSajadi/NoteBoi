package com.example.noteboi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class note_rows extends AppCompatActivity {

    RecyclerView my_rv;
    List<RecyclerViewModel> data;
    MyAdapter adapter;
    FloatingActionButton newnote_button;
    View.OnClickListener onItemClickListener;
    TextView my_tv;
    WaveSwipeRefreshLayout wave;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_rows);
        data = new ArrayList<>();
        data.clear();
        newnote_button = findViewById(R.id.newnote_b);
        my_rv = findViewById(R.id.rv);
        my_tv = findViewById(R.id.tv);
        wave = findViewById(R.id.main_swipe);

        //     ***Apache Licence***
        //THE NEW WAVE REFRESH THING RUN
        wave.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                refresh();
            }
        });

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
        onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isNetworkAvailable()){
                   RecyclerView.ViewHolder selected_ViewHolder = (RecyclerView.ViewHolder) v.getTag();
                   int position = selected_ViewHolder.getAdapterPosition();
                   String clicked_id = data.get(position).getId();
                   finish();
                   Intent intent = new Intent(note_rows.this, note_page.class);
                   intent.putExtra("id", clicked_id);
                   startActivity(intent);
               }else {
                   Toast.makeText(note_rows.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
               }
            }
        };
        adapter.setOnItemClickListener(onItemClickListener);

        //filling the Recycler View with objects containing title, memo and id
        refresh();
    }

    public void refresh(){

        if (isNetworkAvailable()){
            my_rv.setVisibility(View.VISIBLE);
            my_tv.setVisibility(View.GONE);
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
                                    obj.getObjectId()));
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(note_rows.this, "Failed to Load Notes", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }else{
            if(currentUser != null){
              Toast.makeText(this, "Couldn't refresh notes", Toast.LENGTH_SHORT).show();
              my_tv.setVisibility(View.VISIBLE);
              my_rv.setVisibility(View.INVISIBLE);
            }
        }
        //TURN OFF WAVE REFRESHING
        wave.setRefreshing(false);

    }

    public void make_note (View view){
        if(isNetworkAvailable()){
            finish();
            Intent n = new Intent(note_rows.this, note_page.class);
            startActivity(n);
        }else Toast.makeText(this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
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
               if(isNetworkAvailable()){
                   currentUser.logOut();
                   Intent i = new Intent(note_rows.this, MainActivity.class);
                   finish();
                   startActivity(i);
               }
               else Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
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