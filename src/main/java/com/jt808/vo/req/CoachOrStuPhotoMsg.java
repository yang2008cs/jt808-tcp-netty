package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class CoachOrStuPhotoMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 类型：0：教练，1：学员
     * byte
     */
    private int type;
    /**
     * 教练或学员编号
     * byte[16]
     */
    private String num;

    public CoachOrStuPhotoMsg() {
    }

    public CoachOrStuPhotoMsg(PackageData packageData) {
        super(packageData);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "CoachOrStuPhotoMsg{" +
                "type=" + type +
                ", num='" + num + '\'' +
                '}';
    }
}
