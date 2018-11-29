package com.jt808.vo.req;

import com.jt808.vo.PackageData;

public class ForbiddenState extends ExtendedTimekeepingTrainingMsg {
    /**
     * 执行结果 byte
     */
    private int replyCode;
    /**
     * 禁训状态 byte
     */
    private int state;
    /**
     * 提示消息长度 byte
     */
    private int msgLen;
    /**
     * 提示消息内容 string
     */
    private String msg;


    public ForbiddenState() {
    }

    public ForbiddenState(PackageData packageData) {
        super(packageData);
    }


    public ForbiddenState(int replyCode, int state, int msgLen, String msg) {
        this.replyCode = replyCode;
        this.state = state;
        this.msgLen = msgLen;
        this.msg = msg;
    }

    public ForbiddenState(PackageData packageData, int replyCode, int state, int msgLen, String msg) {
        super(packageData);
        this.replyCode = replyCode;
        this.state = state;
        this.msgLen = msgLen;
        this.msg = msg;
    }

    public int getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(int replyCode) {
        this.replyCode = replyCode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
        return "ForbiddenState{" +
                "replyCode=" + replyCode +
                ", state=" + state +
                ", msgLen=" + msgLen +
                ", msg='" + msg + '\'' +
                '}';
    }
}
