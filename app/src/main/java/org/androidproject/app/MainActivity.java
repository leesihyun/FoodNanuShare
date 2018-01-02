package org.androidproject.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static Activity MainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity = MainActivity.this;

        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0)
        {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else
        {
            Intent i = new Intent(this, TabActivity.class);
            startActivity(i);
        }

    }
}
