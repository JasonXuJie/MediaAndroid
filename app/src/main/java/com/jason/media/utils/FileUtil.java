package com.jason.media.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jason on 2019/1/21.
 */

public class FileUtil {





    public static boolean existsFile(String path){
        if (TextUtils.isEmpty(path)){
            return false;
        }
        return existsFile(new File(path));
    }



    public static boolean existsFile(File file){
        return file!=null&&file.exists();
    }
}
