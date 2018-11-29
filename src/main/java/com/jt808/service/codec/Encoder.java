package com.jt808.service.codec;

import com.jt808.common.TPMSConsts;
import com.jt808.util.BCD8421Operater;
import com.jt808.util.BitOperator;
import com.jt808.util.JT808ProtocolUtils;
import com.jt808.util.TerminalArgUtils;
import com.jt808.vo.req.*;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg.TerminalArgList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author :liuyang
 * 消息编码器：将封装的消息编码成byte[]返回
 */
public class Encoder {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private BitOperator bitOperator;
    private BCD8421Operater bcd8421Operater;
    private JT808ProtocolUtils jt808ProtocolUtils;
    private TerminalArgUtils terminalArgUtils;

    public Encoder() {
        this.bitOperator = new BitOperator();
        this.jt808ProtocolUtils = new JT808ProtocolUtils();
        this.bcd8421Operater = new BCD8421Operater();
        this.terminalArgUtils = new TerminalArgUtils();
    }

    /**
     * 组装数据
     * 标识符+消息头+消息体+校验码+标识符
     * @param headerAndBody
     * @param checkSum
     * @return
     * @throws Exception
     */
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
     * 封装平台登录消息
     *
     * @param resp
     * @return
     * @throws Exception
     */
    public byte[] encode4Resp(PlatformLoginMsg.PlatformLoginInfo resp) throws Exception {
        byte[] body = null;
        body = this.bitOperator.concatAll(Arrays.asList(
                // 平台编号
                resp.getNum().getBytes(TPMSConsts.STRING_CHARSET),
                //密码
                resp.getPass().getBytes(TPMSConsts.STRING_CHARSET),
                //接入码
                resp.getCode().getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(body.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.LOGIN, body, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装平台登出消息
     *
     * @param resp
     * @return
     * @throws Exception
     */
    public byte[] encode4OutResp(PlatformLoginMsg.PlatformLoginInfo resp) throws Exception {
        byte[] body = null;
        body = this.bitOperator.concatAll(Arrays.asList(
                // 平台编号
                resp.getNum().getBytes(TPMSConsts.STRING_CHARSET),
                //密码
                resp.getPass().getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(body.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.OUT, body, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装设置终端参数消息
     * @param resp
     * @param msgTotalNum
     * @param packageNum
     * @return
     * @throws Exception
     */
    public byte[] encode4TerminalArgResp(TerminalArg resp,int msgTotalNum ,int packageNum) throws Exception {
        byte[] body = null;
        List<byte[]> argBody = new ArrayList<>();
        for (TerminalArgList terminalArgList:resp.getTerminalArgList()) {
              argBody.add(this.terminalArgUtils.terminalArgToByte(terminalArgList.getArgId(),terminalArgList.getArg()));
        }
        body = this.bitOperator.concatAll(Arrays.asList(
                //参数总数
                bitOperator.integerTo1Bytes(resp.getArgTotal()),
                //分包参数个数
                bitOperator.integerTo1Bytes(resp.getArgNum()),
                //参数项列表
                bitOperator.concatAll(argBody)
        ));

        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(body.length, 0b000, true, 0);

        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.SETTING_TERMINAL_ARG, body, msgBodyProps, 0,msgTotalNum,packageNum);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 当list为空时
     * 封装查寻终端参数
     * 当list不为空时
     * 封装查询指定终端参数
     *
     * @param argIdList
     * @return
     * @throws Exception
     */
    public byte[] encode4queryTerminalArgResp(List<Integer> argIdList) throws Exception {
        byte[] body = null;
        int bodylen =0;
        //消息id
        int msgType = TPMSConsts.QUERY_TERMINAL_ARG;
        if (null != argIdList && argIdList.size() > 0) {
            msgType = TPMSConsts.QUERY_APPOINT_TERMINAL_ARG;
            List<byte[]> bytes = new ArrayList<>();
            for (int i : argIdList) {
                bytes.add(bitOperator.integerTo4Bytes(i));
                logger.info("原id数据:{}", i);
                logger.info("转换id数据:{}", bitOperator.integerTo4Bytes(i));
            }
            logger.info("转换的参数id数据:{}", bitOperator.concatAll(bytes));
            body = this.bitOperator.concatAll(Arrays.asList(
                    //参数总数
                    bitOperator.integerTo1Bytes(argIdList.size()),
                    //参数id列表
                    bitOperator.concatAll(bytes)
            ));
            bodylen = body.length;
        }
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(bodylen, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                msgType, body, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }
    /**
     * 封装临时位置跟踪控制消息
     *
     * @param interval
     * @param validity
     * @return
     * @throws Exception
     */
    public byte[] encode4TemporaryPositionTrackingResp(int interval, int validity) throws Exception {
        byte[] body = this.bitOperator.concatAll(Arrays.asList(
                //参数总数
                bitOperator.integerTo2Bytes(interval),
                //参数id列表
                bitOperator.integerTo4Bytes(validity)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(body.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.TEMPORARY_POSITION_TRACKING_RESP, body, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 封装命令上报学时记录消息
     * @param way
     * @param startTime
     * @param endTime
     * @param num
     * @return
     * @throws Exception
     */
    public byte[] encode4UpPeriodRecordResp(int way, Date startTime, Date endTime, int num) throws Exception {
        DateFormat format = new SimpleDateFormat("yyMMddHHmmss");

        byte[] body = this.bitOperator.concatAll(Arrays.asList(
                //查询方式
                bitOperator.integerTo1Bytes(way),
                //查询起始时间
                bcd8421Operater.string2Bcd(format.format(startTime)),
                //查询终止时间
                bcd8421Operater.string2Bcd(format.format(endTime)),
                //查询条数
                bitOperator.integerTo2Bytes(num)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.UP_PERIOD_RECORD_RESP),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "0000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
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
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 查询计时终端应用参数，消息体为空
     * @return
     * @throws Exception
     */
    public byte[] encode4QueryTerminalApplyArgResp() throws Exception{
        byte[] body =null;
        //扩展消息字节数组
        byte[] msgBody = null;
        msgBody = this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.QUERY_TERMINAL_APPLY_ARG_MSG),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
                //数据长度
                bitOperator.integerTo2Bytes(0),
                //数据内容
                "".getBytes(TPMSConsts.STRING_CHARSET),
                //校验串
                "123465789".getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 设置计时终端应用参数
     * @param req
     * @return
     * @throws Exception
     */
    public byte[] encode4SetTerminalApplyArgMsg(TerminalApplyArgMsg req) throws Exception{
        byte[] body =this.bitOperator.concatAll(Arrays.asList(
                //参数编号
                bitOperator.integerTo1Bytes(req.getArgNum()),
                //定时拍照时间间隔
                bitOperator.integerTo1Bytes(req.getTimedPhotoIntervals()),
                //照片上传设置
                bitOperator.integerTo1Bytes(req.getPhotoUploadSettings()),
                //是否报读附加消息
                bitOperator.integerTo1Bytes(req.getIsRead()),
                //熄火后停止学时计时的延时时间
                bitOperator.integerTo1Bytes(req.getDelayedTime()),
                //熄火后Gnss数据包上传间隔
                bitOperator.integerTo2Bytes(req.getGnssUpInterval()),
                //熄火后教练自动登出的延时时间
                bitOperator.integerTo2Bytes(req.getCoachLoginDelayedTime()),
                //重新验证身份时间
                bitOperator.integerTo2Bytes(req.getRevalidate()),
                //教练跨校教学
                bitOperator.integerTo1Bytes(req.getAcrossSchoolTeaching()),
                //学员跨校学习
                bitOperator.integerTo1Bytes(req.getAcrossSchoolLearning())
        ));
        //扩展消息字节数组
        byte[] msgBody = null;
        msgBody = this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.SET_TRAINING_APPLY_ARG_MSG),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "0000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
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
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 设置禁训状态消息编码
     * @param req
     * @return
     * @throws Exception
     */
    public byte[] encode4setForbiddenStateMsg(ForbiddenState req) throws Exception{
        byte[] body =this.bitOperator.concatAll(Arrays.asList(
                //禁训状态
                bitOperator.integerTo1Bytes(req.getState()),
                //提示消息长度
                bitOperator.integerTo1Bytes(req.getMsgLen()),
                //提示消息内容
                req.getMsg().getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //扩展消息字节数组
        byte[] msgBody = null;
        msgBody = this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.FORBIDDEN_STATE_MSG),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "0000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
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
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 终端控制
     * @param req
     * @return
     * @throws Exception
     */
    public byte[] encode4TerminalControlMsg(TerminalControlMsg req) throws Exception{
        byte[] body = null;
       /* body =  this.bitOperator.concatAll(Arrays.asList(
                //禁训状态
                bitOperator.integerTo1Bytes(req.getState()),
                //提示消息长度
                bitOperator.integerTo1Bytes(req.getMsgLen()),
                //提示消息内容
                req.getMsg().getBytes(TPMSConsts.STRING_CHARSET)
        ));*/
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(body.length, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.CMD_TERMINAL_CONTROL, body, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 位置信息查询，消息体位空
     * @return
     * @throws Exception
     */
    public byte[] encode4queryLocation() throws Exception{
        byte[] body =null;
        //消息体属性
        int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(0, 0b000, false, 0);
        // 消息头
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.QUERY_LOCATION_MSG, body, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, body);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 立即拍照
     * @return
     * @throws Exception
     */
    public byte[] encode4ImmediatelyTakePictures(ImmediatelyTakePicturesMsg req) throws Exception {
        byte[] body =this.bitOperator.concatAll(Arrays.asList(
                //上传模式
                bitOperator.integerTo1Bytes(req.getModel()),
                //摄像头通道号
                bitOperator.integerTo1Bytes(req.getAisle()),
                //图片尺寸
                bitOperator.integerTo1Bytes(req.getSize())
        ));
        //扩展消息字节数组
        byte[] msgBody = null;
        msgBody = this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.IMMEDIATELY_TAKE_PICTURES),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "0000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
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
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 查询照片
     * @param way 查询方式
     * @param startTime 查询开始时间
     * @param endTime 查询结束时间
     * @return
     * @throws Exception
     */
    public byte[] encode4QueryImage(int way, Date startTime, Date endTime) throws Exception{
        DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        byte[] body = this.bitOperator.concatAll(Arrays.asList(
                //查询方式
                bitOperator.integerTo1Bytes(way),
                //查询开始时间
                bcd8421Operater.string2Bcd(format.format(startTime)),
                //查询结束时间
                bcd8421Operater.string2Bcd(format.format(endTime))
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.QUERY_IMAGE),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "0000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
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
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }

    /**
     * 上传指定照片消息编码
     * @param imgNum 照片编号
     * @return
     * @throws Exception
     */
    public byte[] encode4UpAppointImage(String imgNum) throws Exception{
        byte[] body = this.bitOperator.concatAll(Arrays.asList(
                //照片编号
                imgNum.getBytes(TPMSConsts.STRING_CHARSET)
        ));
        //扩展消息字节数组
        byte[] msgBody =  this.bitOperator.concatAll(Arrays.asList(
                // 透传类型
                bitOperator.integerTo1Bytes(0),
                // 透传消息id
                bitOperator.integerTo2Bytes(TPMSConsts.UP_APPOINT_IMAGE),
                //扩展消息属性
                bitOperator.integerTo2Bytes(0),
                //驾培包序号
                bitOperator.integerTo2Bytes(1),
                //计时终端编号
                "0000000000000000".getBytes(TPMSConsts.STRING_CHARSET),
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
        byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader("0000000000000000",
                TPMSConsts.DATA_DOWN, msgBody, msgBodyProps, 0,0,0);

        byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
        // 校验码
        int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
        // 连接并且转义
        return this.doEncode(headerAndBody, checkSum);
    }
}
