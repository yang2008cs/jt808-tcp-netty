package com.jt808.vo.req;

import com.jt808.vo.PackageData;

/**
 * @author :liuyang
 * 终端控制
 */
public class TerminalControlMsg extends PackageData {
    /**
     * 命令字 byte
     */
    private int commandWord;
    /**
     * 命令参数
     */
    private CommandParameter commandParameter;

    public TerminalControlMsg() {
    }

    public TerminalControlMsg(PackageData packageData) {
        this();
        this.channel = packageData.getChannel();
        this.checkSum = packageData.getCheckSum();
        this.msgBodyBytes = packageData.getMsgBodyBytes();
        this.msgHeader = packageData.getMsgHeader();
    }

    public int getCommandWord() {
        return commandWord;
    }

    public void setCommandWord(int commandWord) {
        this.commandWord = commandWord;
    }

    public CommandParameter getCommandParameter() {
        return commandParameter;
    }

    public void setCommandParameter(CommandParameter commandParameter) {
        this.commandParameter = commandParameter;
    }

    @Override
    public String toString() {
        return "TerminalControlMsg{" +
                "commandWord=" + commandWord +
                ", commandParameter=" + commandParameter +
                '}';
    }

    public static class CommandParameter{
        /**
         * 连接控制 byte
         */
        private int linkControl;
        /**
         * 拨号点名称 string
         */
        private String dialName;
        /**
         * 拨号用户名 string
         */
        private String dialUserName;
        /**
         * 拨号密码 string
         */
        private String dialPassWord;
        /**
         * 地址 string
         */
        private String address;
        /**
         * TCP端口 word
         */
        private int tcpPort;
        /**
         * UDP端口 word
         */
        private int udpPort;
        /**
         * 制造商id byte[5]
         */
        private String manufacturerId;
        /**
         * 监管平台鉴权码 string
         */
        private String authCode;
        /**
         * 硬件版本 string
         */
        private String hardwareVersion;
        /**
         * 固件版本 string
         */
        private String firmwareVersion;
        /**
         * URL地址 string
         */
        private String url;
        /**
         * 连接到指定服务器时限 单位：min word
         */
        private int timeLimit;

        public CommandParameter() {
        }

        public int getLinkControl() {
            return linkControl;
        }

        public void setLinkControl(int linkControl) {
            this.linkControl = linkControl;
        }

        public String getDialName() {
            return dialName;
        }

        public void setDialName(String dialName) {
            this.dialName = dialName;
        }

        public String getDialUserName() {
            return dialUserName;
        }

        public void setDialUserName(String dialUserName) {
            this.dialUserName = dialUserName;
        }

        public String getDialPassWord() {
            return dialPassWord;
        }

        public void setDialPassWord(String dialPassWord) {
            this.dialPassWord = dialPassWord;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getTcpPort() {
            return tcpPort;
        }

        public void setTcpPort(int tcpPort) {
            this.tcpPort = tcpPort;
        }

        public int getUdpPort() {
            return udpPort;
        }

        public void setUdpPort(int udpPort) {
            this.udpPort = udpPort;
        }

        public String getManufacturerId() {
            return manufacturerId;
        }

        public void setManufacturerId(String manufacturerId) {
            this.manufacturerId = manufacturerId;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getHardwareVersion() {
            return hardwareVersion;
        }

        public void setHardwareVersion(String hardwareVersion) {
            this.hardwareVersion = hardwareVersion;
        }

        public String getFirmwareVersion() {
            return firmwareVersion;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(int timeLimit) {
            this.timeLimit = timeLimit;
        }

        @Override
        public String toString() {
            return "CommandParameter{" +
                    "linkControl=" + linkControl +
                    ", dialName='" + dialName + '\'' +
                    ", dialUserName='" + dialUserName + '\'' +
                    ", dialPassWord='" + dialPassWord + '\'' +
                    ", address='" + address + '\'' +
                    ", tcpPort=" + tcpPort +
                    ", udpPort=" + udpPort +
                    ", manufacturerId='" + manufacturerId + '\'' +
                    ", authCode='" + authCode + '\'' +
                    ", hardwareVersion='" + hardwareVersion + '\'' +
                    ", firmwareVersion='" + firmwareVersion + '\'' +
                    ", url='" + url + '\'' +
                    ", timeLimit=" + timeLimit +
                    '}';
        }
    }

}
