package com.jt808.service;

import com.alibaba.fastjson.JSON;
import com.jt808.server.SessionManager;
import com.jt808.service.codec.MsgEncoder;
import com.jt808.vo.PackageData;
import com.jt808.vo.PackageData.MsgHeader;
import com.jt808.vo.Session;
import com.jt808.vo.req.*;
import com.jt808.vo.resp.CoachLoginMsgRespBody;
import com.jt808.vo.resp.ServerCommonRespMsgBody;
import com.jt808.vo.resp.StudentLoginMsgRespBody;
import com.jt808.vo.resp.TerminalRegisterMsgRespBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理终端消息
 *
 * @author ：刘洋
 */
public class TerminalMsgProcessService extends BaseMsgProcessService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private MsgEncoder msgEncoder;
    private SessionManager sessionManager;

    public TerminalMsgProcessService() {
        this.msgEncoder = new MsgEncoder();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * 终端注册
     * @param msg
     * @throws Exception
     */
    public void processRegisterMsg(TerminalRegisterMsg msg) throws Exception {
        log.debug("终端注册:{}", JSON.toJSONString(msg, true));

        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        String res = "0";
        TerminalRegisterMsgRespBody respMsgBody = new TerminalRegisterMsgRespBody();
        //返回消息
        //注册应答
        if ("0".equals(res)) {
            Map<String, Object> map = new HashMap<>();
            map.put("number", "01");//应答流水号
            map.put("platformCode", "12345");//平台编号
            map.put("institutionCode", "2000200030004000");//培训机构编号
            map.put("deviceCode", "3000200030004000");//计时终端编号
            map.put("pwd", "400010002000");//证书口令
            map.put("zhengshu", "312312313ffasfafsa");//终端证书
            respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.success);
            respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
            respMsgBody.setNum(map.get("platformCode").toString());
            respMsgBody.setDevnum(map.get("deviceCode").toString());
            respMsgBody.setInscode(map.get("institutionCode").toString());
            respMsgBody.setPass(map.get("pwd").toString());
            respMsgBody.setCredential(map.get("zhengshu").toString());
        } else if ("1".equals(res)) {
            respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.car_already_registered);
            respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        } else if ("2".equals(res)) {
            respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.car_not_found);
            respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        } else if ("3".equals(res)) {
            respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.terminal_already_registered);
            respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        } else if ("4".equals(res)) {
            respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.terminal_not_found);
            respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        }
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4TerminalRegisterResp(msg, respMsgBody, flowId);
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 终端鉴权
     * @param msg
     * @throws Exception
     */
    public void processAuthMsg(TerminalAuthenticationMsg msg) throws Exception {
        // TODO 暂时每次鉴权都成功
        log.debug("终端鉴权:{}", JSON.toJSONString(msg, true));
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);

        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody();
        respMsgBody.setReplyCode(ServerCommonRespMsgBody.success);
        respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        respMsgBody.setReplyId(msg.getMsgHeader().getMsgId());
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 心跳信息 ===>>>回复通用应答
     * @param req
     * @throws Exception
     */
    public void processTerminalHeartBeatMsg(PackageData req) throws Exception {
        log.debug("心跳信息:{}", JSON.toJSONString(req, true));
        final MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        super.send2Client(req.getChannel(), bs);
    }

    /**
     * 终端注销  ===>>>回复通用应答
     * @param req
     * @throws Exception
     */
    public void processTerminalLogoutMsg(PackageData req) throws Exception {
        log.info("终端注销:{}", JSON.toJSONString(req, true));
        final MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        super.send2Client(req.getChannel(), bs);
    }

    /**
     * 位置信息  ===>>>回复通用应答
     * @param req
     * @throws Exception
     */
    public void processLocationInfoUploadMsg(LocationInfoUploadMsg req) throws Exception {
        log.debug("位置 信息:{}", JSON.toJSONString(req, true));
        final MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        super.send2Client(req.getChannel(), bs);
    }

    /**
     * 教练登录 ===>>>> 教练登录应答
     *
     * @param msg
     * @param coachLoginMsg
     * @throws Exception
     */
    public void coachLoginMsg(ExtendedTimekeepingTrainingMsg msg, CoachLoginMsg coachLoginMsg) throws Exception {
        log.debug("教练登录:{}", JSON.toJSONString(coachLoginMsg, true));
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        //封装应答消息
        CoachLoginMsgRespBody respMsgBody = new CoachLoginMsgRespBody();
        respMsgBody.setReplyCode(CoachLoginMsgRespBody.success);
        respMsgBody.setCoachnum(coachLoginMsg.getCoachnum());
        if (respMsgBody.getReplyCode() == 2) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("无效的教练员编号");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else if (respMsgBody.getReplyCode() == 3) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("准教车型不符");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else if (respMsgBody.getReplyCode() == 9) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("其他错误");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.no_read);
            respMsgBody.setMsg("");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        }
        byte[] bs = this.msgEncoder.encodeCoachLoginResp(msg, respMsgBody, msg.getMsgHeader().getFlowId());
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 教练登出 ===>>>> 教练登出应答
     *
     * @param msg
     * @param coachLoginMsg
     * @throws Exception
     */
    public void coachLoginOutMsg(ExtendedTimekeepingTrainingMsg msg, CoachLoginMsg coachLoginMsg) throws Exception {
        log.debug("教练登出:{}", JSON.toJSONString(coachLoginMsg, true));
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        String coachnum = coachLoginMsg.getCoachnum();
        byte[] bs = this.msgEncoder.encodeCoachLoginOutResp(msg, coachnum, msg.getMsgHeader().getFlowId());
        super.send2Client(msg.getChannel(), bs);

    }

    /**
     * 学员登录 ===>>>> 学员登录应答
     *
     * @param msg
     * @param studentLoginMsg
     * @throws Exception
     */
    public void studentLoginMsg(ExtendedTimekeepingTrainingMsg msg, StudentLoginMsg studentLoginMsg) throws Exception {
        log.debug("学员登录:{}", JSON.toJSONString(studentLoginMsg, true));
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        //封装应答消息
        StudentLoginMsgRespBody respMsgBody = new StudentLoginMsgRespBody();
        respMsgBody.setReplyCode(StudentLoginMsgRespBody.success);
        respMsgBody.setStuhnum(studentLoginMsg.getStunum());
        respMsgBody.setTotalHours(800);
        respMsgBody.setFinishHours(600);
        respMsgBody.setTotalMileage(100.5f);
        respMsgBody.setFinishMileage(80.6f);
        if (respMsgBody.getReplyCode() == 2) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("无效的学员编号");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else if (respMsgBody.getReplyCode() == 3) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("禁止登录的学员");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else if (respMsgBody.getReplyCode() == 4) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("区域外教学提醒");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else if (respMsgBody.getReplyCode() == 5) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("准教车型与培训车型不符");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else if (respMsgBody.getReplyCode() == 9) {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.yes_read);
            respMsgBody.setMsg("其他错误");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        } else {
            respMsgBody.setIsRead(StudentLoginMsgRespBody.no_read);
            respMsgBody.setMsg("");
            respMsgBody.setMsgLen(respMsgBody.getMsg().length());
        }
        byte[] bs = this.msgEncoder.encodeStuLoginResp(msg, respMsgBody, msg.getMsgHeader().getFlowId());
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 学员登出 ===>>>> 学员登出应答
     *
     * @param msg
     * @param studentLoginMsg
     * @throws Exception
     */
    public void studentLoginOutMsg(ExtendedTimekeepingTrainingMsg msg, StudentLoginOutMsg studentLoginMsg) throws Exception {
        log.debug("学员登出:{}", JSON.toJSONString(studentLoginMsg, true));
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        String stunum = studentLoginMsg.getStunum();
        byte[] bs = this.msgEncoder.encodeStuLoginOutResp(msg, stunum, msg.getMsgHeader().getFlowId());
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 上报学时记录 =====>>>>> 通用应答
     * @param msg
     * @param upPeriodRecordMsg
     * @throws Exception
     */
    public void upPeriodRecordMsg(ExtendedTimekeepingTrainingMsg msg, UpPeriodRecordMsg upPeriodRecordMsg) throws Exception{
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        final MsgHeader reqHeader = msg.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 照片上传初始化
     * @param msg
     * @param photoUploadInitializesMsg
     * @throws Exception
     */
    public void photoUploadInitializesMsg(ExtendedTimekeepingTrainingMsg msg, PhotoUploadInitializesMsg photoUploadInitializesMsg) throws Exception{
        log.debug("照片上传初始化:{}", JSON.toJSONString(photoUploadInitializesMsg, true));
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        int code = 0;
        byte[] bs = this.msgEncoder.encodePhotoUploadInitializesResp(msg, code, msg.getMsgHeader().getFlowId());
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 上传照片数据包
     * @param msg
     * @param photoUploadDataMsg
     * @throws Exception
     */
    public void photoUploadDataMsg(ExtendedTimekeepingTrainingMsg msg, PhotoUploadDataMsg photoUploadDataMsg)throws Exception {
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        //通用应答
        final MsgHeader reqHeader = msg.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
        super.send2Client(msg.getChannel(), bs);
    }

    /**
     * 上报照片查询结果应答
     * @param msg
     * @param upQueryImageMsg
     * @throws Exception
     */
    public void upQueryImageMsg(ExtendedTimekeepingTrainingMsg msg, UpQueryImageMsg upQueryImageMsg) throws Exception{
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        int code = upQueryImageMsg.getReplyCode();
        byte[] bs = this.msgEncoder.encodeUpQueryImageResp(msg, code, msg.getMsgHeader().getFlowId());
        super.send2Client(msg.getChannel(), bs);
    }
    /**
     * 获取教练或学员照片 === 应答
     * @param msg
     * @param data
     * @throws Exception
     */
    public void coachOrStuPhotoMsg(ExtendedTimekeepingTrainingMsg msg, byte[] data) throws Exception{
        log.debug("发送照片");
        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        //消息包总数
        int msgTotalNum = (int) Math.ceil(data.length / 768f);
        //包序号
        int packageNum = 0;
        int len =768;
        if(data.length>768){
            for(int i=0;i<msgTotalNum;i++){
                packageNum++;
                //最后剩余数据
                if((i+1)*768>data.length){
                    len = data.length-(i*768);
                }
                byte[] tem = new byte[len];
                System.arraycopy(data,i*768,tem,0,tem.length);
                byte[] bs = this.msgEncoder.encodeCoachOrStuPhotoResp(msg, msgTotalNum,packageNum,tem.length,tem, msg.getMsgHeader().getFlowId());
                super.send2Client(msg.getChannel(), bs);
            }
        }else {
            byte[] bs = this.msgEncoder.encodeCoachOrStuPhotoResp(msg, 0,0,data.length,data, msg.getMsgHeader().getFlowId());
            super.send2Client(msg.getChannel(), bs);
        }

    }
}
