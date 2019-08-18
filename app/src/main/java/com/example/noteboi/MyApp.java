package com.example.noteboi;

import android.app.Application;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;

public class MyApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)

                .server("https://parseapi.back4app.com/")
                .applicationId("tseQKFystB5e9Xchu4ANW0ufdPKvWTNh6D3mYPX0")
                .clientKey("oauyE2RqysVBnwmlyELU6ZKfDhQbGXvQ5iFPooyQ")
                .build());


    }


}
