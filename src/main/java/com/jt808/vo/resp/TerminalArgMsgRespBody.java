package com.jt808.vo.resp;

import com.jt808.vo.PackageData;

import java.util.Arrays;

public class TerminalArgMsgRespBody extends PackageData {

    private TerminalArgMsg terminalArgMsg;

    public TerminalArgMsgRespBody() {
    }

    public TerminalArgMsgRespBody(PackageData packageData) {
        this();
        this.channel = packageData.getChannel();
        this.checkSum = packageData.getCheckSum();
        this.msgBodyBytes = packageData.getMsgBodyBytes();
        this.msgHeader = packageData.getMsgHeader();
    }

    public TerminalArgMsg getTerminalArgMsg() {
        return terminalArgMsg;
    }

    public void setTerminalArgMsg(TerminalArgMsg terminalArgMsg) {
        this.terminalArgMsg = terminalArgMsg;
    }

    @Override
    public String toString() {
        return "TerminalArgMsgRespBody{" +
                "terminalArgMsg=" + terminalArgMsg +
                ", msgHeader=" + msgHeader +
                ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) +
                ", checkSum=" + checkSum +
                ", channel=" + channel +
                '}';
    }

    public static class TerminalArgMsg {
        /**
         * 应答流水号 WORD BYTE[2]
         */
        private int replyFlowId;
        /**
         * 应答参数个数 BYTE[1]
         */
        private int argNum;
        /**
         * 包参数个数 BYTE[1]
         */
        private int pakNum;
        /**
         * 参数列表项
         */
        private TerminalArgList terminalArgList;

        public TerminalArgMsg() {
        }

        public int getReplyFlowId() {
            return replyFlowId;
        }

        public void setReplyFlowId(int replyFlowId) {
            this.replyFlowId = replyFlowId;
        }

        public int getArgNum() {
            return argNum;
        }

        public void setArgNum(int argNum) {
            this.argNum = argNum;
        }

        public int getPakNum() {
            return pakNum;
        }

        public void setPakNum(int pakNum) {
            this.pakNum = pakNum;
        }

        public TerminalArgList getTerminalArgList() {
            return terminalArgList;
        }

        public void setTerminalArgList(TerminalArgList terminalArgList) {
            this.terminalArgList = terminalArgList;
        }

        @Override
        public String toString() {
            return "TerminalArgMsg{" +
                    "replyFlowId=" + replyFlowId +
                    ", argNum=" + argNum +
                    ", pakNum=" + pakNum +
                    ", terminalArgList=" + terminalArgList +
                    '}';
        }

        public static class TerminalArgList {
            //参数id DWORD[4]
            private int argId;
            //参数长度 BYTE[1]
            private int argLen;
            //参数值
            private String arg;

            public TerminalArgList() {
            }

            public int getArgId() {
                return argId;
            }

            public void setArgId(int argId) {
                this.argId = argId;
            }

            public int getArgLen() {
                return argLen;
            }

            public void setArgLen(int argLen) {
                this.argLen = argLen;
            }

            public String getArg() {
                return arg;
            }

            public void setArg(String arg) {
                this.arg = arg;
            }

            @Override
            public String toString() {
                return "TerminalArgList{" +
                        "argId=" + argId +
                        ", argLen=" + argLen +
                        ", arg='" + arg + '\'' +
                        '}';
            }
        }
    }
}
