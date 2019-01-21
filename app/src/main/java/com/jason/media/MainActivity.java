package com.jason.media;

import android.view.View;

import com.jason.media.audio.AudioActivity;
import com.jason.media.base.BaseActivity;
import com.jason.media.image.ImageActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @OnClick({R.id.btn_image,R.id.btn_audio,R.id.btn_video})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_image:
                openActivityByNoParams(ImageActivity.class);
                break;
            case R.id.btn_audio:
                openActivityByNoParams(AudioActivity.class);
                break;
            case R.id.btn_video:
                break;
                default:
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void requestData() {

    }
}
