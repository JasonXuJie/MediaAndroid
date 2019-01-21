package com.jason.media.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by jason on 2019/1/21.
 */

public class FileConfig {

    public static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"MediaAndroid";

    public static final String IMAGE = "image.jpg";

}
