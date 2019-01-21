package com.jason.media.image;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import com.jason.media.CameraActivity;
import com.jason.media.R;
import com.jason.media.base.BaseActivity;
import com.jason.media.config.FileConfig;
import com.jason.media.utils.BitmapUtil;
import com.jason.media.utils.FileUtil;
import com.jason.media.utils.IOUtil;
import com.jason.media.utils.ImageData;
import com.jason.media.utils.PermissionUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jason on 2019/1/17.
 */

public class ImageActivity extends BaseActivity {


    @BindView(R.id.img_photo)
    ImageView img_photo;

    @OnClick({R.id.btn_image_from_camera,R.id.btn_image_from_gallery})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_image_from_camera:
                PermissionUtil permissionUtil = new PermissionUtil(this);
                permissionUtil.requestPermission(new PermissionUtil.OnPermissionListener() {
                    @Override
                    public void granted() {
                        openActivityByNoParamsForResult(CameraActivity.class,CAMERA_REQUEST_CODE);
                    }

                    @Override
                    public void denied() {

                    }
                }, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.btn_image_from_gallery:
                PermissionUtil permissionUtil1 = new PermissionUtil(this);
                permissionUtil1.requestPermission(new PermissionUtil.OnPermissionListener() {
                    @Override
                    public void granted() {
                        openGallery();
                    }

                    @Override
                    public void denied() {

                    }
                },Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
    }


    private final int CAMERA_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;

    @Override
    public int getLayoutId() {
        return R.layout.activity_image;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void requestData() {

    }



    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (ImageData.getInstance().getValue()!=null){
                    Bitmap bitmap = BitmapUtil.byte2Bitmap(ImageData.getInstance().getValue());
                    img_photo.setImageBitmap(bitmap);
                }
                break;
            case GALLERY_REQUEST_CODE:
                Uri uri = data.getData();
                File imgFile = new File(FileConfig.ROOT,FileConfig.IMAGE);
                boolean isExist1 = FileUtil.existsFile(imgFile);
                try {
                    if (!isExist1){
                        imgFile.createNewFile();
                    }
                    IOUtil.copyStream(getApplicationContext().getContentResolver().openInputStream(uri),new FileOutputStream(imgFile));
                    int degaree = BitmapUtil.decodeImageDegree(imgFile.getPath());
                    Bitmap bitmap = BitmapUtil.file2Bitmap(imgFile,degaree);
                    img_photo.setImageBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
                default:
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
