package com.jt808.vo.req;

import com.alibaba.fastjson.annotation.JSONField;
import com.jt808.vo.PackageData;

import java.util.Arrays;

/**
 * 扩展计时培训消息
 */
public class ExtendedTimekeepingTrainingMsg extends PackageData {

    private ExtendedTimekeepingTrainingInfo extendedTimekeepingTrainingInfo;

    public ExtendedTimekeepingTrainingMsg() {
    }

    public ExtendedTimekeepingTrainingMsg(PackageData packageData) {
        this();
        this.channel = packageData.getChannel();
        this.checkSum = packageData.getCheckSum();
        this.msgBodyBytes = packageData.getMsgBodyBytes();
        this.msgHeader = packageData.getMsgHeader();
    }

    public ExtendedTimekeepingTrainingInfo getExtendedTimekeepingTrainingInfo() {
        return extendedTimekeepingTrainingInfo;
    }

    public void setExtendedTimekeepingTrainingInFo(ExtendedTimekeepingTrainingInfo extendedTimekeepingTrainingInfo) {
        this.extendedTimekeepingTrainingInfo = extendedTimekeepingTrainingInfo;
    }

    @Override
    public String toString() {
        return "ExtendedTimekeepingTrainingMsg{" +
                "extendedTimekeepingTrainingInfo=" + extendedTimekeepingTrainingInfo +
                ", msgHeader=" + msgHeader +
                ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) +
                ", checkSum=" + checkSum +
                ", channel=" + channel +
                '}';
    }

    public static class ExtendedTimekeepingTrainingInfo{
        //透传消息id 为功能号+消息编号 word byte[0-2]
        private int ospfId;
        /**
         *  扩展消息属性 bit0 表示消息失效类型，应答中也应附带此内容 0：实时消息，1：补传消息
         *               bit1 表示应答属性，0：不需要应答，1：需要应答
         *               bit4-7 表示加密算法，0：未加密，1：SHA1，2：SHA256；其他保留
         *               word byte[2-4]
         */
        private int bodyPropsField;
        //驾培包序号 word byte[4-6]
        private int numOrder;
        //计时终端编号byte[16] byte[6-22]
        private String terminalId;
        //数据长度 word byte[22-24]数据长度为n，没有数据则为0
        private int dataLen;
        //数据内容byte[n] byte[24-(24+n)]
        @JSONField(serialize=false)
        private byte[] dataContent;
        //校验串  byte[(24+n)-x] 采用2048位证书时，长度为256byte，平台发送的扩展消息无此字段
        private String checkSum;

        public ExtendedTimekeepingTrainingInfo() {
        }

        public int getOspfId() {
            return ospfId;
        }

        public void setOspfId(int ospfId) {
            this.ospfId = ospfId;
        }

        public int getBodyPropsField() {
            return bodyPropsField;
        }

        public void setBodyPropsField(int bodyPropsField) {
            this.bodyPropsField = bodyPropsField;
        }

        public int getNumOrder() {
            return numOrder;
        }

        public void setNumOrder(int numOrder) {
            this.numOrder = numOrder;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public int getDataLen() {
            return dataLen;
        }

        public void setDataLen(int dataLen) {
            this.dataLen = dataLen;
        }

        public byte[] getDataContent() {
            return dataContent;
        }

        public void setDataContent(byte[] dataContent) {
            this.dataContent = dataContent;
        }

        public String getCheckSum() {
            return checkSum;
        }

        public void setCheckSum(String checkSum) {
            this.checkSum = checkSum;
        }

        @Override
        public String toString() {
            return "ExtendedTimekeepingTrainingInFo{" +
                    "ospfId=" + ospfId +
                    ", bodyPropsField=" + bodyPropsField +
                    ", numOrder=" + numOrder +
                    ", terminalId='" + terminalId + '\'' +
                    ", dataLen=" + dataLen +
                    ", dataContent=" + Arrays.toString(dataContent) +
                    ", checkSum=" + checkSum +
                    '}';
        }
    }
}
