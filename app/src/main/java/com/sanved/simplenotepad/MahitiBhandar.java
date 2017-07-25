package com.sanved.simplenotepad;

/**
 * Created by Sanved on 04-07-2016.
 */
public class MahitiBhandar {

    String name, preview, size;

    public MahitiBhandar(String name, String preview , String size){
        this.name = name;
        this.preview = preview;
        this.size = size;
    }

    public String getName(){
        return name;
    }

    public String getPreview(){
        return preview;
    }

    public String getSize(){
        return size;
    }

}

