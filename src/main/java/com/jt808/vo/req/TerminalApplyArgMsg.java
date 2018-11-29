package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class TerminalApplyArgMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 参数编号 byte
     */
    private int argNum;
    /**
     * 定时拍照时间间隔 byte
     */
    private int timedPhotoIntervals;
    /**
     * 照片上传设置 byte
     */
    private int photoUploadSettings;
    /**
     * 是否报读附加消息 byte
     */
    private int isRead;
    /**
     * 熄火后停止学时计时的延时时间 byte
     */
    private int delayedTime;
    /**
     * 熄火后Gnss数据包上传间隔 word
     */
    private int gnssUpInterval;
    /**
     * 熄火后教练自动登出的延时时间 word
     */
    private int coachLoginDelayedTime;
    /**
     * 重新验证身份时间 word
     */
    private int revalidate;
    /**
     * 教练跨校教学 byte
     */
    private int acrossSchoolTeaching;
    /**
     * 学员跨校学习
     */
    private int acrossSchoolLearning;

    public TerminalApplyArgMsg() {
    }

    public TerminalApplyArgMsg(PackageData packageData) {
        super(packageData);
    }

    public TerminalApplyArgMsg(int argNum, int timedPhotoIntervals, int photoUploadSettings, int isRead, int delayedTime, int gnssUpInterval, int coachLoginDelayedTime, int revalidate, int acrossSchoolTeaching, int acrossSchoolLearning) {
        this.argNum = argNum;
        this.timedPhotoIntervals = timedPhotoIntervals;
        this.photoUploadSettings = photoUploadSettings;
        this.isRead = isRead;
        this.delayedTime = delayedTime;
        this.gnssUpInterval = gnssUpInterval;
        this.coachLoginDelayedTime = coachLoginDelayedTime;
        this.revalidate = revalidate;
        this.acrossSchoolTeaching = acrossSchoolTeaching;
        this.acrossSchoolLearning = acrossSchoolLearning;
    }

    public TerminalApplyArgMsg(PackageData packageData, int argNum, int timedPhotoIntervals, int photoUploadSettings, int isRead, int delayedTime, int gnssUpInterval, int coachLoginDelayedTime, int revalidate, int acrossSchoolTeaching, int acrossSchoolLearning) {
        super(packageData);
        this.argNum = argNum;
        this.timedPhotoIntervals = timedPhotoIntervals;
        this.photoUploadSettings = photoUploadSettings;
        this.isRead = isRead;
        this.delayedTime = delayedTime;
        this.gnssUpInterval = gnssUpInterval;
        this.coachLoginDelayedTime = coachLoginDelayedTime;
        this.revalidate = revalidate;
        this.acrossSchoolTeaching = acrossSchoolTeaching;
        this.acrossSchoolLearning = acrossSchoolLearning;
    }

    public int getArgNum() {
        return argNum;
    }

    public void setArgNum(int argNum) {
        this.argNum = argNum;
    }

    public int getTimedPhotoIntervals() {
        return timedPhotoIntervals;
    }

    public void setTimedPhotoIntervals(int timedPhotoIntervals) {
        this.timedPhotoIntervals = timedPhotoIntervals;
    }

    public int getPhotoUploadSettings() {
        return photoUploadSettings;
    }

    public void setPhotoUploadSettings(int photoUploadSettings) {
        this.photoUploadSettings = photoUploadSettings;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getDelayedTime() {
        return delayedTime;
    }

    public void setDelayedTime(int delayedTime) {
        this.delayedTime = delayedTime;
    }

    public int getGnssUpInterval() {
        return gnssUpInterval;
    }

    public void setGnssUpInterval(int gnssUpInterval) {
        this.gnssUpInterval = gnssUpInterval;
    }

    public int getCoachLoginDelayedTime() {
        return coachLoginDelayedTime;
    }

    public void setCoachLoginDelayedTime(int coachLoginDelayedTime) {
        this.coachLoginDelayedTime = coachLoginDelayedTime;
    }

    public int getRevalidate() {
        return revalidate;
    }

    public void setRevalidate(int revalidate) {
        this.revalidate = revalidate;
    }

    public int getAcrossSchoolTeaching() {
        return acrossSchoolTeaching;
    }

    public void setAcrossSchoolTeaching(int acrossSchoolTeaching) {
        this.acrossSchoolTeaching = acrossSchoolTeaching;
    }

    public int getAcrossSchoolLearning() {
        return acrossSchoolLearning;
    }

    public void setAcrossSchoolLearning(int acrossSchoolLearning) {
        this.acrossSchoolLearning = acrossSchoolLearning;
    }

    @Override
    public String toString() {
        return "QueryTerminalApplyArgRespBody{" +
                "argNum=" + argNum +
                ", timedPhotoIntervals=" + timedPhotoIntervals +
                ", photoUploadSettings=" + photoUploadSettings +
                ", isRead=" + isRead +
                ", delayedTime=" + delayedTime +
                ", gnssUpInterval=" + gnssUpInterval +
                ", coachLoginDelayedTime=" + coachLoginDelayedTime +
                ", revalidate=" + revalidate +
                ", acrossSchoolTeaching=" + acrossSchoolTeaching +
                ", acrossSchoolLearning=" + acrossSchoolLearning +
                '}';
    }

}
