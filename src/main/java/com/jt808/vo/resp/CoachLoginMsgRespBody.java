package com.jt808.vo.resp;

public class CoachLoginMsgRespBody {
    public static final byte success = 1;
    public static final byte invalid_trainer_number = 2;
    public static final byte must_teach_models_does_not = 3;
    public static final byte other_errors = 9;

    public static final byte all_read = 0;
    public static final byte yes_read = 1;
    public static final byte no_read = 2;
    // byte[0-1] 应答流水号(WORD) 对应的终端注册消息的流水号
    private int replyFlowId;
    /***
     * byte 登录结果(BYTE) <br>
     * 1：成功<br>
     * 2：无效的教练员编号<br>
     * 3：准教车型不符<br>
     * 9：其他错误<br>
     **/
    private byte replyCode;
    // byte[16] 教练编号
    private String coachnum;
    /**
     * 是否报读附加消息
     * 0：根据全局设置决定是否报读
     * 1：需要报读
     * 2：不必报读
     */
    private byte isRead;
    //附加消息长度
    private int msgLen;
    //附加消息
    private String msg;

    public CoachLoginMsgRespBody() {
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

    public String getCoachnum() {
        return coachnum;
    }

    public void setCoachnum(String coachnum) {
        this.coachnum = coachnum;
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
        return "CoachLoginMsgRespBody{" +
                "replyFlowId=" + replyFlowId +
                ", replyCode=" + replyCode +
                ", coachnum='" + coachnum + '\'' +
                ", isRead=" + isRead +
                ", msgLen=" + msgLen +
                ", msg='" + msg + '\'' +
                '}';
    }
}
