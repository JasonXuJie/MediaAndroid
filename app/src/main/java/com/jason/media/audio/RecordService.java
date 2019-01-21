package com.jason.media.audio;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.jason.media.utils.RecordManager;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jason on 2019/1/15.
 */

public class RecordService extends Service {


    private MyBinder binder;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        binder = new MyBinder();
        return binder;
    }



    public static class MyBinder extends Binder {

        private Timer timer;
        private int second = 0;
        private OnTimerCallBack callBack;
        private Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){
                    callBack.recordTime(second);
                }
            }
        };

        public void setOnTimerCallBack(OnTimerCallBack callBack){
                this.callBack = callBack;
        }

        public void start(){
            RecordManager.getInstance().start();
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (callBack!=null){
                        second++;
                        handler.sendEmptyMessage(1);
                        //Logger.e("在运行");
                    }
                }
            };
            timer.schedule(timerTask,0,1000);
        }

        public void stop(){
            RecordManager.getInstance().stop();
            if (timer!=null){
                timer.cancel();
                handler.removeCallbacksAndMessages(null);
                if (callBack!=null){
                    callBack.cancelTime();
                }
            }
        }

        public void exit(){
            if (timer!=null){
                timer.cancel();
                handler.removeCallbacksAndMessages(null);
                removeCallBack();
            }
        }

        private void removeCallBack(){
            callBack = null;
        }

        public interface OnTimerCallBack{
            void recordTime(int second);
            void cancelTime();
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("onCreate");
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (binder!=null){
            binder.exit();
            RecordManager.getInstance().stop();
            Logger.e("onUnbind");
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Logger.e("onDestroy");
        super.onDestroy();
    }
}
