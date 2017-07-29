package com.sanved.simplenotepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Created by Sanved on 02-07-2016.
 */
public class NotepadActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText notepad;
    FloatingActionButton fab;
    Vibrator v;

    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    ArrayList<String> fonts;

    private static String name = "Notepad";
    private static String path;
    private static int num = 0;
    private int textCount = 0;

    File file;

    private static boolean mExternalStorageAvailable = false, mExternalStorageWriteable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_screen);

        //Gathering data
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            name = extras.getString("name");
            num = extras.getInt("num");
            path = extras.getString("path");
        } else {
            name = (String) savedInstanceState.getSerializable("name");
            num = savedInstanceState.getInt("num");
            path = (String) savedInstanceState.getSerializable("path");
        }

        initVals();
        File storage = new File(Environment.getExternalStorageDirectory()
                + "/SimpleNotepad");
        if (!storage.exists()) storage.mkdirs();


        //Engine
        if(path.contains("null")){
            file = new File(storage, name + ".txt");
            saveFile(true);
        }else{
            file = new File(path);
            readFile();
        }

        //todo add the tour guide here
    }

    @Override
    protected void onResume() {
        super.onResume();
        int fontNum = prefs.getInt("fontNum",0);
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/" + fonts.get(fontNum));
        notepad.setTypeface(myFont);

        float fontSize = prefs.getFloat("fontSize",18);
        notepad.setTextSize(fontSize);

    }

    public void initVals(){

        fonts = new ArrayList<>();

        fonts.add("arial.ttf");
        fonts.add("calibri.ttf");
        fonts.add("comic.ttf");
        fonts.add("consola.ttf");

        prefs = getSharedPreferences("datanotepad", MODE_PRIVATE);
        ed = prefs.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle(name);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfShouldSave();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.udtahuyaButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile(true);
            }
        });

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        notepad = (EditText) findViewById(R.id.etnotepad);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            checkIfShouldSave();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkExternalMedia(){

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public void saveFile(boolean vibrateAndToast){
        checkExternalMedia();

        if(mExternalStorageAvailable && mExternalStorageWriteable) {

            //Setting files

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println(notepad.getText().toString());
                pw.flush();
                pw.close();
                f.close();
                if(vibrateAndToast) {
                    Toast.makeText(NotepadActivity.this, "File Saved", Toast.LENGTH_SHORT).show();
                    boolean abc = prefs.getBoolean("vibe",true);
                    if(abc) v.vibrate(200);
                }
                double size = file.length();
                ed.putString("name"+num,name);
                ed.putString("size"+num,""+size+" bytes");
                if(notepad.getText().length() >= 150){
                    String preview = notepad.getText().toString().substring(0,147) + "...";
                    ed.putString("preview"+num, preview);
                }else{
                    String preview = notepad.getText().toString();
                    ed.putString("preview"+num, preview);
                }
                ed.putString("path"+num,""+file.getCanonicalPath());
                Log.e(file.getAbsolutePath(), file.getCanonicalPath());
                textCount = notepad.length();
                ed.commit();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Padmarichalo", "******* File not found. ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(NotepadActivity.this, "Can't save, Access Denied", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteFile(){
        File storage = new File(Environment.getExternalStorageDirectory()
                + "/SimpleNotepad"+ name + ".txt");
        storage.deleteOnExit();
        Toast.makeText(NotepadActivity.this, "File Deleted", Toast.LENGTH_SHORT).show();
        v.vibrate(200);
        finish();
        /*if(deleted) {
            Toast.makeText(NotepadActivity.this, "File Deleted", Toast.LENGTH_SHORT).show();
            v.vibrate(200);
        } else{
            Toast.makeText(NotepadActivity.this, "File NOT Deleted", Toast.LENGTH_SHORT).show();
        }*/

    }

    public void readFile(){
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        notepad.setText(text.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notepad, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.save:
                saveFile(true);
                break;
            case R.id.clear:
                AlertDialog.Builder build = new AlertDialog.Builder(NotepadActivity.this);
                build
                        .setTitle("Clear")
                        .setMessage("Do you want to clear the entire file?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notepad.setText("");
                                saveFile(false);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);

                build.create().show();
                break;
            case R.id.send:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, notepad.getText().toString() );
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textCount != notepad.length()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Save File")
                    .setMessage("Content not saved, do you want to save the file?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveFile(true);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }

    public void checkIfShouldSave() {
        if(textCount != notepad.length()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Save File")
                    .setMessage("Content not saved, do you want to save the file?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveFile(true);
                            boolean fUse = prefs.getBoolean("fUse", true);

                            if(fUse){
                                Toast.makeText(NotepadActivity.this, "Long-click the file for more options", Toast.LENGTH_SHORT).show();
                                ed.putBoolean("fUse", false);
                                ed.commit();
                            }
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            boolean fUse = prefs.getBoolean("fUse", true);

                            if(fUse){
                                Toast.makeText(NotepadActivity.this, "Long-click the file for more options", Toast.LENGTH_SHORT).show();
                                ed.putBoolean("fUse", false);
                                ed.commit();
                            }
                            finish();
                        }
                    });
            builder.show();
        }else{
            boolean fUse = prefs.getBoolean("fUse", true);

            if(fUse){
                Toast.makeText(NotepadActivity.this, "Long-click the file for more options", Toast.LENGTH_LONG).show();
                ed.putBoolean("fUse", false);
                ed.commit();
            }
            finish();
        }
    }
}
