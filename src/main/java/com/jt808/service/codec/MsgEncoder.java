package com.jt808.service.codec;
import com.jt808.common.TPMSConsts;
import com.jt808.util.BitOperator;
import com.jt808.util.JT808ProtocolUtils;
import com.jt808.vo.PackageData;
import com.jt808.vo.Session;
import com.jt808.vo.req.ExtendedTimekeepingTrainingMsg;
import com.jt808.vo.req.TerminalRegisterMsg;
import com.jt808.vo.resp.CoachLoginMsgRespBody;
import com.jt808.vo.resp.ServerCommonRespMsgBody;
import com.jt808.vo.resp.StudentLoginMsgRespBody;
import com.jt808.vo.resp.TerminalRegisterMsgRespBody;

import java.util.Arrays;

/**
 * @author :liuyang
 * 消息编码器
 */
public class MsgEncoder {
    private BitOperator bitOperator;
    private JT808ProtocolUtils jt808ProtocolUtils;

    public MsgEncoder() {
        this.bitOperator = new BitOperator();
        this.jt808ProtocolUtils = new JT808ProtocolUtils();
    }

    /**
     * 注册应答消息编码
     *
     * @param req
     * @param respMsgBody
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encode4TerminalRegisterResp(TerminalRegisterMsg req, TerminalRegisterMsgRespBody respMsgBody,
                                              int flowId) throws Exception {
        // 消息体字节数组
        byte[] msgBody = null;
        if (respMsgBody.getReplyCode() == TerminalRegisterMsgRespBody.success) {
            msgBody = this.bitOperator.concatAll(Arrays.asList(
                    // 流水号(2)
                    bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()),
                    // 结果
                    new byte[]{respMsgBody.getReplyCode()},
                    //平台编号
                    respMsgBody.getNum().getBytes(TPMSConsts.STRING_CHARSET),
                    //培训机构编号
                    respMsgBody.getInscode().getBytes(TPMSConsts.STRING_CHARSET),
                    //计时终端编号
                    respMsgBody.getDevnum().getBytes(TPMSConsts.STRING_CHARSET),
                    //证书口令
                    respMsgBody.getPass().getBytes(TPMSConsts.STRING_CHARSET),
                    //终端证书
                    respMsgBody.getCredential().getBytes(TPMSConsts.STRING_CHARSET)
            ));
        } else {
            msgBody = this.bitOperator.concatAll(Arrays.asList(
                    // 流水号(2)
                    bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()),
                    // 结果
                    new byte[]{respMsgBody.getReplyCode()}
            ));
        }
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),
                TPMSConsts.CMD_TERMINAL_REGISTER_RESP, msgBody, msgBodyProps, flowId,0,0);
        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 通用应答消息编码
     *
     * @param req
     * @param respMsgBody
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encode4ServerCommonRespMsg(PackageData req, ServerCommonRespMsgBody respMsgBody, int flowId)
            throws Exception {
        byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(
                // 应答流水号
                bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()),
                // 应答ID,对应的终端消息的ID
                bitOperator.integerTo2Bytes(respMsgBody.getReplyId()),
                // 结果
                new byte[]{respMsgBody.getReplyCode()}
        ));

        // 消息头
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),
                TPMSConsts.CMD_COMMON_RESP, msgBody, msgBodyProps, flowId,0,0);
        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    public byte[] encode4ParamSetting(byte[] msgBodyBytes, Session session) throws Exception {
        // 消息头
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBodyBytes.length, 0b000, false, 0);
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(session.getTerminalPhone(),
                TPMSConsts.CMD_TERMINAL_PARAM_SETTINGS, msgBodyBytes, msgBodyProps, session.currentFlowId(),0,0);
        // 连接消息头和消息体
        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBodyBytes);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    private byte[] doEncode(byte[] headerAndBody, int checkSum) throws Exception {
        byte[] noEscapedBytes = this.bitOperator.concatAll(Arrays.asList(
                // 0x7e
                new byte[]{TPMSConsts.PKG_DELIMITER},
                // 消息头+ 消息体
                headerAndBody,
                // 校验码
                bitOperator.integerTo1Bytes(checkSum),
                // 0x7e
                new byte[]{TPMSConsts.PKG_DELIMITER}
        ));
        // 转义
        return jt808ProtocolUtils.doEscape4Send(noEscapedBytes, 1, noEscapedBytes.length - 2);
    }

    /**
     * 封装教练登录应答消息
     *
     * @param msg
     * @param respMsgBody
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encodeCoachLoginResp(ExtendedTimekeepingTrainingMsg msg, CoachLoginMsgRespBody respMsgBody, int flowId) throws Exception {
        // 教练应答字节数组
        byte[] coachBody =  this.bitOperator.concatAll(Arrays.asList(
                // 结果
                new byte[]{respMsgBody.getReplyCode()},
                //教练编号
                respMsgBody.getCoachnum().getBytes(TPMSConsts.STRING_CHARSET),
                //是否报读
                new byte[]{respMsgBody.getIsRead()},
                //附加消息长度
                bitOperator.integerTo1Bytes(respMsgBody.getMsgLen()),
                //附加消息
                respMsgBody.getMsg().getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.COACH_LOGIN_RESP),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                msg.getExtendedTimekeepingTrainingInfo().getTerminalId().getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(coachBody.length),
                //数据内容
                coachBody,
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(msg.getMsgHeader().getTerminalPhone(),
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, flowId,0,0);
        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装教练登出应答消息
     *
     * @param msg
     * @param coachnum
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encodeCoachLoginOutResp(ExtendedTimekeepingTrainingMsg msg, String coachnum, int flowId) throws Exception {
        // 教练应答字节数组
        byte[] coachBody =  this.bitOperator.concatAll(Arrays.asList(
                // 结果
                bitOperator.integerTo1Bytes(1),
                //教练编号
                coachnum.getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.COACH_LOGIN_OUT_RESP),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                msg.getExtendedTimekeepingTrainingInfo().getTerminalId().getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(coachBody.length),
                //数据内容
                coachBody,
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));

        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(msg.getMsgHeader().getTerminalPhone(),
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, flowId,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装学员登录应答消息
     *
     * @param msg
     * @param respMsgBody
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encodeStuLoginResp(ExtendedTimekeepingTrainingMsg msg, StudentLoginMsgRespBody respMsgBody, int flowId) throws Exception {
        // 学员应答字节数组
        byte[] stuBody = null;
        stuBody = this.bitOperator.concatAll(Arrays.asList(
                // 结果
                new byte[]{respMsgBody.getReplyCode()},
                //学员编号
                respMsgBody.getStuhnum().getBytes(TPMSConsts.STRING_CHARSET),
                //总学时
                bitOperator.integerTo2Bytes(respMsgBody.getTotalHours()),
                //已完成学时
                bitOperator.integerTo2Bytes(respMsgBody.getFinishHours()),
                //总里程
                bitOperator.float2byte(respMsgBody.getTotalMileage()),
                //已完成里程
                bitOperator.float2byte(respMsgBody.getFinishMileage()),
                //是否报读
                new byte[]{respMsgBody.getIsRead()},
                //附加消息长度
                bitOperator.integerTo1Bytes(respMsgBody.getMsgLen()),
                //附加消息
                respMsgBody.getMsg().getBytes(TPMSConsts.STRING_CHARSET)
        ));

        //扩展消息字节数组
        byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.STU_LOGIN_RESP),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                msg.getExtendedTimekeepingTrainingInfo().getTerminalId().getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(stuBody.length),
                //数据内容
                stuBody,
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(msg.getMsgHeader().getTerminalPhone(),
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, flowId,0,0);
        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装学员登出应答
     *
     * @param msg
     * @param stunum
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encodeStuLoginOutResp(ExtendedTimekeepingTrainingMsg msg, String stunum, int flowId) throws Exception {
        // 学员应答字节数组
        byte[] stuBody =  this.bitOperator.concatAll(Arrays.asList(
                // 结果
                bitOperator.integerTo1Bytes(1),
                //学员编号
                stunum.getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.STU_LOGIN_OUT_RESP),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                msg.getExtendedTimekeepingTrainingInfo().getTerminalId().getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(stuBody.length),
                //数据内容
                stuBody,
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(msg.getMsgHeader().getTerminalPhone(),
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, flowId,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装照片上传初始化应答
     * @param msg
     * @param code
     * @param flowId
     * @return
     * @throws Exception
     */
    public byte[] encodePhotoUploadInitializesResp(ExtendedTimekeepingTrainingMsg msg, int code, int flowId) throws Exception{
        // 应答字节数组
        byte[] body = this.bitOperator.concatAll(Arrays.asList(
                // 结果
                bitOperator.integerTo1Bytes(code)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(0x8305),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                msg.getExtendedTimekeepingTrainingInfo().getTerminalId().getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(body.length),
                //数据内容
                body,
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));

        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(msg.getMsgHeader().getTerminalPhone(),
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, flowId,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 上报照片查询结果应答消息编码
     * @param msg
     * @param code
     * @param flowId
     * @return
     */
    public byte[] encodeUpQueryImageResp(ExtendedTimekeepingTrainingMsg msg, int code, int flowId)throws Exception {
        // 应答字节数组
        byte[] body = this.bitOperator.concatAll(Arrays.asList(
                // 结果
                bitOperator.integerTo1Bytes(code)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.UP_QUERY_IMAGE_RESP),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                msg.getExtendedTimekeepingTrainingInfo().getTerminalId().getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(body.length),
                //数据内容
                body,
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));

        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(msg.getMsgHeader().getTerminalPhone(),
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, flowId,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }
}
