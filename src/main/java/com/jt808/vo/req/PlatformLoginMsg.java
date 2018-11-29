package com.jt808.vo.req;

import com.jt808.vo.PackageData;

import java.util.Arrays;

/**
 * @author :liuyang
 * 平台登录请求
 */
public class PlatformLoginMsg extends PackageData {

    private PlatformLoginInfo platformLoginInfo;

    public PlatformLoginMsg(PackageData packageData) {
        this();
        this.channel = packageData.getChannel();
        this.checkSum = packageData.getCheckSum();
        this.msgBodyBytes = packageData.getMsgBodyBytes();
        this.msgHeader = packageData.getMsgHeader();
    }

    public PlatformLoginMsg() {
    }

    public PlatformLoginInfo getPlatformLoginInfo() {
        return platformLoginInfo;
    }

    public void setPlatformLoginInfo(PlatformLoginInfo platformLoginInfo) {
        this.platformLoginInfo = platformLoginInfo;
    }

    @Override
    public String toString() {
        return "PlatformLoginResp{" +
                "platformLoginInfo=" + platformLoginInfo +
                ", msgHeader=" + msgHeader +
                ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) +
                ", checkSum=" + checkSum +
                ", channel=" + channel +
                '}';
    }

    public static class PlatformLoginInfo {
        //平台编号 byte[5]
        private String num;
        //密码  byte[8]
        private String pass;
        //接入码 dword

        private String code;
        //应答结果 发送时不复制
        private int result;

        public PlatformLoginInfo() {
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "PlatformLoginInfo{" +
                    "num='" + num + '\'' +
                    ", pass='" + pass + '\'' +
                    ", code='" + code + '\'' +
                    '}';
        }
    }
}
