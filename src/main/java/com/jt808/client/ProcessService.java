package com.jt808.client;

import com.jt808.service.codec.Encoder;
import com.jt808.vo.req.ForbiddenState;
import com.jt808.vo.req.ImmediatelyTakePicturesMsg;
import com.jt808.vo.req.TerminalApplyArgMsg;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg.TerminalArgList;
import com.jt808.vo.req.TerminalControlMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author ：刘洋
 * 业务处理：数据组装==>>编码==>>发送==>>处理返回数据
 */
public class ProcessService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Encoder encoder;
    private MsgSend msgSend;

    public ProcessService() {
        this.encoder = new Encoder();
        this.msgSend = new MsgSend();
    }

    /**
     * 设置终端参数 需要终端参数项列表 ok
     *
     * @param lists
     * @return
     */
    public String settingTerminalArg(List<TerminalArgList> lists) {
        //lists.size()>10则分包，否则不分包
        int len = 10;
        String msg = null;
        TerminalArg terminalArg = new TerminalArg();
        terminalArg.setArgTotal(lists.size());
        //消息包总数
        int msgTotalNum = (int) Math.ceil(lists.size() / 10f);
        //包序号
        int packageNum = 0;
        try {
            if (lists.size() > len) {
                int listSize = lists.size();
                int toIndex = 10;
                for (int i = 0; i < lists.size(); i += 10) {
                    //作用为toIndex最后没有10条数据则剩余几条newList中就装几条
                    if (i + 10 > listSize) {
                        toIndex = listSize - i;
                    }
                    List<TerminalArgList> newList = lists.subList(i, i + toIndex);
                    terminalArg.setArgNum(newList.size());
                    terminalArg.setTerminalArgList(newList);
                    packageNum++;
                    byte[] bs = this.encoder.encode4TerminalArgResp(terminalArg, msgTotalNum, packageNum);
                    msg = this.msgSend.send(bs, "000000000000123");
                    logger.info("第{}次发送{}", packageNum, msg);
                }
            } else {
                terminalArg.setTerminalArgList(lists);
                terminalArg.setArgNum(lists.size());
                byte[] bs = this.encoder.encode4TerminalArgResp(terminalArg, 0, 0);
                msg = this.msgSend.send(bs, "000000000000123");
                return msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 当list为空时
     * 封装查寻终端参数 ok
     * 当list不为空时
     * 封装查询指定终端参数 ok
     *
     * @param argIdList
     */
    public String queryTerminalArg(List<Integer> argIdList) {
        try {
            byte[] bs = this.encoder.encode4queryTerminalArgResp(argIdList);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 临时位置跟踪
     *
     * @param interval 时间间隔 单位 s
     * @param validity 有效期 单位 s
     */
    public String temporaryPositionTracking(int interval, int validity) {
        try {
            byte[] bs = this.encoder.encode4TemporaryPositionTrackingResp(interval, validity);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 命令上报学时记录 ok
     *
     * @param way       查询方式 1：按时间上传 2：按条数上传
     * @param startTime 查询起始时间 格式YYMMddHHmmss
     * @param endTime   查询终止时间 格式YYMMddHHmmss
     * @param num       查询条数
     */
    public String upPeriodRecord(int way, Date startTime, Date endTime, int num) {
        try {
            byte[] bs = this.encoder.encode4UpPeriodRecordResp(way, startTime, endTime, num);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置计时终端应用参数 ok
     *
     * @param terminalApplyArg
     * @return
     */
    public String setTerminalApplyArg(TerminalApplyArgMsg terminalApplyArg) {
        try {
            byte[] bs = this.encoder.encode4SetTerminalApplyArgMsg(terminalApplyArg);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询计时终端应用参数 ok
     *
     * @return
     */
    public String queryTerminalApplyArg() {
        try {
            byte[] bs = this.encoder.encode4QueryTerminalApplyArgResp();
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置禁训状态 ok
     *
     * @param forbiddenState
     * @return
     */
    public String setForbiddenState(ForbiddenState forbiddenState) {
        try {
            if (forbiddenState.getState() == 1) {
                forbiddenState.setMsg("可用");
            } else {
                forbiddenState.setMsg("禁用");
            }
            forbiddenState.setMsgLen(forbiddenState.getMsg().length());
            byte[] bs = this.encoder.encode4setForbiddenStateMsg(forbiddenState);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 终端控制
     *
     * @param terminalControlMsg
     * @return
     */
    public String terminalControl(TerminalControlMsg terminalControlMsg) {
        try {
            byte[] bs = this.encoder.encode4TerminalControlMsg(terminalControlMsg);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 位置信息查询 ok
     *
     * @return
     */
    public String queryLocation() {
        try {
            byte[] bs = this.encoder.encode4queryLocation();
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 立即拍照 ok
     * @return
     */
    public String immediatelyTakePictures(ImmediatelyTakePicturesMsg msg){
        try {
            byte[] bs = this.encoder.encode4ImmediatelyTakePictures(msg);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询照片
     * @param way 查询方式
     * @param startTime 查询开始时间
     * @param endTime 查询结束时间
     * @return
     */
    public String queryImage(int way,Date startTime, Date endTime ){
        try {
            byte[] bs = this.encoder.encode4QueryImage(way,startTime,endTime);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传指定照片消息编码
     * @param imgNum 照片编号
     * @return
     */
    public String upAppointImage(String imgNum){
        try {
            byte[] bs = this.encoder.encode4UpAppointImage(imgNum);
            return this.msgSend.send(bs, "000000000000123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
