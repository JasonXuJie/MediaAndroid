package com.jason.media;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by jason on 2019/1/17.
 */

public class MyApplication extends Application {


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
    }


    public static Context getCtx(){
        return mContext;
    }



    private void init(){
        Logger.addLogAdapter(new AndroidLogAdapter());
    }


}
