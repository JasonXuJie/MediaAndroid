package com.jason.media.utils;

/**
 * Created by jason on 2018/7/30.
 */

public class ImageData {

    private static ImageData instance;
    private byte[] value;


    public static ImageData getInstance(){
        if (instance==null){
            synchronized (ImageData.class){
                if (instance==null){
                    instance = new ImageData();
                }
            }
        }
        return instance;
    }

    private ImageData(){

    }


    public void setValue(byte[] bytes){
        this.value = bytes;
    }


    public byte[] getValue(){
        return value;
    }
}
