package com.jt808.vo.req;

import com.jt808.vo.PackageData;

/**
 * @Author ：刘洋
 * 教练登录消息
 */
public class CoachLoginMsg  extends ExtendedTimekeepingTrainingMsg{
    //教练编号 byte[16]
    private String coachnum;
    //教练身份证 byte[18]
    private String idcard;
    //准教车型 byte[2]
    private String teachpermitted;
    //Gnss数据包byte[28]
    private GnssMsg gnss;

    public CoachLoginMsg(){}

    public CoachLoginMsg(PackageData packageData){
        super(packageData);
    }


    public String getCoachnum() {
        return coachnum;
    }

    public void setCoachnum(String coachnum) {
        this.coachnum = coachnum;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getTeachpermitted() {
        return teachpermitted;
    }

    public void setTeachpermitted(String teachpermitted) {
        this.teachpermitted = teachpermitted;
    }

    public GnssMsg getGnss() {
        return gnss;
    }

    public void setGnss(GnssMsg gnss) {
        this.gnss = gnss;
    }

    @Override
    public String toString() {
        return "CoachLoginMsg{" +
                "coachnum='" + coachnum + '\'' +
                ", idcard='" + idcard + '\'' +
                ", teachpermitted='" + teachpermitted + '\'' +
                ", gnss=" + gnss +
                '}';
    }
}
