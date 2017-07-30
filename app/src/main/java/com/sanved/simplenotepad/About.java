package com.sanved.simplenotepad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Sanved on 11-07-2016.
 */
public class About extends AppCompatActivity {

    Button github;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        github = (Button) findViewById(R.id.bGit);

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String link = "https://github.com/sanved77/SimpleNotepad";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);

            }
        });
    }
}