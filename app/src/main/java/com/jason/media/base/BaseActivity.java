package com.jason.media.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jason on 2019/1/17.
 */

public abstract class BaseActivity extends AppCompatActivity {


    private Unbinder unbinder;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        initViews();
        requestData();
    }




    public abstract int getLayoutId();

    public abstract void initViews();

    public abstract void requestData();



    public void openActivityByParams(Class<?> cls, Bundle bundle){
        Intent intent = new Intent(this,cls);
        if (bundle!=null) intent.putExtras(bundle);
        startActivity(intent);
    }


    public void openActivityByNoParams(Class<?> cls){
        openActivityByParams(cls,null);
    }

    public void openActivityByParamsForResult(Class<?> cls, Bundle bundle, int requestCode){
        Intent intent = new Intent(this,cls);
        if (bundle!=null)intent.putExtras(bundle);
        startActivityForResult(intent,requestCode);
    }

    public void openActivityByNoParamsForResult(Class<?> cls, int requsetCode){
        openActivityByParamsForResult(cls,null,requsetCode);
    }







    @Override
    protected void onDestroy() {
        if (unbinder!=null) unbinder.unbind();
        super.onDestroy();
    }
}
