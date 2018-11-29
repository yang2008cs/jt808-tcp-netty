package com.jt808.vo.resp;

import com.jt808.vo.PackageData;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg.TerminalArgList;

import java.util.List;

public class QueryTerminalArgRespMsgBody extends PackageData {
    // byte[0-1] 应答流水号(WORD) 对应的终端注册消息的流水号
    private int replyFlowId;
    /**
     * 应答参数个数 byte[1]
     */
    private int argTotal;
    /**
     * 包参数个数(本数据包包含的参数个数) byte[1]
     */
    private int argNum;
    /**
     * 参数列表项
     */
    private List<TerminalArgList> terminalArgList;

    public QueryTerminalArgRespMsgBody() {
    }

    public QueryTerminalArgRespMsgBody(PackageData packageData) {
        this();
        this.channel = packageData.getChannel();
        this.checkSum = packageData.getCheckSum();
        this.msgBodyBytes = packageData.getMsgBodyBytes();
        this.msgHeader = packageData.getMsgHeader();
    }

    public QueryTerminalArgRespMsgBody(int replyFlowId, int argTotal, int argNum, List<TerminalArgList> terminalArgList) {
        this.replyFlowId = replyFlowId;
        this.argTotal = argTotal;
        this.argNum = argNum;
        this.terminalArgList = terminalArgList;
    }

    public int getReplyFlowId() {
        return replyFlowId;
    }

    public void setReplyFlowId(int replyFlowId) {
        this.replyFlowId = replyFlowId;
    }

    public int getArgTotal() {
        return argTotal;
    }

    public void setArgTotal(int argTotal) {
        this.argTotal = argTotal;
    }

    public int getArgNum() {
        return argNum;
    }

    public void setArgNum(int argNum) {
        this.argNum = argNum;
    }

    public List<TerminalArgList> getTerminalArgList() {
        return terminalArgList;
    }

    public void setTerminalArgList(List<TerminalArgList> terminalArgList) {
        this.terminalArgList = terminalArgList;
    }

    @Override
    public String toString() {
        return "QueryTerminalArgRespMsgBody{" +
                "replyFlowId=" + replyFlowId +
                ", argTotal=" + argTotal +
                ", argNum=" + argNum +
                ", terminalArgList=" + terminalArgList +
                '}';
    }
}
