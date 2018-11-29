package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class ImmediatelyTakePicturesMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 执行结果 byte
     */
    private int replyCode;
    /**
     * 上传模式 byte
     */
    private int model;
    /**
     * 摄像头通道号 byte
     */
    private int aisle;
    /**
     * 图片尺寸 byte
     */
    private int size;

    public ImmediatelyTakePicturesMsg() {
    }

    public ImmediatelyTakePicturesMsg(PackageData packageData) {
        super(packageData);
    }

    public int getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(int replyCode) {
        this.replyCode = replyCode;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getAisle() {
        return aisle;
    }

    public void setAisle(int aisle) {
        this.aisle = aisle;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ImmediatelyTakePicturesMsg{" +
                "replyCode=" + replyCode +
                ", model=" + model +
                ", aisle=" + aisle +
                ", size=" + size +
                '}';
    }
}
