package com.jt808.vo.req;

import com.jt808.vo.PackageData;

/**
 * @author :liuyang
 * 学员登录消息
 */
public class StudentLoginMsg extends ExtendedTimekeepingTrainingMsg {
    //学员编号 byte[16]
    private String stunum;
    //当前教练编号 byte[16]
    private String coachnum;
    //培训课程BCD[5]
    private String curriculum;
    //课堂id dword 4 位
    private int classRoomId;
    //Gnss数据包byte[28]
    private GnssMsg gnss;

    public StudentLoginMsg() {
    }

    public StudentLoginMsg(PackageData packageData) {
        super(packageData);
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

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
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
        return "StudentLoginMsg{" +
                "stunum='" + stunum + '\'' +
                ", coachnum='" + coachnum + '\'' +
                ", curriculum='" + curriculum + '\'' +
                ", classRoomId=" + classRoomId +
                ", gnss=" + gnss +
                '}';
    }
}
