package com.sanved.simplenotepad;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class StartScreen extends AppCompatActivity {

    RecyclerView rv;
    Toolbar toolbar;
    FloatingActionButton fab;
    TextView tvNoti;
    Vibrator v;

    ArrayList<MahitiBhandar> list;

    SharedPreferences pref;
    SharedPreferences.Editor ed;

    RVAdapt adapt;

    private static boolean mExternalStorageAvailable = false, mExternalStorageWriteable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        //Setup app prerequisites
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            requestPermissions(new String[]{Manifest.permission.VIBRATE}, 200);
        }

        initVals();

        Context con = StartScreen.this.getApplication();
        adapt = new RVAdapt(list,con);
        rv.setAdapter(adapt);

        if (adapt.getItemCount() == 0) tvNoti.setVisibility(View.VISIBLE);

        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(StartScreen.this, NotepadActivity.class);
                int pos = position + 1;
                i.putExtra("name", pref.getString("name"+pos,""));
                i.putExtra("num", pos);
                i.putExtra("path",pref.getString("path"+pos,""));
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final CharSequence[] items = {"Open", "Rename" ,"Delete", "Share"};

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0://  Open/Edit
                                Intent i = new Intent(StartScreen.this, NotepadActivity.class);
                                int pos = position + 1;
                                i.putExtra("name", pref.getString("name"+pos,""));
                                i.putExtra("num", pos);
                                i.putExtra("path",pref.getString("path"+pos,""));
                                startActivity(i);
                                break;
                            case 1:// Rename
                                renameFileStarter(position);
                                break;
                            case 2: // Delete
                                AlertDialog.Builder builder = new AlertDialog.Builder(StartScreen.this);
                                builder.setMessage("Are you sure you want to Delete the File ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                deleteFile(position);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                break;
                            case 3:
                                int pos2 = position + 1;
                                String path = pref.getString("path"+pos2,"");
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
                                startActivity(Intent.createChooser(intent, ""));
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }));

        makeData();

        new MaterialShowcaseView.Builder(this)
                .setTarget(fab)
                .setDismissText("OK")
                .setDismissTextColor(Color.GREEN)
                .setDismissOnTargetTouch(true)
                .setContentText("This button is for creating a new file")
                .setShapePadding(100)
                //.setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse("6968") // provide a unique ID used to ensure it is only shown once
                .show();
    }

    public void initVals(){
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        pref = getSharedPreferences("datanotepad", MODE_PRIVATE);
        ed = pref.edit();
        list = new ArrayList<>();
        tvNoti = (TextView) findViewById(R.id.tvEmp);

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Simple Notepad");
        fab = (FloatingActionButton) findViewById(R.id.udtahuyaButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile();
            }
        });
    }

    public void makeData(){
        int key = pref.getInt("keyN",0);

        list.clear();

        for(int i = 1; i <= key; i++){
            String name = pref.getString("name"+i,"");
            String preview = pref.getString("preview"+i,"");
            String size = pref.getString("size"+i,"");

            MahitiBhandar bhandar = new MahitiBhandar(name,preview,size);
            list.add(bhandar);
        }

        //sortList();

        adapt.notifyDataSetChanged();

        if (adapt.getItemCount() != 0) tvNoti.setVisibility(View.GONE);
        else tvNoti.setVisibility(View.VISIBLE);
    }

    public void sortList(){

        int i,j,cmp;
        String tname,tpreview,tsize;

        for(i=0;i<list.size();i++){

            for(j=0;j<list.size();j++){

                cmp = list.get(i).name.compareToIgnoreCase(list.get(j).name);

                if(cmp > 0){

                    // get(i) is alphabetically greater
                    // swapping values

                    tname = list.get(i).name;
                    tpreview = list.get(i).preview;
                    tsize = list.get(i).size;

                    list.get(i).name = list.get(j).name;
                    list.get(i).preview = list.get(j).preview;
                    list.get(i).size = list.get(j).size;

                    list.get(j).name = tname;
                    list.get(j).preview = tpreview;
                    list.get(j).size = tsize;

                }

            }

        }

    }

    public void createFile(){
        AlertDialog.Builder build = new AlertDialog.Builder(StartScreen.this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);

        final EditText bug = (EditText) dialogView.findViewById(R.id.etBug);

        build
                .setTitle("New Text File")
                .setView(dialogView)
                .setMessage("What would you like to name the new Text File ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(StartScreen.this, NotepadActivity.class);
                        i.putExtra("name",bug.getText().toString());
                        int keyN = pref.getInt("keyN", 999);
                        if(keyN == 999) {
                            ed.putInt("keyN",1).commit();
                            keyN = 1;
                        }else {
                            keyN++;
                            ed.putInt("keyN",keyN).commit();
                        }

                        i.putExtra("num",keyN);
                        i.putExtra("path","null");
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        build.create().show();
    }

    public void deleteFile(int position){
        /*mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Delete")
                .build());*/

        int ct, npp;
        ct = pref.getInt("keyN", 0);
        position++;

        File file = new File(pref.getString("path"+position,""));
        file.delete();

        if(position == ct){
            ed.putInt("keyN", --ct);
            ed.commit();
        }else{
            int n;
            for (n = position; n <= ct; n++) {
                npp = n + 1;
                ed.putString("name" + n, pref.getString("name" + npp, ""));
                ed.putString("size" + n, pref.getString("size" + npp, ""));
                ed.putString("preview" + n, pref.getString("preview" + npp, ""));
                ed.putString("path" + n, pref.getString("path" + npp, ""));
                ed.commit();
            }
            ed.putInt("keyN", --ct);
            ed.commit();
        }

        Toast.makeText(StartScreen.this, "File Delete", Toast.LENGTH_SHORT).show();
        boolean abc = pref.getBoolean("vibe",true);
        if(abc) v.vibrate(200);

        makeData();
    }

    public void renameFileStarter(int pos){
        final int position = pos;
        AlertDialog.Builder builder2 = new AlertDialog.Builder(StartScreen.this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);

        final EditText bug = (EditText) dialogView.findViewById(R.id.etBug);
        builder2.setMessage("Enter the new name for this file?")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        renameFile(position, bug.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert2 = builder2.create();
        alert2.show();
    }

    public void renameFile(int position, String newName){
        position++;
        File file = new File(pref.getString("path"+position,""));
        File storage = new File(Environment.getExternalStorageDirectory()
                + "/SimpleNotepad/" + newName + ".txt");

        String data = readFile(file);
        boolean status = writeToFile(storage, data);

        if(status){
            ed.putString("name"+position, newName);
            try {
                ed.putString("path" + position, storage.getCanonicalPath());
            }catch (IOException ie){
                ie.printStackTrace();
            }
            ed.commit();
            boolean de = file.delete();
            if(de) Log.e("Deleted","");
            else Log.e("Not Deleted","");
            makeData();
        }
    }

    public String readFile(File file){
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
            Toast.makeText(StartScreen.this, "Can't rename, Access Denied", Toast.LENGTH_SHORT).show();
        }
        return text.toString();
    }

    public boolean writeToFile(File file, String data){
        checkExternalMedia();

        boolean status;

        if(mExternalStorageAvailable && mExternalStorageWriteable) {

            //Setting files

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println(data);
                pw.flush();
                pw.close();
                f.close();
                status = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Padmarichalo", "******* File not found. ");
                status = false;
            } catch (IOException e) {
                e.printStackTrace();
                status = false;
            }

        }else{
            Toast.makeText(StartScreen.this, "Can't save, Access Denied", Toast.LENGTH_SHORT).show();
            status = false;
        }
        return status;
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.create:
                createFile();
                break;

            case R.id.settings:
                Intent in = new Intent(StartScreen.this, Settings.class);
                startActivity(in);
                break;

            case R.id.about:
                Intent in2 = new Intent(StartScreen.this, About.class);
                startActivity(in2);
                break;
        }
        return super.onOptionsItemSelected(item);
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
}

