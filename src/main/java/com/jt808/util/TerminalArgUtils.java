package com.jt808.util;

import com.jt808.common.TPMSConsts;
import com.jt808.common.TerminalArgIdEnums;
import com.jt808.service.codec.MsgDecoder;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg.TerminalArgList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：刘洋
 */
public class TerminalArgUtils {
    private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);
    private BitOperator bitOperator;

    public TerminalArgUtils() {
        this.bitOperator = new BitOperator();
    }

    /**
     * 终端参数项编码
     *
     * @param argId
     * @param str
     * @return
     */
    public byte[] terminalArgToByte(int argId, String str) {
        byte[] arg = argToByte(argId, str);
        byte[] bs = this.bitOperator.concatAll(
                bitOperator.integerTo4Bytes(argId),
                bitOperator.integerTo1Bytes(arg.length),
                arg);
        return bs;
    }

    /**
     * 终端参数项解码
     *
     * @param bs
     * @return
     */
    public TerminalArgList byteToTerminalArg(byte[] bs) {
        TerminalArgList body = new TerminalArgList();
        body.setArgId(this.parseIntFromBytes(bs, 0, 4));
        body.setArgLen(this.parseIntFromBytes(bs, 4, 1));
        if (TerminalArgIdEnums.D_0X0013.getId() == body.getArgId()) {
            body.setArg(this.parseStringFromBytes(bs, 5, body.getArgLen()));
        } else  {
            body.setArg(String.valueOf(this.parseIntFromBytes(bs, 5, 4)));
        }
        body.setMsg(TerminalArgIdEnums.msg(body.getArgId()));
        return body;
    }


    /**
     * 根据参数id 转换参数
     *
     * @param argId 参数id
     * @param str   参数
     * @return
     */
    public byte[] argToByte(int argId, String str) {
        byte[] arg = null;
         if (TerminalArgIdEnums.D_0X0013.getId() == argId) {
            arg = str.getBytes(TPMSConsts.STRING_CHARSET);
        } else  {
            arg = this.bitOperator.integerTo4Bytes(Integer.valueOf(str));
        }
        return arg;
    }

    /**
     * byte[] 转 int
     *
     * @param data
     * @param startIndex
     * @param length
     * @return
     */
    private int parseIntFromBytes(byte[] data, int startIndex, int length) {
        return this.parseIntFromBytes(data, startIndex, length, 0);
    }

    /**
     * byte[] 转 int
     *
     * @param data
     * @param startIndex
     * @param length
     * @param defaultVal
     * @return
     */
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
     * byte[]  String
     *
     * @param data
     * @param startIndex
     * @param lenth
     * @return
     */
    protected String parseStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseStringFromBytes(data, startIndex, lenth, null);
    }

    /**
     * byte[] 转 String
     *
     * @param data
     * @param startIndex
     * @param lenth
     * @param defaultVal
     * @return
     */
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

    public static void main(String []arg){
      log.info("{}",new TerminalArgUtils().terminalArgToByte(0x0018,"88889"));
      log.info("{}",new TerminalArgUtils().byteToTerminalArg(new byte[]{0, 0, 0, 24, 4, 0, 1, 91, 57}));
    }
}
