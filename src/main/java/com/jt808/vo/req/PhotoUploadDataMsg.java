package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class PhotoUploadDataMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 照片编号 byte[10]
     */
    private String photoNum;
    /**
     * 照片数据 bytr[n]
     */
    private String photoData;

    public PhotoUploadDataMsg() {
    }

    public PhotoUploadDataMsg(PackageData packageData) {
        super(packageData);
    }

    public PhotoUploadDataMsg(String photoNum, String photoData) {
        this.photoNum = photoNum;
        this.photoData = photoData;
    }

    public PhotoUploadDataMsg(PackageData packageData, String photoNum, String photoData) {
        super(packageData);
        this.photoNum = photoNum;
        this.photoData = photoData;
    }

    public String getPhotoNum() {
        return photoNum;
    }

    public void setPhotoNum(String photoNum) {
        this.photoNum = photoNum;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    @Override
    public String toString() {
        return "PhotoUploadDataMsg{" +
                "photoNum='" + photoNum + '\'' +
                ", photoData='" + photoData + '\'' +
                '}';
    }
}
