package com.koshurTech.tadaa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class aboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Toolbar toolbarTop = (Toolbar) findViewById(R.id.aboutTb);

        final TextView tbtv = (TextView) findViewById(R.id.aboutTbText);
        toolbarTop.setTitle("");

        setSupportActionBar(toolbarTop);

        toolbarTop.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(aboutActivity.this);
            }
        });



    }
}