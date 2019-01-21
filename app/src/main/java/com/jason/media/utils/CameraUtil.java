package com.jason.media.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jason on 2018/7/3.
 */

public class CameraUtil {

    private static CameraUtil instance;
    private Context context;

    public static CameraUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (CameraUtil.class) {
                if (instance == null) {
                    instance = new CameraUtil(context);
                }
            }
        }
        return instance;
    }

    private CameraUtil(Context context) {
        this.context = context;
    }

    //判断相机是否支持相机硬件
    public boolean checkCameraHardWare() {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    //启动相机(0为后置，1为前置)
    public Camera getCamera(int cameraId) {
        Camera camera;
        if (Camera.getNumberOfCameras() >= 2) {
            camera = Camera.open(cameraId);
        } else {
            camera = Camera.open();
        }
        return camera;
    }


    //获取相机的对焦方式
    public String getCameraFocusable(Camera.Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            return Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        } else if (focusModes.contains((Camera.Parameters.FOCUS_MODE_AUTO))) {
            return Camera.Parameters.FOCUS_MODE_AUTO;
        }
        return null;
    }

    /**
     * 根据比例得到合适的尺寸的最大尺寸
     */
    public static Camera.Size getProperSize4Ratio(List<Camera.Size> sizeList, float displayRatio) {
        Collections.sort(sizeList, new SizeL2hComparator());
        Camera.Size result = null;
        for (Camera.Size size : sizeList) {
            float curRatio = ((float) size.width) / size.height;
            if (curRatio == displayRatio) {
                result = size;
            }
        }

        if (null == result) {
            for (Camera.Size size : sizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 3f / 4) {
                    result = size;
                }
            }
        }
        return result;
    }


    /**
     * 根据宽度得到最大合适的尺寸
     *
     * @param sizeList
     * @param Width
     * @return
     */
    public static Camera.Size getMaxSize4Width(List<Camera.Size> sizeList, int Width) {
        // 先对传进来的size列表进行排序
        Collections.sort(sizeList, new SizeL2hComparator());
        Camera.Size result = null;
        for (Camera.Size size : sizeList) {
            if (size.height == Width) {
                result = size;
            }
        }
        return result;
    }


    /**
     * 获取支持的最大尺寸
     */
    public static Camera.Size getMaxSize(List<Camera.Size> sizeList) {
        // 先对传进来的size列表进行排序
        Collections.sort(sizeList, new SizeL2hComparator());
        Camera.Size result = null;
        if (sizeList != null && !sizeList.isEmpty()) {
            result = sizeList.get(sizeList.size() - 1);
        }
        return result;
    }

    /**
     * 从小到大排序
     */
    private static class SizeL2hComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            if (size1.width < size2.width) {
                return -1;
            } else if (size1.width > size2.width) {
                return 1;
            }
            return 0;
        }
    }

    public static int getRecorderRotation(int cameraId){
        Camera.CameraInfo info = new                 Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }


}
