package com.jt808.service.handler;

import com.alibaba.fastjson.JSON;
import com.jt808.util.Img2Base64Util;
import com.jt808.common.TPMSConsts;
import com.jt808.server.ChannelMap;
import com.jt808.server.LoopBuffer;
import com.jt808.server.SessionManager;
import com.jt808.service.TerminalMsgProcessService;
import com.jt808.service.codec.MsgDecoder;
import com.jt808.util.DigitalUtils;
import com.jt808.vo.PackageData;
import com.jt808.vo.PackageData.MsgHeader;
import com.jt808.vo.Session;
import com.jt808.vo.req.*;
import com.jt808.vo.resp.QueryTerminalArgRespMsgBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :liuyang
 * 将接收到的消息进行处理，截取消息头和消息体，判断消息头中消息id，
 * 根据消息id将消息体数据进行解码
 */
public class TCPServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionManager sessionManager;
    private final MsgDecoder decoder;
    private TerminalMsgProcessService msgProcessService;
    private LoopBuffer loopBuffer;
    private Map<Long, byte[]> map;

    public TCPServerHandler() {
        this.sessionManager = SessionManager.getInstance();
        this.decoder = new MsgDecoder();
        this.msgProcessService = new TerminalMsgProcessService();
        this.loopBuffer = LoopBuffer.getInstance();
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * 接收消息
     *
     * @param ctx
     * @param msg
     * @throws InterruptedException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        try {
            ByteBuf buf = (ByteBuf) msg;
            if (buf.readableBytes() <= 0) {
                return;
            }
            byte[] bs = new byte[buf.readableBytes()];
            buf.readBytes(bs);
            bs = DigitalUtils.meanTransfer(bs);
            logger.info("数据length bs:{},数据 bs:{}", bs.length, bs);
            // 字节数据转换为针对于808消息结构的实体类
            PackageData pkg = this.decoder.bytes2PackageData(bs);
            // 引用channel,以便回送数据给硬件
            pkg.setChannel(ctx.channel());
            String phone = pkg.getMsgHeader().getTerminalPhone();
            //手机号码长度补全统一长度16
            phone = String.format("%016d", Long.parseLong(phone));
            //使用手机号码作为key，将对应的连接通道保存
            ChannelMap.addChannel(phone, ctx.channel());
            this.processPackageData(pkg);
        } finally {
            release(msg);
        }
    }


    /**
     * 处理业务逻辑
     *
     * @param packageData
     */
    private void processPackageData(PackageData packageData) {
        final MsgHeader header = packageData.getMsgHeader();
        // 1. 终端心跳-消息体为空 ==> 平台通用应答
        if (TPMSConsts.MSG_ID_TERMINAL_HEART_BEAT == header.getMsgId()) {
            logger.info(">>>>>[终端心跳],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                this.msgProcessService.processTerminalHeartBeatMsg(packageData);
            } catch (Exception e) {
                logger.error("<<<<<[终端心跳]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 2. 终端鉴权 ==> 平台通用应答
        else if (TPMSConsts.MSG_ID_TERMINAL_AUTHENTICATION == header.getMsgId()) {
            logger.info(">>>>>[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                TerminalAuthenticationMsg authenticationMsg = this.decoder.toTerminalAuthenticationMsg(packageData);
                this.msgProcessService.processAuthMsg(authenticationMsg);
                logger.info("终端鉴权:{}", JSON.toJSONString(authenticationMsg, true));
                logger.info("<<<<<[终端鉴权],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端鉴权]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 3. 终端注册 ==> 终端注册应答
        else if (TPMSConsts.MSG_ID_TERMINAL_REGISTER == header.getMsgId()) {
            logger.info(">>>>>[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                TerminalRegisterMsg msg = this.decoder.toTerminalRegisterMsg(packageData);
                this.msgProcessService.processRegisterMsg(msg);
                logger.info("<<<<<[终端注册],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端注册]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 4. 终端注销(终端注销数据消息体为空) ==> 平台通用应答
        else if (TPMSConsts.MSG_ID_TERMINAL_LOG_OUT == header.getMsgId()) {
            logger.info(">>>>>[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                this.msgProcessService.processTerminalLogoutMsg(packageData);
                logger.info("<<<<<[终端注销],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[终端注销]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 5. 位置信息汇报 ==> 平台通用应答
        else if (TPMSConsts.MSG_ID_TERMINAL_LOCATION_INFO_UPLOAD == header.getMsgId()) {
            logger.info(">>>>>[位置信息],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                LocationInfoUploadMsg locationInfoUploadMsg = this.decoder.toLocationInfoUploadMsg(packageData);
                this.msgProcessService.processLocationInfoUploadMsg(locationInfoUploadMsg);
                //logger.info("位置信息:{}", JSON.toJSONString(locationInfoUploadMsg, true));
            } catch (Exception e) {
                logger.error("<<<<<[位置信息]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 6. 位置信息查询应答
        else if (TPMSConsts.QUERY_LOCATION_RESP == header.getMsgId()) {
            logger.info(">>>>>[位置信息查询应答],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            logger.info(">>>>>[位置信息查询应答],data={}", packageData);
            try {
                LocationInfoUploadMsg locationInfoUploadMsg = this.decoder.toLocationInfoUploadMsg(packageData);
                this.msgProcessService.processLocationInfoUploadMsg(locationInfoUploadMsg);
                logger.info("位置信息查询应答:{}", JSON.toJSONString(locationInfoUploadMsg, true));
                logger.info("<<<<<[位置信息查询应答],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[位置信息查询应答]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 7. 扩展计时培训消息
        else if (TPMSConsts.DATA_UP == header.getMsgId()) {
            //logger.info(">>>>>[数据上行],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            try {
                ExtendedTimekeepingTrainingMsg msg = this.decoder.toExtendedTimekeepingTrainingMsg(packageData);
                processExtendedMsg(msg);
                // logger.info("<<<<<[数据上行],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                logger.error("<<<<<[数据上行]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 8. 查询终端参数应答
        else if (TPMSConsts.MSG_ID_TERMINAL_PARAM_QUERY_RESP == header.getMsgId()) {
            logger.info(">>>>>[查询终端参数应答],phone={},flowid={}", header.getTerminalPhone(), header.getFlowId());
            QueryTerminalArgRespMsgBody msg = null;
            byte[] msgBodyByte = packageData.getMsgBodyBytes();
            byte[] tmp = new byte[msgBodyByte.length - 4];
            System.arraycopy(msgBodyByte, 4, tmp, 0, tmp.length);
            byte[] msg4 = new byte[4];
            System.arraycopy(msgBodyByte, 0, msg4, 0, msg4.length);
            try {
                if (header.isHasSubPackage()) {
                    loopBuffer.write(tmp);
                    if (header.getSubPackageSeq() == header.getTotalSubPackage()) {
                        msg = this.decoder.toQueryTerminalArgRespMsg(msg4, loopBuffer.read(loopBuffer.count()));
                        loopBuffer.remove(loopBuffer.count());
                        logger.info("查询终端参数应答:{}", msg.getTerminalArgList());
                    }
                } else {
                    msg = this.decoder.toQueryTerminalArgRespMsg(msg4, tmp);
                    logger.info("查询终端参数应答:{}", msg.getTerminalArgList());
                }
            } catch (Exception e) {
                logger.error("<<<<<[查询终端参数应答]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 其他情况
        else {
            logger.error(">>>>>>[未知消息类型],phone={},msgId={},package={}", header.getTerminalPhone(), header.getMsgId(),
                    packageData);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        logger.error("发生异常:{}", cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = Session.buildSession(ctx.channel());
        sessionManager.put(session.getId(), session);
        logger.debug("终端连接:{}", session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asLongText();
        Session session = sessionManager.findBySessionId(sessionId);
        this.sessionManager.removeBySessionId(sessionId);
        logger.debug("终端断开连接:{}", session);
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
                logger.error("服务器主动断开连接:{}", session);
                ctx.close();
            }
        }
    }

    private void release(Object msg) {
        try {
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param msg
     * @throws Exception 处理扩展消息上行数据
     */
    private void processExtendedMsg(ExtendedTimekeepingTrainingMsg msg) throws Exception {
        //logger.info("扩展消息:{}", JSON.toJSONString(msg, true));
        byte[] dataCon = msg.getExtendedTimekeepingTrainingInfo().getDataContent();
        //教练登录
        if (TPMSConsts.COACH_LOGIN == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            CoachLoginMsg coachLoginMsg = this.decoder.toCoachLoginMsg(dataCon);
            this.msgProcessService.coachLoginMsg(msg, coachLoginMsg);
        }
        //教练登出
        else if (TPMSConsts.COACH_LOGIN_OUT == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            CoachLoginMsg coachLoginMsg = this.decoder.toCoachLoginOutMsg(dataCon);
            this.msgProcessService.coachLoginOutMsg(msg, coachLoginMsg);
        }
        //学员登录
        else if (TPMSConsts.STU_LOGIN == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            StudentLoginMsg studentLoginMsg = this.decoder.toStudentLoginMsg(dataCon);
            this.msgProcessService.studentLoginMsg(msg, studentLoginMsg);
        }
        //学员登出
        else if (TPMSConsts.STU_LOGIN_OUT == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            StudentLoginOutMsg studentLoginMsg = this.decoder.studentLoginOutMsg(dataCon);
            this.msgProcessService.studentLoginOutMsg(msg, studentLoginMsg);
        }
        //上报学时记录
        else if (TPMSConsts.UP_PERIOD_RECORD == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            UpPeriodRecordMsg upPeriodRecordMsg = this.decoder.upPeriodRecordMsg(dataCon);
            this.msgProcessService.upPeriodRecordMsg(msg, upPeriodRecordMsg);
        }
        //照片上传初始化
        else if (TPMSConsts.PHOTO_UPLOAD_INITIALIZES == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            PhotoUploadInitializesMsg photoUploadInitializesMsg = this.decoder.photoUploadInitializesMsg(dataCon);
            this.msgProcessService.photoUploadInitializesMsg(msg, photoUploadInitializesMsg);
        }
        //上传照片数据包
        else if (TPMSConsts.PHOTO_UPLOAD_DATA == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            PhotoUploadDataMsg photoUploadDataMsg = null;
            logger.info("照片数据:{}", dataCon);
            try {
                if (msg.getMsgHeader().isHasSubPackage()) {
                    //包总数
                    logger.info("包总数:{}", msg.getMsgHeader().getTotalSubPackage(), msg.getMsgHeader().getSubPackageSeq());
                    //包序号
                    logger.info("包序号:{}", msg.getMsgHeader().getSubPackageSeq());
                    //照片编号
                    byte[] tmp = new byte[10];
                    System.arraycopy(dataCon, 0, tmp, 0, tmp.length);
                    logger.info("照片编号:{}", tmp);
                    //照片数据
                    byte[] bs = new byte[dataCon.length - 10];
                    System.arraycopy(dataCon, 10, bs, 0, bs.length);
                    map.put(msg.getMsgHeader().getSubPackageSeq(), bs);
                    logger.info("map.size():{}", map.size());
                    if (map.size() == msg.getMsgHeader().getTotalSubPackage()) {
                        for (int i = 0; i < map.size(); i++) {
                            loopBuffer.write(map.get(i + 1L));
                        }
                        logger.info("数据长度:{}", loopBuffer.count());
                        photoUploadDataMsg = this.decoder.photoUploadDataPckMsg(tmp, loopBuffer.read(loopBuffer.count()));
                        Img2Base64Util.generateImage(photoUploadDataMsg.getPhotoData(),"D:\\"+photoUploadDataMsg.getPhotoNum()+".jpg");
                        logger.info("上传照片数据包:{}", JSON.toJSONString(photoUploadDataMsg, true));
                        loopBuffer.remove(loopBuffer.count());
                        map.clear();
                    }
                } else {
                    photoUploadDataMsg = this.decoder.photoUploadDataMsg(dataCon);
                    Img2Base64Util.generateImage(photoUploadDataMsg.getPhotoData(),"D:\\"+photoUploadDataMsg.getPhotoNum()+".jpg");
                    logger.info("上传照片数据包:{}", JSON.toJSONString(photoUploadDataMsg, true));
                }
            } catch (Exception e) {
                logger.error("<<<<<[上传照片数据包]处理错误,phone={},flowid={},err={}", msg.getMsgHeader().getTerminalPhone(), msg.getMsgHeader().getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
            this.msgProcessService.photoUploadDataMsg(msg, photoUploadDataMsg);
        }
        //查询计时终端应用参数应答
        else if (TPMSConsts.QUERY_TERMINAL_APPLY_ARG == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            TerminalApplyArgMsg terminalApplyArgMsg = this.decoder.queryTerminalApplyArgRespBody(dataCon);
            logger.info("查询计时终端应用参数应答:{}", JSON.toJSONString(terminalApplyArgMsg, true));

        }
        //设置计时终端应用参数应答
        else if (TPMSConsts.SET_TRAINING_APPLY_ARG_RESP == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            int code = this.decoder.setTerminalApplyArgRespBody(dataCon);
            if (code == 1) {
                logger.info("设置计时终端应用参数应答:【设置成功】");
            } else {
                logger.info("设置计时终端应用参数应答:【设置失败】");
            }
        }
        //设置禁训状态消息应答
        else if (TPMSConsts.FORBIDDEN_STATE_RESP == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            ForbiddenState forbiddenState = this.decoder.setForbiddenStateRespBody(dataCon);
            int code = forbiddenState.getReplyCode();
            if (code == 1) {
                logger.info("设置计时终端应用参数应答:【设置成功】");
            } else if (code == 0) {
                logger.info("设置计时终端应用参数应答:【默认应答】");
            } else {
                logger.info("设置计时终端应用参数应答:【设置失败】");
            }
        }
        //命令上传学时记录应答
        else if (TPMSConsts.UP_PERIOD_RECORD_ORDER_RESP == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            int code = this.decoder.setUpPeriodRecordOrder(dataCon);
            if (code == 1) {
                logger.info("命令上传学时记录应答:【查询的记录正在上传】");
            } else if (code == 2) {
                logger.info("命令上传学时记录应答:【SD卡没有找到】");
            } else if (code == 3) {
                logger.info("命令上传学时记录应答:【执行成功，但无指定记录】");
            } else if (code == 4) {
                logger.info("命令上传学时记录应答:【执行成功，稍后上报查询结果】");
            }
        }
        //立即拍照应答
        else if (TPMSConsts.IMMEDIATELY_TAKE_PICTURES_RESP == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            ImmediatelyTakePicturesMsg resp = this.decoder.toImmediatelyTakePictures(dataCon);
            logger.info("立即拍照应答:{}", JSON.toJSONString(resp, true));
        }
        //查询照片应答
        else if (TPMSConsts.QUERY_IMAGE_RESP == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            int code = this.decoder.toQueryImage(dataCon);
            if (code == 1) {
                logger.info("命令上传学时记录应答:【执行成功】");
            } else {
                logger.info("命令上传学时记录应答:【执行失败】");
            }
        }
        //上报照片查询结果 === 应答
        else if (TPMSConsts.UP_QUERY_IMAGE == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            UpQueryImageMsg upQueryImageMsg = this.decoder.toUpQueryImage(dataCon);
            upQueryImageMsg.setReplyCode(0);
            logger.debug("上报照片查询结果:{}", JSON.toJSONString(upQueryImageMsg, true));
            this.msgProcessService.upQueryImageMsg(msg, upQueryImageMsg);
        }
        //上传指定照片应答
        else if (TPMSConsts.UP_APPOINT_IMAGE_RESP == msg.getExtendedTimekeepingTrainingInfo().getOspfId()) {
            int code = this.decoder.toUpAppointImageResp(dataCon);
            if (code == 0) {
                logger.info("上传指定照片应答:【找到照片，稍后上传】");
            } else {
                logger.info("上传指定照片应答:【没有找到该照片】");
            }
        }
    }
}