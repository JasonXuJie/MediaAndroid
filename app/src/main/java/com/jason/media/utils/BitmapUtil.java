package com.jason.media.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jason on 2019/1/21.
 */

public class BitmapUtil {

    /**
     * 图像的旋转方向是0
     */
    public static final int ROTATE0 = 0;
    /**
     * 图像的旋转方向是90
     */
    public static final int ROTATE90 = 90;
    /**
     * 图像的旋转方向是180
     */
    public static final int ROTATE180 = 180;
    /**
     * 图像的旋转方向是270
     */
    public static final int ROTATE270 = 270;
    /**
     * 图像的旋转方向是360
     */
    public static final int ROTATE360 = 360;
    /**
     * 图片显示最大边的像素
     */
    public static final int MAXLENTH = 1024;


    public static Bitmap file2Bitmap(File file){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeFile(file.getPath(),options);
    }


    public static Bitmap byte2Bitmap(byte[] data){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeByteArray(data,0,data.length,options);
    }


    public static Bitmap file2Bitmap(File file,int degree) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap mBitmap = BitmapFactory.decodeFile(file.getPath(),options);
        bitmap = rotateBitmapByDegree(mBitmap,degree);
        return bitmap;
    }


    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, float degree) {
        Bitmap mBitmap = null;
        Matrix matrix = new Matrix();
        if (degree == 0){
            mBitmap = bitmap;
        }else {
            matrix.setRotate(degree);
            mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return mBitmap;
    }


    /**
     * 解析图片旋转方向
     */
    public static int decodeImageDegree(String path) {
        int degress = ROTATE0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degress = ROTATE90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degress = ROTATE180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degress = ROTATE270;
                    break;
                default:
                    degress = ROTATE0;
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
            degress = ROTATE0;
        }
        return degress;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int decodeImageDegree(InputStream stream) {
        int degress = ROTATE0;
        try {
            ExifInterface exifInterface = new ExifInterface(stream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degress = ROTATE90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degress = ROTATE180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degress = ROTATE270;
                    break;
                default:
                    degress = ROTATE0;
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
            degress = ROTATE0;
        }
        return degress;
    }


}
