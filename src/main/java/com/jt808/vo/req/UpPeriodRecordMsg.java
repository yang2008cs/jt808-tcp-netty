package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class UpPeriodRecordMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 学时记录编号 byte[26]
     */
    private String recordNum;
    /**
     * 上报类型 byte
     */
    private int upType;
    /**
     * 学员编号 byte[16]
     */
    private String stunum;
    /**
     * 教练编号 byte[16]
     */
    private String coachnum;
    /**
     * 课堂id dword 4 位
     */
    private int classRoomId;
    /**
     * 记录产生时间 BCD[3]
     * HHmmss
     */
    private String recordTime;
    /**
     * 培训课程BCD[5]
     */
    private String curriculum;
    /**
     * 记录状态 BYTE
     * 0正常记录，1异常记录
     */
    private int state;
    /**
     * 最大速度 WORD
     */
    private int speed;
    /**
     * 里程 WORD
     */
    private int mileage;
    /**
     * Gnss数据包byte[28]
     */
    private GnssMsg gnss;

    public UpPeriodRecordMsg() {
    }

    public UpPeriodRecordMsg(PackageData packageData) {
        super(packageData);
    }


    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public int getUpType() {
        return upType;
    }

    public void setUpType(int upType) {
        this.upType = upType;
    }

    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public String getCoachnum() {
        return coachnum;
    }

    public void setCoachnum(String coachnum) {
        this.coachnum = coachnum;
    }

    public int getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(int classRoomId) {
        this.classRoomId = classRoomId;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public GnssMsg getGnss() {
        return gnss;
    }

    public void setGnss(GnssMsg gnss) {
        this.gnss = gnss;
    }

    @Override
    public String toString() {
        return "UpPeriodRecordMsg{" +
                "recordNum='" + recordNum + '\'' +
                ", upType=" + upType +
                ", stunum='" + stunum + '\'' +
                ", coachnum='" + coachnum + '\'' +
                ", classRoomId=" + classRoomId +
                ", recordTime='" + recordTime + '\'' +
                ", curriculum='" + curriculum + '\'' +
                ", state=" + state +
                ", speed=" + speed +
                ", mileage=" + mileage +
                ", gnss=" + gnss +
                '}';
    }
}
