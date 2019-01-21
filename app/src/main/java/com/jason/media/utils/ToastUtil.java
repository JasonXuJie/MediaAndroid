package com.jason.media.utils;

import android.widget.Toast;

import com.jason.media.MyApplication;

/**
 * Created by jason on 2019/1/17.
 */

public class ToastUtil {


    public static void showShort(String msg){
        Toast.makeText(MyApplication.getCtx(),msg,Toast.LENGTH_SHORT).show();
    }


    public static void showLong(String msg){
        Toast.makeText(MyApplication.getCtx(),msg,Toast.LENGTH_LONG).show();
    }
}
