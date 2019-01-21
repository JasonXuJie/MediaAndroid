package com.jason.media;


import android.animation.Animator;

import com.airbnb.lottie.LottieAnimationView;
import com.jason.media.base.BaseActivity;
import com.jason.media.config.FileConfig;
import com.jason.media.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;

/**
 * Created by jason on 2019/1/17.
 */

public class SplashActivity extends BaseActivity {


    @BindView(R.id.lottieView)
    LottieAnimationView lottieView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews() {
        createRootFile();
        lottieView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                openActivityByNoParams(MainActivity.class);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void createRootFile(){
        File file = new File(FileConfig.ROOT);
        boolean isExist = FileUtil.existsFile(file);
        Logger.e(isExist+"");
        if (!isExist){
            file.mkdirs();
        }
    }

    @Override
    public void requestData() {

    }


    @Override
    protected void onDestroy() {
        lottieView.clearAnimation();
        super.onDestroy();
    }
}
