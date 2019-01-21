package com.jason.media;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.jason.media.base.BaseActivity;
import com.jason.media.utils.CameraUtil;
import com.jason.media.utils.ImageData;
import com.jason.media.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jason on 2018/7/3.
 */

public class CameraActivity extends BaseActivity {


    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.img_take_picture)
    ImageView img_take_picture;
    @OnClick({R.id.img_back, R.id.img_take_picture})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                ImageData.getInstance().setValue(null);
                finish();
                break;
            case R.id.img_take_picture:
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (data != null) {
                            ImageData.getInstance().setValue(data);
                            finish();
                        }
                    }
                });
                break;
        }
    }

    private int flag = 0;
    private Camera camera;
    private SurfaceHolder holder;
    private int mScreenWidth;
    private int mScreenHeight;



    @Override
    public int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    public void initViews() {
        Bundle bundle = getIntent().getExtras();
        getScreenMetrix();
        if (bundle != null) {
            flag = bundle.getInt("flag", 1);
        }
        holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFormat(PixelFormat.TRANSLUCENT);
        holder.addCallback(callback);
    }

    @Override
    public void requestData() {

    }


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Logger.e("surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Logger.e("surfaceChanged");
            initCamera();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Logger.e("surfaceDestroyed");
            releaseCamera();
        }
    };


    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }


    private void getScreenMetrix(){
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }




    private void initCamera() {
        if (camera == null) {
            camera = CameraUtil.getInstance(getApplicationContext()).getCamera(flag);
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
//            for (Camera.Size size : pictureSizeList) {
//                Logger.e( "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
//            }
            Camera.Size picSize = getProperSize(pictureSizeList, ((float) mScreenHeight / mScreenWidth));
            if (null == picSize) {
                //Logger.e( "null == picSize");
                picSize = parameters.getPictureSize();
            }
            Logger.e( "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
            parameters.setPictureSize(picSize.width,picSize.height);
            List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
//            for (Camera.Size size : previewSizeList) {
//                Logger.e("previewSizeList size.width=" + size.width + "  size.height=" + size.height);
//            }
            Camera.Size preSize = getProperSize(previewSizeList, ((float) mScreenHeight) / mScreenWidth);
            if (null != preSize) {
                Logger.e("preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
                parameters.setPreviewSize(preSize.width, preSize.height);
            }
            parameters.setPictureFormat(PixelFormat.JPEG);
            if (CameraUtil.getInstance(getApplicationContext()).getCameraFocusable(parameters) != null) {
                parameters.setFocusMode(CameraUtil.getInstance(getApplicationContext()).getCameraFocusable(parameters));
            }
            parameters.setJpegQuality(70);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(flag, info);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
            camera.cancelAutoFocus();
            camera.setParameters(parameters);
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     *            h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        //Logger.e("screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }
        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }


    /**
     * camera release
     * */
    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            Logger.e("camera release");
        }
    }

}
