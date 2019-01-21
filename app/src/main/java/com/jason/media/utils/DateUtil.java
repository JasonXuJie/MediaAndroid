package com.jason.media.utils;

/**
 * Created by jason on 2019/1/17.
 */

public class DateUtil {

    /**
     * @param time 秒数
     * */
    public static String transTime(int time){
        int second = time % 60;
        int minute = time / 60 % 60;
        int hour = time / 3600;
        return String.format("%02d:%02d:%02d",hour,minute,second);
    }
}
