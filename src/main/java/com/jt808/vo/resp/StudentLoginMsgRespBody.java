package com.jt808.vo.resp;

public class StudentLoginMsgRespBody {
    public static final byte success = 1;
    public static final byte invalid_stu_number = 2;
    public static final byte ban_stu_login = 3;
    public static final byte external_instruction_reminder = 4;
    public static final byte must_teach_models_and_training_model_does_not = 5;
    public static final byte other_errors = 9;


    public static final byte yes_read = 1;
    public static final byte no_read = 0;
    // byte[0-1] 应答流水号(WORD) 对应的终端注册消息的流水号
    private int replyFlowId;
    /***
     * byte 登录结果(BYTE) <br>
     * 1：成功<br>
     * 2：无效的学员编号<br>
     * 3：禁止登录的学员<br>
     * 4：区域外教学提醒<br>
     * 5：准教车型与培训车型不符<br>
     * 9：其他错误<br>
     **/
    private byte replyCode;
    // byte[16] 学员编号
    private String stuhnum;
    //总培训学时 word 2位 单位min
    private int totalHours;
    //当前培训部分已完成学时 word 2位 单位min
    private int finishHours;
    //总培训里程 word 2位 单位1/10km
    private float totalMileage;
    //当前培训部分已完成里程 word 2位 单位1/10km
    private float finishMileage;
    /**
     * 是否报读附加消息
     * 1：需要报读
     * 0：不必报读
     */
    private byte isRead;
    //附加消息长度
    private int msgLen;
    //附加消息
    private String msg;

    public StudentLoginMsgRespBody() {
    }

    public int getReplyFlowId() {
        return replyFlowId;
    }

    public void setReplyFlowId(int replyFlowId) {
        this.replyFlowId = replyFlowId;
    }

    public byte getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(byte replyCode) {
        this.replyCode = replyCode;
    }

    public String getStuhnum() {
        return stuhnum;
    }

    public void setStuhnum(String stuhnum) {
        this.stuhnum = stuhnum;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getFinishHours() {
        return finishHours;
    }

    public void setFinishHours(int finishHours) {
        this.finishHours = finishHours;
    }

    public float getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(float totalMileage) {
        this.totalMileage = totalMileage;
    }

    public float getFinishMileage() {
        return finishMileage;
    }

    public void setFinishMileage(float finishMileage) {
        this.finishMileage = finishMileage;
    }

    public byte getIsRead() {
        return isRead;
    }

    public void setIsRead(byte isRead) {
        this.isRead = isRead;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public void setMsgLen(int msgLen) {
        this.msgLen = msgLen;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "StudentLoginMsgRespBody{" +
                "replyFlowId=" + replyFlowId +
                ", replyCode=" + replyCode +
                ", stuhnum='" + stuhnum + '\'' +
                ", totalHours=" + totalHours +
                ", finishHours=" + finishHours +
                ", totalMileage=" + totalMileage +
                ", finishMileage=" + finishMileage +
                ", isRead=" + isRead +
                ", msgLen=" + msgLen +
                ", msg='" + msg + '\'' +
                '}';
    }
}
