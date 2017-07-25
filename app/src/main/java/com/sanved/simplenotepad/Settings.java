package com.sanved.simplenotepad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.widget.AppCompatRadioButton;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.preference.PreferenceManager;

/**
 * Created by Sanved on 04-07-2016.
 */
public class Settings extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    Toolbar toolbar;
    SwitchCompat sizeOnOff,ss2;
    private AppCompatRadioButton rbop1,rbop2,rbop3,rbop4;
    private RadioGroup answer;
    EditText fontSize;

    SharedPreferences pref, prefs;
    SharedPreferences.Editor ed, ed2;

    private static int fontOn;
    private static boolean sizeStat = true, sizeStat2 = true;
    private static float sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initVals();

    }

    public void initVals(){

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Settings");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Other
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ed2 = prefs.edit();
        pref = getSharedPreferences("datanotepad", MODE_PRIVATE);
        ed = pref.edit();
        fontOn = pref.getInt("fontNum",0);
        sizeStat = prefs.getBoolean("sizeShow",true);
        sizeStat2 = prefs.getBoolean("vibe",true);


        //Widgets
        sizeOnOff = (SwitchCompat) findViewById(R.id.sizeSwitch);
        sizeOnOff.setChecked(sizeStat);
        sizeOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed2.putBoolean("sizeShow",sizeOnOff.isChecked()).commit();
            }
        });

        ss2 = (SwitchCompat) findViewById(R.id.ss2);
        ss2.setChecked(sizeStat2);
        ss2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed2.putBoolean("vibe",ss2.isChecked()).commit();
            }
        });
        answer = (RadioGroup) findViewById(R.id.rgAnswer);
        rbop1 = (AppCompatRadioButton) findViewById(R.id.op1);
        rbop2 = (AppCompatRadioButton) findViewById(R.id.op2);
        rbop3 = (AppCompatRadioButton) findViewById(R.id.op3);
        rbop4 = (AppCompatRadioButton) findViewById(R.id.op4);
        turnOnRadioButton(fontOn);
        answer.setOnCheckedChangeListener(this);
        fontSize = (EditText) findViewById(R.id.etSize);
        float px = fontSize.getTextSize();
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        sp = px/scaledDensity;
        fontSize.setText("" + pref.getFloat("fontSize",sp));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        float fsize = Float.parseFloat(fontSize.getText().toString());

        ed.putFloat("fontSize",fsize).commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.op1:
                fontChanger(0);
                break;
            case R.id.op2:
                fontChanger(1);
                break;
            case R.id.op3:
                fontChanger(2);
                break;
            case R.id.op4:
                fontChanger(3);
                break;
        }
    }

    public void fontChanger(int fontid){
        ed.putInt("fontNum",fontid).commit();
    }

    public void turnOnRadioButton(int num){
        switch(num){
            case 0:
                rbop1.setChecked(true);
                break;
            case 1:
                rbop2.setChecked(true);
                break;
            case 2:
                rbop3.setChecked(true);
                break;
            case 3:
                rbop4.setChecked(true);
                break;
        }
    }
}
