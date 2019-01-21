package com.jason.media.utils;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import com.orhanobut.logger.Logger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * Description: 音频文件转化为AAC格式文件
 *
 */
public class AacUtil {

    private String srcPath;
    private String dstPath;
    private MediaCodec mediaDecode;
    private MediaCodec mediaEncode;
    private MediaExtractor mediaExtractor;

    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;
    private MediaCodec.BufferInfo encodeBufferInfo;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private OnCompleteListener onCompleteListener;
    private OnProgressListener onProgressListener;
    private long fileTotalSize;

    private ArrayBlockingQueue<byte[]> queue;


    public static AacUtil newInstance() {
        return new AacUtil();
    }

    /**
     * 设置输入输出文件位置
     *
     * @param srcPath
     * @param dstPath
     */
    public void setIOPath(String srcPath, String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    /**
     * 此类已经过封装
     * 调用prepare方法 会初始化Decode 、Encode 、输入输出流 等一些列操作
     */
    public void prepare() {

        if (srcPath == null) {
            throw new IllegalArgumentException("srcPath can't be null");
        }

        if (dstPath == null) {
            throw new IllegalArgumentException("dstPath can't be null");
        }

        try {
            fos = new FileOutputStream(new File(dstPath));
            bos = new BufferedOutputStream(fos, 200 * 1024);
            File file = new File(srcPath);
            fileTotalSize = file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        queue = new ArrayBlockingQueue<byte[]>(10);

        initAACMediaEncode();//AAC编码器
    }


    private String encodeType = MediaFormat.MIMETYPE_AUDIO_AAC;

    /**
     * 初始化AAC编码器
     */
    private void initAACMediaEncode() {
        try {

            //参数对应-> mime type、采样率、声道数
            MediaFormat encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 16000, 1);
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);//比特率
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024);//作用于inputBuffer的大小

            mediaEncode = MediaCodec.createEncoderByType(encodeType);
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaEncode == null) {
            Logger.e("create mediaEncode failed");
            return;
        }

        mediaEncode.start();
        encodeInputBuffers = mediaEncode.getInputBuffers();
        encodeOutputBuffers = mediaEncode.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();


    }

    private boolean codeOver = false;

    /**
     * 开始转码
     * 音频数据{@link #srcPath}先解码成PCM  PCM数据在编码成MediaFormat.MIMETYPE_AUDIO_AAC音频格式
     * mp3->PCM->aac
     */
    public void startAsync() {
        Logger.e("start");

        new Thread(new DecodeRunnable()).start();
    }


    /**
     * 解码{@link #srcPath}音频文件 得到PCM数据块
     *
     * @return 是否解码完所有数据
     */
    private void srcAudioFormatToPCM() {
        File file = new File(srcPath);// 指定要读取的文件
        FileInputStream fio = null;
        try {
            fio = new FileInputStream(file);
            byte[] bb = new byte[1024];
            while (!codeOver) {
                if (fio.read(bb) != -1) {
                    Logger.e("============   putPCMData ============" + bb.length);
                    dstAudioFormatFromPCM(bb);
                } else {
                    codeOver = true;
                }
            }

            fio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private byte[] chunkAudio = new byte[0];

    /**
     * 编码PCM数据 得到AAC格式的音频文件
     */
    private void dstAudioFormatFromPCM(byte[] pcmData) {

        int inputIndex;
        ByteBuffer inputBuffer;
        int outputIndex;
        ByteBuffer outputBuffer;

        int outBitSize;
        int outPacketSize;
        byte[] PCMAudio;
        PCMAudio = pcmData;

        encodeInputBuffers = mediaEncode.getInputBuffers();
        encodeOutputBuffers = mediaEncode.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();


        inputIndex = mediaEncode.dequeueInputBuffer(0);
        inputBuffer = encodeInputBuffers[inputIndex];
        inputBuffer.clear();
        inputBuffer.limit(PCMAudio.length);
        inputBuffer.put(PCMAudio);//PCM数据填充给inputBuffer
        mediaEncode.queueInputBuffer(inputIndex, 0, PCMAudio.length, 0, 0);//通知编码器 编码


        outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 0);
        while (outputIndex > 0) {

            outBitSize = encodeBufferInfo.size;
            outPacketSize = outBitSize + 7;//7为ADT头部的大小
            outputBuffer = encodeOutputBuffers[outputIndex];//拿到输出Buffer
            outputBuffer.position(encodeBufferInfo.offset);
            outputBuffer.limit(encodeBufferInfo.offset + outBitSize);
            chunkAudio = new byte[outPacketSize];
            addADTStoPacket(chunkAudio, outPacketSize);//添加ADTS
            outputBuffer.get(chunkAudio, 7, outBitSize);//将编码得到的AAC数据 取出到byte[]中

            try {
                //录制aac音频文件，保存在手机内存中
                bos.write(chunkAudio, 0, chunkAudio.length);
                bos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputBuffer.position(encodeBufferInfo.offset);
            mediaEncode.releaseOutputBuffer(outputIndex, false);
            outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 0);

        }

    }

    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = 8; // 16KHz
        int chanCfg = 1; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;

    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            if (bos != null) {
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(bos, fos);
        }

        if (mediaEncode != null) {
            mediaEncode.stop();
            mediaEncode.release();
            mediaEncode = null;
        }

        if (mediaDecode != null) {
            mediaDecode.stop();
            mediaDecode.release();
            mediaDecode = null;
        }

        if (mediaExtractor != null) {
            mediaExtractor.release();
            mediaExtractor = null;
        }

        if (onCompleteListener != null) {
            onCompleteListener = null;
        }

        if (onProgressListener != null) {
            onProgressListener = null;
        }
        Logger.e("release");
    }

    /**
     * 解码线程
     */
    private class DecodeRunnable implements Runnable {
        @Override
        public void run() {
            srcAudioFormatToPCM();
        }
    }


    /**
     * 转码完成回调接口
     */
    public interface OnCompleteListener {
        void completed();
    }

    /**
     * 转码进度监听器
     */
    public interface OnProgressListener {
        void progress(long current, long total);
    }

    /**
     * 设置转码完成监听器
     *
     * @param onCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

}