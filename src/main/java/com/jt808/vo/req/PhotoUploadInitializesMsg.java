package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class PhotoUploadInitializesMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 照片编号 byte[10]
     */
    private String photoNum;
    /**
     * 学员编号 byte[16]
     */
    private String stunum;
    /**
     * 上传模式 byte
     */
    private int upMode;
    /**
     * 摄像头通道号 byte
     */
    private int cameraChannelNumber;
    /**
     * 图片尺寸 byte
     */
    private int photoSize;
    /**
     * 发起图片的事件类型 byte
     */
    private int photoType;
    /**
     * 总包数 word 2
     */
    private int totalPackage;
    /**
     * 照片数据大小 dword 4 位
     */
    private int photoDataSize;
    /**
     * 课堂id dword 4 位
     */
    private int classRoomId;
    /**
     * 卫星定位数据包 byte[28]
     */
    private GnssMsg gnss;
    /**
     * 人脸识别置信度 byte 0-100数值越大置信度越高
     */
    private int confidenceCoefficient;

    public PhotoUploadInitializesMsg() {
    }

    public PhotoUploadInitializesMsg(PackageData packageData) {
        super(packageData);
    }



    public String getPhotoNum() {
        return photoNum;
    }

    public void setPhotoNum(String photoNum) {
        this.photoNum = photoNum;
    }

    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public int getUpMode() {
        return upMode;
    }

    public void setUpMode(int upMode) {
        this.upMode = upMode;
    }

    public int getCameraChannelNumber() {
        return cameraChannelNumber;
    }

    public void setCameraChannelNumber(int cameraChannelNumber) {
        this.cameraChannelNumber = cameraChannelNumber;
    }

    public int getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    public int getPhotoType() {
        return photoType;
    }

    public void setPhotoType(int photoType) {
        this.photoType = photoType;
    }

    public int getTotalPackage() {
        return totalPackage;
    }

    public void setTotalPackage(int totalPackage) {
        this.totalPackage = totalPackage;
    }

    public int getPhotoDataSize() {
        return photoDataSize;
    }

    public void setPhotoDataSize(int photoDataSize) {
        this.photoDataSize = photoDataSize;
    }

    public int getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(int classRoomId) {
        this.classRoomId = classRoomId;
    }

    public GnssMsg getGnss() {
        return gnss;
    }

    public void setGnss(GnssMsg gnss) {
        this.gnss = gnss;
    }

    public int getConfidenceCoefficient() {
        return confidenceCoefficient;
    }

    public void setConfidenceCoefficient(int confidenceCoefficient) {
        this.confidenceCoefficient = confidenceCoefficient;
    }

    @Override
    public String toString() {
        return "PhotoUploadInitializesMsg{" +
                "photoNum='" + photoNum + '\'' +
                ", stunum='" + stunum + '\'' +
                ", upMode=" + upMode +
                ", cameraChannelNumber=" + cameraChannelNumber +
                ", photoSize=" + photoSize +
                ", photoType=" + photoType +
                ", totalPackage=" + totalPackage +
                ", photoDataSize=" + photoDataSize +
                ", classRoomId=" + classRoomId +
                ", gnss=" + gnss +
                ", confidenceCoefficient=" + confidenceCoefficient +
                '}';
    }
}
