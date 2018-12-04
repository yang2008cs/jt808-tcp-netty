package com.jt808.service.codec;

import com.jt808.common.TPMSConsts;
import com.jt808.util.BCD8421Operater;
import com.jt808.util.BitOperator;
import com.jt808.util.TerminalArgUtils;
import com.jt808.vo.PackageData;
import com.jt808.vo.PackageData.MsgHeader;
import com.jt808.vo.req.*;
import com.jt808.vo.req.ExtendedTimekeepingTrainingMsg.ExtendedTimekeepingTrainingInfo;
import com.jt808.vo.req.TerminalArgMsg.TerminalArg.TerminalArgList;
import com.jt808.vo.req.TerminalRegisterMsg.TerminalRegInfo;
import com.jt808.vo.resp.QueryTerminalArgRespMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息解码器
 *
 * @author ：liuyang
 */
public class MsgDecoder {

    private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);

    private BitOperator bitOperator;
    private BCD8421Operater bcd8421Operater;
    private TerminalArgUtils terminalArgUtils;

    public MsgDecoder() {
        this.bitOperator = new BitOperator();
        this.bcd8421Operater = new BCD8421Operater();
        this.terminalArgUtils = new TerminalArgUtils();
    }

    /**
     * 数据包
     *
     * @param data
     * @return PackageData
     */
    public PackageData bytes2PackageData(byte[] data) {
        PackageData ret = new PackageData();
        // 1. 16byte 或 12byte 消息头
        MsgHeader msgHeader = this.parseMsgHeaderFromBytes(data);
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
    private MsgHeader parseMsgHeaderFromBytes(byte[] data) {
        MsgHeader msgHeader = new MsgHeader();

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

    /**
     * byte[] 转BCD String
     *
     * @param data
     * @param startIndex
     * @param lenth
     * @return
     */
    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseBcdStringFromBytes(data, startIndex, lenth, null);
    }

    /**
     * byte[] 转BCD String
     *
     * @param data
     * @param startIndex
     * @param lenth
     * @param defaultVal
     * @return
     */
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
     * 终端注册消息
     *
     * @param packageData
     * @return
     */
    public TerminalRegisterMsg toTerminalRegisterMsg(PackageData packageData) {
        TerminalRegisterMsg ret = new TerminalRegisterMsg(packageData);
        byte[] data = ret.getMsgBodyBytes();

        TerminalRegInfo body = new TerminalRegInfo();

        // 1. byte[0-2] 省域ID(WORD)
        // 设备安装车辆所在的省域，省域ID采用GB/T2260中规定的行政区划代码6位中前两位
        // 0保留，由平台取默认值
        body.setProvinceId(this.parseIntFromBytes(data, 0, 2));

        // 2. byte[2-4] 设备安装车辆所在的市域或县域,市县域ID采用GB/T2260中规定的行 政区划代码6位中后四位
        // 0保留，由平台取默认值
        body.setCityId(this.parseIntFromBytes(data, 2, 2));

        // 3. byte[4-9] 制造商ID(BYTE[5]) 5 个字节，终端制造商编码
        // byte[] tmp = new byte[5];
        body.setManufacturerId(this.parseStringFromBytes(data, 4, 5));

        // 4. byte[9-29] 终端型号(BYTE[8]) 八个字节， 此终端型号 由制造商自行定义 位数不足八位的，补空格。
        body.setTerminalType(this.parseStringFromBytes(data, 9, 20).trim());

        // 5. byte[29-36] 计时终端出厂序列号(BYTE[7]) 七个字节， 由大写字母 和数字组成， 此终端 ID由制造 商自行定义
        body.setTerminalId(this.parseStringFromBytes(data, 29, 7));

        // 6. byte[36-51] IMEI(BYTE[15]) 七个字节
        body.setImei(this.parseStringFromBytes(data, 36, 15));

        // 7. byte[51] 车牌颜色(BYTE) 车牌颜 色按照JT/T415-2006 中5.4.12 的规定
        body.setLicensePlateColor(this.parseIntFromBytes(data, 51, 1));

        // 8. byte[52-x] 车牌(STRING) 公安交 通管理部门颁 发的机动车号牌
        body.setLicensePlate(this.parseStringFromBytes(data, 52, data.length - 52));
        ret.setTerminalRegInfo(body);
        return ret;
    }

    public LocationInfoUploadMsg toLocationInfoUploadMsg(PackageData packageData) throws Exception {
        LocationInfoUploadMsg ret = new LocationInfoUploadMsg(packageData);
        final byte[] data = ret.getMsgBodyBytes();
        // 1. byte[0-4] 报警标志(DWORD(32))
        ret.setWarningFlagField(this.parseIntFromBytes(data, 0, 4));
        // 2. byte[4-8] 状态(DWORD(32))
        ret.setStatusField(this.parseIntFromBytes(data, 4, 4));
        // 3. byte[8-12] 纬度(DWORD(32)) 以度为单位的纬度值乘以10^6，精确到百万分之一度
        ret.setLatitude(this.parseIntFromBytes(data, 8, 4) / 100000f);
        // 4. byte[12-16] 经度(DWORD(32)) 以度为单位的经度值乘以10^6，精确到百万分之一度
        ret.setLongitude(this.parseIntFromBytes(data, 12, 4) / 100000f);
        // 5.行驶记录速度
        ret.setRunSpeed(this.parseIntFromBytes(data, 16, 2));
        //6.卫星定位速度
        ret.setSatelliteSpeed(this.parseIntFromBytes(data, 18, 2));
        // byte[20-22] 方向(WORD) 0-359，正北为 0，顺时针
        ret.setDirection(this.parseIntFromBytes(data, 20, 2));
        // byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
        // GMT+8 时间，本标准中之后涉及的时间均采用此时区
        byte[] tmp = new byte[6];
        System.arraycopy(data, 22, tmp, 0, 6);
        String time = this.parseBcdStringFromBytes(data, 22, 6);
        DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        ret.setTime(format.parse(time));
        return ret;
    }

    private float parseFloatFromBytes(byte[] data, int startIndex, int length) {
        return this.parseFloatFromBytes(data, startIndex, length, 0f);
    }

    private float parseFloatFromBytes(byte[] data, int startIndex, int length, float defaultVal) {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            final int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[len];
            System.arraycopy(data, startIndex, tmp, 0, len);
            return bitOperator.byte2Float(tmp);
        } catch (Exception e) {
            log.error("解析浮点数出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }


    /**
     * 扩展计时培训消息
     *
     * @param packageData
     * @return
     */
    public ExtendedTimekeepingTrainingMsg toExtendedTimekeepingTrainingMsg(PackageData packageData) {
        ExtendedTimekeepingTrainingMsg ret = new ExtendedTimekeepingTrainingMsg(packageData);
        byte[] data = ret.getMsgBodyBytes();
        log.info("data: {}", data);
        ExtendedTimekeepingTrainingInfo body = new ExtendedTimekeepingTrainingInfo();
        //1 byte[0-2] 透传消息id
        body.setOspfId(this.parseIntFromBytes(data, 1, 2));
        //2 byte[2-4] 扩展消息属性
        body.setBodyPropsField(this.parseIntFromBytes(data, 3, 2));
        //3 byte[4-6] 驾培包序号
        body.setNumOrder(this.parseIntFromBytes(data, 5, 2));
        //4 byte[6-22] 计时终端统一编号
        body.setTerminalId(this.parseStringFromBytes(data, 7, 16).trim());
        //5 byte[22-24] 数据长度
        body.setDataLen(this.parseIntFromBytes(data, 23, 2));
        if (body.getDataLen() > 0) {
            byte[] tmp = new byte[body.getDataLen()];
            System.arraycopy(data, 25, tmp, 0, tmp.length);
            body.setDataContent(tmp);
            body.setCheckSum(this.parseStringFromBytes(data, 25 + body.getDataLen(), data.length - 25 - body.getDataLen()).trim());
        } else {
            body.setCheckSum(this.parseStringFromBytes(data, 25, data.length - 25).trim());
        }
        ret.setExtendedTimekeepingTrainingInFo(body);
        return ret;
    }

    /**
     * 处理教练登录消息
     *
     * @param data
     * @return
     */
    public CoachLoginMsg toCoachLoginMsg(byte[] data) {
        CoachLoginMsg body = new CoachLoginMsg();
        //教练员编号
        body.setCoachnum(this.parseStringFromBytes(data, 0, 16).trim());
        //教练员身份证号
        body.setIdcard(this.parseStringFromBytes(data, 16, 18).trim());
        //准教车型
        body.setTeachpermitted(this.parseStringFromBytes(data, 34, 2).trim());
        //GNSS数据包
        byte[] tmp = new byte[28];
        System.arraycopy(data, 36, tmp, 0, tmp.length);
        body.setGnss(gnssMsg(tmp));
        return body;
    }

    /**
     * 处理教练登出消息
     *
     * @param data
     * @return
     */
    public CoachLoginMsg toCoachLoginOutMsg(byte[] data) {
        CoachLoginMsg body = new CoachLoginMsg();
        //教练员编号
        body.setCoachnum(this.parseStringFromBytes(data, 0, 16).trim());
        //GNSS数据包
        byte[] tmp = new byte[28];
        System.arraycopy(data, 16, tmp, 0, tmp.length);
        body.setGnss(gnssMsg(tmp));
        return body;
    }

    /**
     * 学员登录消息
     *
     * @param data
     * @return
     */
    public StudentLoginMsg toStudentLoginMsg(byte[] data) {
        StudentLoginMsg body = new StudentLoginMsg();
        //学员编号
        body.setStunum(this.parseStringFromBytes(data, 0, 16).trim());
        //教练员编号
        body.setCoachnum(this.parseStringFromBytes(data, 16, 16).trim());
        //培训课程
        body.setCurriculum(this.parseBcdStringFromBytes(data, 32, 5));
        //课堂id
        body.setClassRoomId(this.parseIntFromBytes(data, 37, 4));
        //GNSS数据包
        byte[] tmp = new byte[28];
        System.arraycopy(data, 41, tmp, 0, tmp.length);
        body.setGnss(gnssMsg(tmp));
        return body;
    }

    /**
     * 学员登出消息
     *
     * @param data
     * @return
     */
    public StudentLoginOutMsg studentLoginOutMsg(byte[] data) {
        StudentLoginOutMsg body = new StudentLoginOutMsg();
        //学员编号
        body.setStunum(this.parseStringFromBytes(data, 0, 16).trim());
        //登出时间
        body.setOutTime(this.parseBcdStringFromBytes(data, 16, 6));
        //学员该次登录总时间
        body.setLoginTotalTime(this.parseIntFromBytes(data, 22, 2));
        //学员该次登录总里程
        body.setLoginTotalMileage(this.parseIntFromBytes(data, 24, 2));
        //课堂id
        body.setClassRoomId(this.parseIntFromBytes(data, 26, 4));
        byte[] tmp = new byte[28];
        System.arraycopy(data, 30, tmp, 0, tmp.length);
        body.setGnss(gnssMsg(tmp));
        return body;
    }

    /**
     * 上报学时记录
     *
     * @param data
     * @return
     */
    public UpPeriodRecordMsg upPeriodRecordMsg(byte[] data) {
        UpPeriodRecordMsg body = new UpPeriodRecordMsg();
        //学时记录编号 byte[26]
        body.setRecordNum(this.parseStringFromBytes(data, 0, 26).trim());
        //上报类型 byte
        body.setUpType(this.parseIntFromBytes(data, 26, 1));
        //学员编号
        body.setStunum(this.parseStringFromBytes(data, 27, 16).trim());
        //教练员编号
        body.setCoachnum(this.parseStringFromBytes(data, 43, 16).trim());
        //课堂id
        body.setClassRoomId(this.parseIntFromBytes(data, 59, 4));
        //记录产生时间 BCD[3]
        body.setRecordNum(this.parseBcdStringFromBytes(data, 63, 3));
        //培训课程
        body.setCurriculum(this.parseBcdStringFromBytes(data, 66, 5));
        //记录状态 BYTE
        body.setState(this.parseIntFromBytes(data, 71, 1));
        //最大速度 WORD
        body.setSpeed(this.parseIntFromBytes(data, 72, 2));
        //里程 WORD
        body.setMileage(this.parseIntFromBytes(data, 74, 2));
        byte[] tmp = new byte[38];
        System.arraycopy(data, 76, tmp, 0, tmp.length);
        body.setGnss(gnssMsg(tmp));
        return body;
    }

    /**
     * 上传图片初始化
     *
     * @param data
     * @return
     */
    public PhotoUploadInitializesMsg photoUploadInitializesMsg(byte[] data) {
        PhotoUploadInitializesMsg body = new PhotoUploadInitializesMsg();
        //照片编号
        body.setPhotoNum(this.parseStringFromBytes(data, 0, 10).trim());
        //学员编号
        body.setStunum(this.parseStringFromBytes(data, 10, 16).trim());
        //上传模式
        body.setUpMode(this.parseIntFromBytes(data, 26, 1));
        //摄像头通道号
        body.setCameraChannelNumber(this.parseIntFromBytes(data, 27, 1));
        //图片尺寸
        body.setPhotoSize(this.parseIntFromBytes(data, 28, 1));
        //发起图片的时间类型
        body.setPhotoType(this.parseIntFromBytes(data, 29, 1));
        //总包数
        body.setTotalPackage(this.parseIntFromBytes(data, 30, 2));
        //照片数据大小
        body.setPhotoDataSize(this.parseIntFromBytes(data, 32, 4));
        //课堂id
        body.setClassRoomId(this.parseIntFromBytes(data, 36, 4));
        //卫星定位数据包
        byte[] tmp = new byte[28];
        System.arraycopy(data, 40, tmp, 0, tmp.length);
        body.setGnss(gnssMsg(tmp));
        //人脸识别置信度
        body.setConfidenceCoefficient(this.parseIntFromBytes(data, 68, 1));
        return body;
    }

    /**
     * 上传图片数据
     *
     * @param data
     * @return
     */
    public PhotoUploadDataMsg photoUploadDataMsg(byte[] data) {
        PhotoUploadDataMsg body = new PhotoUploadDataMsg();
        //照片编号
        body.setPhotoNum(this.parseStringFromBytes(data, 0, 10).trim());
        //照片数据
        body.setPhotoData(this.parseStringFromBytes(data, 10, data.length - 10).trim());
        return body;
    }

    /**
     * 上传图片数据
     *
     * @param data
     * @return
     */
    public PhotoUploadDataMsg photoUploadDataPckMsg(byte[] tmp,byte[] data) {
        PhotoUploadDataMsg body = new PhotoUploadDataMsg();
        //照片编号
        body.setPhotoNum(this.parseStringFromBytes(tmp, 0, 10).trim());
        //照片数据
        body.setPhotoData(this.parseStringFromBytes(data, 0, data.length-1));

        return body;
    }

    /**
     * 查询计时终端应用参数
     *
     * @param data
     * @return
     */
    public TerminalApplyArgMsg queryTerminalApplyArgRespBody(byte[] data) {
        TerminalApplyArgMsg body = new TerminalApplyArgMsg();
        //参数编号
        body.setArgNum(this.parseIntFromBytes(data, 0, 1));
        //定时拍照时间间隔
        body.setTimedPhotoIntervals(this.parseIntFromBytes(data, 1, 1));
        //照片上传设置
        body.setPhotoUploadSettings(this.parseIntFromBytes(data, 2, 1));
        //是否报读附加消息
        body.setIsRead(this.parseIntFromBytes(data, 3, 1));
        //熄火后停止学时计时的延时时间
        body.setDelayedTime(this.parseIntFromBytes(data, 4, 1));
        //熄火后Gnss数据包上传间隔
        body.setGnssUpInterval(this.parseIntFromBytes(data, 5, 2));
        //熄火后教练自动登出的延时时间
        body.setCoachLoginDelayedTime(this.parseIntFromBytes(data, 7, 2));
        //重新验证身份时间
        body.setRevalidate(this.parseIntFromBytes(data, 9, 2));
        //教练跨校教学
        body.setAcrossSchoolTeaching(this.parseIntFromBytes(data, 11, 1));
        //学员跨校学习
        body.setAcrossSchoolLearning(this.parseIntFromBytes(data, 12, 1));

        return body;
    }

    public QueryTerminalArgRespMsgBody toQueryTerminalArgRespMsg(byte[] bs, byte[] data) {
        QueryTerminalArgRespMsgBody body = new QueryTerminalArgRespMsgBody();
        //应答流水号
        body.setReplyFlowId(this.parseIntFromBytes(bs, 0, 2));
        //应答参数个数
        body.setArgTotal(this.parseIntFromBytes(bs, 2, 1));
        //包参数个数
        body.setArgNum(this.parseIntFromBytes(bs, 3, 1));
        //参数项列表
        List<TerminalArgList> terminalArgList = new ArrayList<TerminalArgList>();
        int x = 0;
        for (int i = 0; i < body.getArgTotal(); i++) {
            int argLen = this.parseIntFromBytes(data, 4 + x, 1) + 5;
            byte[] tmp = new byte[argLen];
            System.arraycopy(data, x, tmp, 0, tmp.length);
            terminalArgList.add(terminalArgUtils.byteToTerminalArg(tmp));
            x += argLen;
        }
        body.setTerminalArgList(terminalArgList);
        return body;
    }

    /**
     * 设置计时终端应用参数应答消息解码
     *
     * @param data
     * @return
     */
    public int setTerminalApplyArgRespBody(byte[] data) {
        return this.parseIntFromBytes(data, 0, 1);
    }

    /**
     * 设置禁训状态消息应答消息解码
     *
     * @param data
     * @return
     */
    public ForbiddenState setForbiddenStateRespBody(byte[] data) {
        ForbiddenState body = new ForbiddenState();
        //执行结果
        body.setReplyCode(this.parseIntFromBytes(data, 0, 1));
        //禁训状态
        body.setState(this.parseIntFromBytes(data, 1, 1));
        //提示消息长度
        body.setMsgLen(this.parseIntFromBytes(data, 2, 1));
        //提示消息内容
        if (body.getMsgLen() > 0) {
            body.setMsg(this.parseStringFromBytes(data, 3, body.getMsgLen()));
        }
        return body;
    }

    /**
     * 终端鉴权
     *
     * @param packageData
     * @return
     */
    public TerminalAuthenticationMsg toTerminalAuthenticationMsg(PackageData packageData) {
        TerminalAuthenticationMsg ret = new TerminalAuthenticationMsg(packageData);
        final byte[] data = ret.getMsgBodyBytes();
        //时间戳
        ret.setTimestamp(this.parseIntFromBytes(data, 0, 4));
        //鉴权密文
        ret.setAuthCode(this.parseStringFromBytes(data, 4, data.length - 4));
        return ret;
    }

    /**
     * 命令上传学时记录应答
     *
     * @param data
     * @return
     */
    public int setUpPeriodRecordOrder(byte[] data) {
        return this.parseIntFromBytes(data, 0, 1);
    }

    /**
     * 立即拍照应答
     *
     * @param data
     * @return
     */
    public ImmediatelyTakePicturesMsg toImmediatelyTakePictures(byte[] data) {
        ImmediatelyTakePicturesMsg resp = new ImmediatelyTakePicturesMsg();
        //执行结果
        resp.setReplyCode(this.parseIntFromBytes(data, 0, 1));
        //上传模式
        resp.setModel(this.parseIntFromBytes(data, 1, 1));
        //摄像头通道号
        resp.setAisle(this.parseIntFromBytes(data, 2, 1));
        //图片尺寸
        resp.setSize(this.parseIntFromBytes(data, 3, 1));
        return resp;
    }

    /**
     * 查询照片应答
     *
     * @param data
     * @return
     */
    public int toQueryImage(byte[] data) {
        return this.parseIntFromBytes(data, 0, 1);
    }

    /**
     * 上报照片查询结果消息解码
     *
     * @param data
     * @return
     */
    public UpQueryImageMsg toUpQueryImage(byte[] data) {
        UpQueryImageMsg imageMsg = new UpQueryImageMsg();
        List<String> imgNum = new ArrayList<String>();
        //是否上报结束 为0时表示数据没有发完，没有后续字段
        imageMsg.setIsUpEnd(this.parseIntFromBytes(data, 0, 1));
        if (imageMsg.getIsUpEnd() == 1) {
            //符合条件的照片总数
            imageMsg.setImgTotal(this.parseIntFromBytes(data, 1, 1));
            //此次发送的照片数
            imageMsg.setSendImgNum(this.parseIntFromBytes(data, 2, 1));
            //循环取出照片编号
            byte[] bs = new byte[data.length - 3];
            System.arraycopy(data, 3, bs, 0, bs.length);
            for (int i = 0; i < bs.length; i += 10) {
                imgNum.add(this.parseStringFromBytes(data, i, i + 10));
            }
            //照片编号集合
            imageMsg.setImgNum(imgNum);
        }
        return imageMsg;
    }

    /**
     * 上传指定照片应答消息解码
     *
     * @param data
     * @return
     */
    public int toUpAppointImageResp(byte[] data) {
        return this.parseIntFromBytes(data, 0, 1);
    }

    /**
     * 解析GNSS数据包
     *
     * @param bs
     * @return
     * @throws Exception
     */
    public GnssMsg gnssMsg(byte[] bs) {
        GnssMsg gnssMsg = new GnssMsg();
        try {
            // 1. byte[0-4] 报警标志(DWORD(32))
            gnssMsg.setWarningFlagField(this.parseIntFromBytes(bs, 0, 4));
            // 2. byte[4-8] 状态(DWORD(32))
            gnssMsg.setStatusField(this.parseIntFromBytes(bs, 4, 4));
            // 3. byte[8-12] 纬度(DWORD(32)) 以度为单位的纬度值乘以10^6，精确到百万分之一度
            gnssMsg.setLatitude(this.parseIntFromBytes(bs, 8, 4) / 100000f);
            // 4. byte[12-16] 经度(DWORD(32)) 以度为单位的经度值乘以10^6，精确到百万分之一度
            gnssMsg.setLongitude(this.parseIntFromBytes(bs, 12, 4) / 100000f);
            // 5.行驶记录速度
            gnssMsg.setRunSpeed(this.parseIntFromBytes(bs, 16, 2));
            //6.卫星定位速度
            gnssMsg.setSatelliteSpeed(this.parseIntFromBytes(bs, 18, 2));
            // byte[20-22] 方向(WORD) 0-359，正北为 0，顺时针
            gnssMsg.setDirection(this.parseIntFromBytes(bs, 20, 2));
            // byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
            // GMT+8 时间，本标准中之后涉及的时间均采用此时区
            byte[] tmp = new byte[6];
            System.arraycopy(bs, 22, tmp, 0, 6);
            String time = this.parseBcdStringFromBytes(bs, 22, 6);
            DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            gnssMsg.setTime(format.parse(time));
            if (bs.length > 28) {
                List<GnssMsg.AttachLocation> locations = new ArrayList<GnssMsg.AttachLocation>();
                byte[] data = new byte[bs.length - 28];
                System.arraycopy(bs, 28, data, 0, data.length);
                int len = 0;
                for (int i = 0; i < data.length; i++) {
                    GnssMsg.AttachLocation attachLocation = new GnssMsg.AttachLocation();
                    attachLocation.setMsgId(this.parseIntFromBytes(data, 0 + len, 1));
                    attachLocation.setMsgLen(this.parseIntFromBytes(data, 1 + len, 1));
                    attachLocation.setMsg(this.parseIntFromBytes(data, 2 + len, attachLocation.getMsgLen()));
                    locations.add(attachLocation);
                    len += 2 + attachLocation.getMsgLen();
                    if (data.length-len <=0) {
                        break;
                    }
                }
                gnssMsg.setAttachLocation(locations);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gnssMsg;
    }
    /**
     * 获取教练或学员照片
     * @param data
     * @return
     */
    public CoachOrStuPhotoMsg toCoachOrStuPhotoMsg(byte[] data) {
        CoachOrStuPhotoMsg coachOrStuPhotoMsg = new CoachOrStuPhotoMsg();
        //类型 byte
        coachOrStuPhotoMsg.setType(this.parseIntFromBytes(data,0,1));
        //教练或学员编号 byte[16]
        coachOrStuPhotoMsg.setNum(this.parseStringFromBytes(data,1,16));
        return coachOrStuPhotoMsg;
    }
}
