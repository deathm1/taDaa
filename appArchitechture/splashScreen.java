package com.koshurTech.tadaa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.koshurTech.tadaa.auth.googleSignIn;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        checkTheme();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), googleSignIn.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }
        },300);
    }


    private void checkTheme() {
        SharedPreferences sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(sp.getBoolean("theme", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }
}