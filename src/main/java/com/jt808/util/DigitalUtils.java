package com.jt808.util;

public class DigitalUtils {
    //转义，若检验码、消息头以及消息体中出现125则要进行转义。125后紧跟一个2转为126,125后紧跟一个1转为125
    public static byte[] meanTransfer(byte[] original) {
        byte[] bytes = {};
        for (int i = 0; i < original.length; i++) {
            if (original[i] == 125) {
                if (original[i + 1] == 1) {
                    byte[] mean = {125};
                    bytes = mergeBytes(bytes, mean);
                } else if (original[i + 1] == 2) {
                    byte[] mean = {126};
                    bytes = mergeBytes(bytes, mean);
                }
            } else if ((original[i] == 1 || original[i] == 2) && original[i - 1] == 125) {
                byte[] mean = {};
                bytes = mergeBytes(bytes, mean);
            } else {
                byte[] mean = {original[i]};
                bytes = mergeBytes(bytes, mean);
            }
        }
        return bytes;
    }


    //转义，标识位为126，若检验码、消息头以及消息体中出现126则要进行转义。126转为125后紧跟一个2,125转为125后紧跟一个1
    public static byte[] transferMean(byte[] original) {
        byte[] bytes = {};
        for (int i = 0; i < original.length; i++) {
            if (original[i] == 126) {
                byte[] mean = {125, 2};
                bytes = mergeBytes(bytes, mean);

            } else if (original[i] == 125) {
                byte[] mean = {125, 1};
                bytes = mergeBytes(bytes, mean);
            } else {
                byte[] mean = {original[i]};
                bytes = mergeBytes(bytes, mean);
            }
        }
        return bytes;
}

    /**
     * 组合byte数组
     *
     * @param bs
     * @return
     */
    public static byte[] mergeBytes(byte[]... bs) {
        int length = 0;
        for (byte[] bs2 : bs) {
            length += bs2.length;
        }
        // 请求数组长度
        byte[] result = new byte[length];
        int curLength = 0;
        for (byte[] b : bs) {
            System.arraycopy(b, 0, result, curLength, b.length);
            curLength += b.length;
        }
        return result;
    }

}
