package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class StudentLoginOutMsg extends ExtendedTimekeepingTrainingMsg {
    //学员编号 byte[16]
    private String stunum;
    //登出时间 BCD[6] YYMMddHHmmss
    private String outTime;
    //学员该次登录总时间 word 2位
    private int loginTotalTime;
    //学员该次登录总里程 word 2位
    private float loginTotalMileage;
    //课堂id dword 4 位
    private int classRoomId;
    //Gnss数据包byte[28]
    private GnssMsg gnss;

    public StudentLoginOutMsg() {
    }

    public StudentLoginOutMsg(PackageData packageData) {
        super(packageData);
    }



    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public int getLoginTotalTime() {
        return loginTotalTime;
    }

    public void setLoginTotalTime(int loginTotalTime) {
        this.loginTotalTime = loginTotalTime;
    }

    public float getLoginTotalMileage() {
        return loginTotalMileage;
    }

    public void setLoginTotalMileage(float loginTotalMileage) {
        this.loginTotalMileage = loginTotalMileage;
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

    @Override
    public String toString() {
        return "StudentLoginOutMsg{" +
                "stunum='" + stunum + '\'' +
                ", outTime='" + outTime + '\'' +
                ", loginTotalTime=" + loginTotalTime +
                ", loginTotalMileage=" + loginTotalMileage +
                ", classRoomId=" + classRoomId +
                ", gnss=" + gnss +
                '}';
    }
}
