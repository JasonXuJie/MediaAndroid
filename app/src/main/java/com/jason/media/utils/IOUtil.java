package com.jason.media.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IOUtil {



    private static final int BUFFER_SIZE = 1024;
    private static final int EOF = -1;


    public static void copyStream(InputStream inputStream, OutputStream outputStream){
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer))!=EOF){
                outputStream.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(inputStream,outputStream);
        }
    }

    public static void close(Closeable... closeableList) {
        try {
            for (Closeable closeable : closeableList) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
