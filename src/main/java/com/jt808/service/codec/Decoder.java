package com.jt808.service.codec;

import com.alibaba.fastjson.JSON;
import com.jt808.common.TPMSConsts;
import com.jt808.util.BCD8421Operater;
import com.jt808.util.BitOperator;
import com.jt808.vo.PackageData;
import com.jt808.vo.req.PlatformLoginMsg;
import com.jt808.vo.resp.TerminalArgMsgRespBody;
import com.jt808.vo.resp.TerminalArgMsgRespBody.TerminalArgMsg;
import com.jt808.vo.resp.TerminalArgMsgRespBody.TerminalArgMsg.TerminalArgList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端消息解码器
 */
public class Decoder {
    private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);

    private BitOperator bitOperator;
    private BCD8421Operater bcd8421Operater;

    public Decoder() {
        this.bitOperator = new BitOperator();
        this.bcd8421Operater = new BCD8421Operater();
    }

    /**
     * 数据包
     *
     * @param data
     * @return PackageData
     */
    public PackageData bytes2PackageData(byte[] data) {
        PackageData ret = new PackageData();
        // 1. 16byte 或 20byte 消息头
        PackageData.MsgHeader msgHeader = this.parseMsgHeaderFromBytes(data);
        ret.setMsgHeader(msgHeader);

        int msgBodyByteStartIndex = 16;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 20;
        }
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        ret.setMsgBodyBytes(tmp);

        // 3. 去掉分隔符之后，最后一位就是校验码
        int checkSumInPkg = data[data.length - 1];
        int calculatedCheckSum = this.bitOperator.getCheckSum4JT808(data, 0, data.length - 1);
        ret.setCheckSum(checkSumInPkg);
        if (checkSumInPkg != calculatedCheckSum) {
            log.warn("检验码不一致,msgid:{},pkg:{},calculated:{}", msgHeader.getMsgId(), checkSumInPkg, calculatedCheckSum);
        }
        return ret;
    }

    /**
     * 消息头
     *
     * @param data
     * @return
     */
    private PackageData.MsgHeader parseMsgHeaderFromBytes(byte[] data) {
        PackageData.MsgHeader msgHeader = new PackageData.MsgHeader();

        // 1. 消息ID word(16)
        msgHeader.setMsgId(this.parseIntFromBytes(data, 1, 2));
        // 2. 消息体属性 word(16)=================>
        int msgBodyProps = this.parseIntFromBytes(data, 3, 2);
        msgHeader.setMsgBodyPropsField(msgBodyProps);
        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        msgHeader.setMsgBodyLength(msgBodyProps & 0x3ff);
        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        msgHeader.setEncryptionType((msgBodyProps & 0x1c00) >> 10);
        // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
        msgHeader.setHasSubPackage(((msgBodyProps & 0x2000) >> 13) == 1);
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        msgHeader.setReservedBit(((msgBodyProps & 0xc000) >> 14) + "");
        // 消息体属性 word(16)<=================
        // 3. 终端手机号 bcd[6]
        msgHeader.setTerminalPhone(this.parseBcdStringFromBytes(data, 5, 8));
        // 4. 消息流水号 word(16) 按发送顺序从 0 开始循环累加
        msgHeader.setFlowId(this.parseIntFromBytes(data, 13, 2));
        // 5. 消息包封装项
        // 有子包信息
        if (msgHeader.isHasSubPackage()) {
            // 消息包封装项字段
            msgHeader.setPackageInfoField(this.parseIntFromBytes(data, 16, 4));
            // byte[0-1] 消息包总数(word(16))
            msgHeader.setTotalSubPackage(this.parseIntFromBytes(data, 16, 2));
            // byte[2-3] 包序号(word(16)) 从 1 开始
            msgHeader.setSubPackageSeq(this.parseIntFromBytes(data, 18, 2));
        }
        return msgHeader;
    }

    protected String parseStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseStringFromBytes(data, startIndex, lenth, null);
    }

    private String parseStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return new String(tmp, TPMSConsts.STRING_CHARSET);
        } catch (Exception e) {
            log.error("解析字符串出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseBcdStringFromBytes(data, startIndex, lenth, null);
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return this.bcd8421Operater.bcd2String(tmp);
        } catch (Exception e) {
            log.error("解析BCD(8421码)出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length) {
        return this.parseIntFromBytes(data, startIndex, length, 0);
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length, int defaultVal) {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            final int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[len];
            System.arraycopy(data, startIndex, tmp, 0, len);
            return bitOperator.byteToInteger(tmp);
        } catch (Exception e) {
            log.error("解析整数出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    /**
     * 终端注册消息应答
     *
     * @param bs
     * @return
     */
    public PlatformLoginMsg toLoginMsg(byte[] bs) {
        PackageData packageData = this.bytes2PackageData(bs);
        PlatformLoginMsg ret = new PlatformLoginMsg(packageData);
        PlatformLoginMsg.PlatformLoginInfo body = new PlatformLoginMsg.PlatformLoginInfo();
        byte[] data = ret.getMsgBodyBytes();
        log.debug("注册应答:{}", JSON.toJSONString(data, true));
        body.setResult(this.parseIntFromBytes(data, 0, 1));
        ret.setPlatformLoginInfo(body);
        return ret;
    }

    /**
     * 查询终端参数应答
     * @param bs
     * @return
     */
    public TerminalArgMsgRespBody toTerminalArgMsg(byte[] bs) {
        PackageData packageData = this.bytes2PackageData(bs);
        TerminalArgMsgRespBody ret = new TerminalArgMsgRespBody(packageData);
        byte[] data = ret.getMsgBodyBytes();
        TerminalArgMsg body = new TerminalArgMsg();
        TerminalArgList terminalArgList = new TerminalArgList();
        //应答流水号
        body.setReplyFlowId(this.parseIntFromBytes(data,0,2));
        //应答参数个数
        body.setArgNum(this.parseIntFromBytes(data,2,1));
        //包参数个数
        body.setPakNum(this.parseIntFromBytes(data,3,1));
        //参数列表项
        //参数id
        terminalArgList.setArgId(this.parseIntFromBytes(data,4,4));
        terminalArgList.setArgLen(this.parseIntFromBytes(data,8,1));
        terminalArgList.setArg(this.parseStringFromBytes(data,9,data.length-9));
        body.setTerminalArgList(terminalArgList);
        ret.setTerminalArgMsg(body);
        return ret;
    }

    /**
     * 命令上报学时记录应答消息
     * @param bs
     * @return
     */
    public Integer toUpPeriodRecordMsg(byte[] bs) {
        PackageData packageData = this.bytes2PackageData(bs);
        byte[] data = packageData.getMsgBodyBytes();
        return this.parseIntFromBytes(data,0,1);
    }
}
