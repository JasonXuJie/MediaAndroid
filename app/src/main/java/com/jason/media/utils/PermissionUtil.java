package com.jason.media.utils;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by jason on 2019/1/2.
 */

public class PermissionUtil {


    private RxPermissions rxPermissions;

    public PermissionUtil(FragmentActivity activity){
        rxPermissions = new RxPermissions(activity);
    }




    public void requestPermission(final OnPermissionListener listener, String...permission){
        rxPermissions.request(permission).subscribe(granted -> {
            if (granted){
                listener.granted();
            }else {
                listener.denied();
            }
        });
    }


    public interface OnPermissionListener{
        void granted();
        void denied();
    }
}
