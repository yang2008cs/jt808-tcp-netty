package com.jt808.vo.req;

import java.util.Date;
import java.util.List;

public class GnssMsg {
    //位置基本信息数据
    /**
     * 报警标识
     * byte [0-4] (DWORD(32))
     */
    private int warningFlagField;
    /**
     * byte[4-8] 状态(DWORD(32))
     */
    private int statusField;
    /**
     * byte[8-12] 纬度(DWORD(32))
     */
    private float latitude;
    /**
     * byte[12-16] 经度(DWORD(32))
     */
    private float longitude;
    /**
     * byte [16-18]行驶记录速度 (WORD)1/10km/h
     */
    private float runSpeed;
    /**
     * byte[18-20] 卫星定位速度(WORD) 1/10km/h
     */
    private float satelliteSpeed;
    /**
     * byte[20-22] 方向(WORD) 0-359，正北为 0，顺时针
     */
    private int direction;
    /**
     * byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
     *  GMT+8 时间，本标准中之后涉及的时间均采用此时区
     */
    private Date time;

    private List<AttachLocation> attachLocation;

    public GnssMsg() {
    }


    public int getWarningFlagField() {
        return warningFlagField;
    }

    public void setWarningFlagField(int warningFlagField) {
        this.warningFlagField = warningFlagField;
    }

    public int getStatusField() {
        return statusField;
    }

    public void setStatusField(int statusField) {
        this.statusField = statusField;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getRunSpeed() {
        return runSpeed;
    }

    public void setRunSpeed(float runSpeed) {
        this.runSpeed = runSpeed;
    }

    public float getSatelliteSpeed() {
        return satelliteSpeed;
    }

    public void setSatelliteSpeed(float satelliteSpeed) {
        this.satelliteSpeed = satelliteSpeed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public List<AttachLocation> getAttachLocation() {
        return attachLocation;
    }

    public void setAttachLocation(List<AttachLocation> attachLocation) {
        this.attachLocation = attachLocation;
    }

    @Override
    public String toString() {
        return "GnssMsg{" +
                "warningFlagField=" + warningFlagField +
                ", statusField=" + statusField +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", runSpeed=" + runSpeed +
                ", satelliteSpeed=" + satelliteSpeed +
                ", direction=" + direction +
                ", time=" + time +
                ", attachLocation=" + attachLocation +
                '}';
    }

    public static class AttachLocation{
        /**
         * 位置附加信息 byte
         */
        private int msgId;
        /**
         * 附加信息长度 byte
         */
        private int msgLen;
        /**
         * 附加消息 dword word
         */
        private int msg;

        public AttachLocation() {
        }

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        public int getMsgLen() {
            return msgLen;
        }

        public void setMsgLen(int msgLen) {
            this.msgLen = msgLen;
        }

        public int getMsg() {
            return msg;
        }

        public void setMsg(int msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "AttachLocation{" +
                    "msgId=" + msgId +
                    ", msgLen=" + msgLen +
                    ", msg=" + msg +
                    '}';
        }
    }
}
