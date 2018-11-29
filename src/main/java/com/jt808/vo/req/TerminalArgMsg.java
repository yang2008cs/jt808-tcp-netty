package com.jt808.vo.req;

import com.jt808.vo.PackageData;

import java.util.Arrays;
import java.util.List;

public class TerminalArgMsg extends PackageData {

    private TerminalArg terminalArg;

    public TerminalArgMsg() {
    }

    public TerminalArgMsg(PackageData packageData) {
        this();
        this.channel = packageData.getChannel();
        this.checkSum = packageData.getCheckSum();
        this.msgBodyBytes = packageData.getMsgBodyBytes();
        this.msgHeader = packageData.getMsgHeader();
    }

    public TerminalArg getTerminalArg() {
        return terminalArg;
    }

    public void setTerminalArg(TerminalArg terminalArg) {
        this.terminalArg = terminalArg;
    }

    @Override
    public String toString() {
        return "TerminalArgMsg{" +
                "terminalArg=" + terminalArg +
                ", msgHeader=" + msgHeader +
                ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) +
                ", checkSum=" + checkSum +
                ", channel=" + channel +
                '}';
    }

    public static class TerminalArg {
        /**
         * 参数总数 byte[1]
         */
        private int argTotal;
        /**
         * 分包参数个数(本数据包包含的参数个数) byte[1]
         */
        private int argNum;
        /**
         * 参数列表项
         */
        private List<TerminalArgList> terminalArgList;

        public TerminalArg() {
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
            return "TerminalArg{" +
                    "argTotal=" + argTotal +
                    ", argNum=" + argNum +
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
            //参数说明
            private String msg;

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

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            @Override
            public String toString() {
                return "TerminalArgList{" +
                        "argId=" + argId +
                        ", argLen=" + argLen +
                        ", arg='" + arg + '\'' +
                        ", msg='" + msg + '\'' +
                        '}';
            }
        }

    }
}
