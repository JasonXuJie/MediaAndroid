package com.jason.media.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jason on 2019/1/15.
 */

public class RecordManager {
    //指定音频原
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率
    private static final int mSampleRateInHz = 44100;
    //指定捕获音频的声道数目
    private static final int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
    //指定音频量化位数
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes;
    private File mRecordingFile;//储存AudioRecord录下来的文件
    private boolean  isStarted = false;//是否已开启
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord = null;// 声明 AudioRecord 对象
    private File mFileRoot = null;//文件目录
    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudiioRecordFile";
    //保存的音频文件名
    private static final String mFileName = "audiorecordtest.pcm";
    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private DataOutputStream mDataOutputStream;
    private Thread mRecordThread;




    public static RecordManager getInstance(){
        return RecordHolder.INSTANCE;
    }


    private RecordManager(){

    }

    private static class RecordHolder{
        private static final RecordManager INSTANCE = new RecordManager();
    }



    public void start(){
        if (isStarted){
            Logger.e("start return");
            return;
        }
        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists()){
            Logger.e("创建文件夹");
            mFileRoot.mkdirs();
        }
        createFile();
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig,mAudioFormat);
        Logger.e("缓冲区大小:"+mBufferSizeInBytes);
        mAudioRecord = new AudioRecord(mAudioSource,mSampleRateInHz,mChannelConfig,mAudioFormat,mBufferSizeInBytes);
        mAudioRecord.startRecording();
        isRecording = true;
        mRecordThread = new Thread(recordRunnable);
        mRecordThread.start();
        isStarted = true;
    }




    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[mBufferSizeInBytes];
            try {
                mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));
                while (isRecording&&mAudioRecord.getRecordingState()== AudioRecord.RECORDSTATE_RECORDING){
                    int readSize = mAudioRecord.read(buffer,0,mBufferSizeInBytes);
                    if (readSize>0){
                        mDataOutputStream.write(buffer);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                Logger.e("finally");
                IOUtil.close(mDataOutputStream);
            }
        }
    };


    private void createFile(){
        mRecordingFile = new File(mFileRoot,mFileName);
        if(mRecordingFile.exists()){//音频保存过删除
            Logger.e("删除");
            mRecordingFile.delete();
        }
        try {
           mRecordingFile.createNewFile();//创建新文件
        }catch (IOException e){
            e.printStackTrace();
            Logger.e( "创建储存音频文件出错");
        }
    }



    public void stop(){
        if (!isStarted){
            Logger.e("stop return");
            return;
        }
        isRecording = false;
        try {
            mRecordThread.interrupt();
            mRecordThread.join(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            Logger.e("mAudioRecord stop");
            mAudioRecord.stop();
        }
        mAudioRecord.release();
        Logger.e("mAudioRecord release");
        isStarted = false;
    }

    public void pcm2wav(){
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".wav";
        Logger.e(result);
        WavUtil.convertPcm2Wav(path,result,mSampleRateInHz,mBufferSizeInBytes);
    }

    public String getWavPath(){
        String path = mFileRoot + File.separator + mFileName;
        String result = path.substring(0, path.lastIndexOf(".")) + ".wav";
        return result;
    }

}
