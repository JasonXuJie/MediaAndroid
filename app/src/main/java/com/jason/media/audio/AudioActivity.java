package com.jason.media.audio;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import com.jason.media.R;
import com.jason.media.base.BaseActivity;
import com.jason.media.utils.AudioTrackManager;
import com.jason.media.utils.DateUtil;
import com.jason.media.utils.PermissionUtil;
import com.jason.media.utils.RecordManager;
import com.jason.media.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jason on 2019/1/17.
 */

public class AudioActivity extends BaseActivity {


    @BindView(R.id.tv_audio_time)
    TextView tv_audio_time;
    @OnClick({R.id.btn_audio_start,R.id.btn_audio_stop,R.id.btn_audio_wav,R.id.btn_audio_play})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_audio_start:
                PermissionUtil permissionUtil = new PermissionUtil(this);
                permissionUtil.requestPermission(new PermissionUtil.OnPermissionListener() {
                    @Override
                    public void granted() {
                        isBind = true;
                        Intent intent = new Intent(AudioActivity.this,RecordService.class);
                        bindService(intent,connection,BIND_AUTO_CREATE);
                    }

                    @Override
                    public void denied() {
                        ToastUtil.showShort("请先开启权限");
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO);
                break;
            case R.id.btn_audio_stop:
                if (myBinder!=null){
                    myBinder.stop();
                }else {
                    ToastUtil.showShort("请先开始");
                }
                break;
            case R.id.btn_audio_wav:
                if (isFinished){
                    RecordManager.getInstance().pcm2wav();
                }else {
                    ToastUtil.showShort("请先结束录音");
                }
                break;
            case R.id.btn_audio_play:
                if (isFinished){
                    AudioTrackManager.getInstance().startPlay(RecordManager.getInstance().getWavPath());
                }else {
                    ToastUtil.showShort("请先结束录音");
                }
                break;
        }
    }

    private boolean isBind = false;
    private RecordService.MyBinder myBinder;
    private boolean isFinished = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_audio;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void requestData() {

    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.e("onServiceConnected");
            myBinder = (RecordService.MyBinder) service;
            myBinder.start();
            myBinder.setOnTimerCallBack(new RecordService.MyBinder.OnTimerCallBack() {
                @Override
                public void recordTime(int second) {
                    tv_audio_time.setText(DateUtil.transTime(second));
                }

                @Override
                public void cancelTime() {
                    isFinished = true;
                    tv_audio_time.setText("录制结束");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onDestroy() {
        if (isBind){
            unbindService(connection);
            connection = null;
        }
        AudioTrackManager.getInstance().stopPlay();
        super.onDestroy();
    }
}
