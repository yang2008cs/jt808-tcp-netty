package com.jt808.common;

import java.nio.charset.Charset;

/**
 * 标识位
 *
 * @author :liuyang
 */
public class TPMSConsts {

    public static final String STRING_ENCODING = "GBK";

    public static final Charset STRING_CHARSET = Charset.forName(STRING_ENCODING);
    /**
     * 平台登录
     */
    public static final int LOGIN = 0x01f0;
    /**
     * 平台登录应答
     */
    public static final int LOGIN_RESP = 0x81f0;

    /**
     * 平台登出
     */
    public static final int OUT = 0x01f1;
    //========================================================================
    /**
     * 标识位
     */
    public static final int PKG_DELIMITER = 0x7e;
    /**
     * 客户端发呆15分钟后,服务器主动断开连接
     */
    public static int TCP_CLIENT_IDLE_MINUTES = 30;
    /**
     * 终端通用应答
     */
    public static final int MSG_ID_TERMINAL_COMMON_RESP = 0x0001;
    /**
     * 终端心跳
     */
    public static final int MSG_ID_TERMINAL_HEART_BEAT = 0x0002;
    /**
     * 终端注册
     */
    public static final int MSG_ID_TERMINAL_REGISTER = 0x0100;
    /**
     * 终端注销
     */
    public static final int MSG_ID_TERMINAL_LOG_OUT = 0x0003;
    /**
     * 终端鉴权
     */
    public static final int MSG_ID_TERMINAL_AUTHENTICATION = 0x0102;
    /**
     * 位置信息汇报
     */
    public static final int MSG_ID_TERMINAL_LOCATION_INFO_UPLOAD = 0x0200;

    /**
     * 平台通用应答
     */
    public static final int CMD_COMMON_RESP = 0x8001;
    /**
     * 终端注册应答
     */
    public static final int CMD_TERMINAL_REGISTER_RESP = 0x8100;
    /**
     * 设置终端参数
     */
    public static final int CMD_TERMINAL_PARAM_SETTINGS = 0X8103;
    /**
     * 终端控制
     */
    public static final int CMD_TERMINAL_CONTROL = 0X8105;
    /**
     * 位置信息查询
     */
    public static final int QUERY_LOCATION_MSG = 0X8201;
    /**
     * 位置信息查询应答
     */
    public static final int QUERY_LOCATION_RESP = 0X0201;

    /**
     * 数据下行透传
     */
    public static final int DATA_DOWN = 0x8900;
    /**
     * 数据上行透传
     */
    public static final int DATA_UP = 0x0900;
    //========================================================================
    //透传消息
    //========================================================================
    /**
     * 教练员登录
     */
    public static final int COACH_LOGIN = 0x0101;
    /**
     * 教练员登录应答
     */
    public static final int COACH_LOGIN_RESP = 0x8101;
    /**
     * 教练员登出
     */
    public static final int COACH_LOGIN_OUT = 0x0102;
    /**
     * 教练员登出应答
     */
    public static final int COACH_LOGIN_OUT_RESP = 0x8102;
    /**
     * 学员登录
     */
    public static final int STU_LOGIN = 0x0201;
    /**
     * 学员登录应答
     */
    public static final int STU_LOGIN_RESP = 0x8201;
    /**
     * 学员登出
     */
    public static final int STU_LOGIN_OUT = 0x0202;
    /**
     * 学员登出应答
     */
    public static final int STU_LOGIN_OUT_RESP = 0x8202;
    /**
     * 上传学时记录
     */
    public static final int UP_PERIOD_RECORD = 0x0203;
    /**
     * 照片上传初始化
     */
    public static final int PHOTO_UPLOAD_INITIALIZES = 0x0305;
    /**
     * 上传照片数据包
     */
    public static final int PHOTO_UPLOAD_DATA = 0x0306;
    /**
     * 获取教练或学员照片
     */
    public static final int COACH_OR_STU_PHOTO = 0x8207;
    /**
     * 下发教练或学员照片
     */
    public static final int COACH_OR_STU_PHOTO_RESP = 0x0207;
    /**
     * 查询计时终端应用参数应答
     */
    public static final int QUERY_TERMINAL_APPLY_ARG = 0x0503;
    /**
     * 设置计时终端应用参数应答
     */
    public static final int SET_TRAINING_APPLY_ARG_RESP = 0x0501;
    /**
     * 查询终端参数应答
     */
    public static final int MSG_ID_TERMINAL_PARAM_QUERY_RESP = 0x0104;
    /**
     * 设置禁训状态消息应答
     */
    public static final int FORBIDDEN_STATE_RESP = 0x0502;
//========================================================================
    /**
     * 设置终端参数
     */
    public static final int SETTING_TERMINAL_ARG = 0x8103;
    /**
     * 查询终端参数
     */
    public static final int QUERY_TERMINAL_ARG = 0x8104;
    /**
     * 查询指定终端参数
     */
    public static final int QUERY_APPOINT_TERMINAL_ARG = 0x8106;
    /**
     * 临时位置跟踪控制
     */
    public static final int TEMPORARY_POSITION_TRACKING_RESP = 0x8202;
    /**
     * 命令上报学时记录
     */
    public static final int UP_PERIOD_RECORD_RESP = 0x8205;
    /**
     * 命令上报学时记录应答
     */
    public static final int UP_PERIOD_RECORD_ORDER_RESP = 0x0205;
    /**
     * 立即拍照
     */
    public static final int IMMEDIATELY_TAKE_PICTURES = 0x8301;
    /**
     * 立即拍照应答
     */
    public static final int IMMEDIATELY_TAKE_PICTURES_RESP = 0x0301;
    /**
     * 查询计时终端应用参数
     */
    public static final int QUERY_TERMINAL_APPLY_ARG_MSG = 0x8503;
    /**
     * 设置禁训状态消息
     */
    public static final int FORBIDDEN_STATE_MSG = 0x8502;
    /**
     * 设置计时终端应用参数
     */
    public static final int SET_TRAINING_APPLY_ARG_MSG = 0x8501;
    /**
     * 查询照片
     */
    public static final int QUERY_IMAGE = 0x8302;
    /**
     * 查询照片应答
     */
    public static final int QUERY_IMAGE_RESP = 0x0302;
    /**
     * 上报照片查询结果
     */
    public static final int UP_QUERY_IMAGE = 0x0303;
    /**
     * 上报照片查询结果应答
     */
    public static final int UP_QUERY_IMAGE_RESP = 0x8303;
    /**
     * 上传指定照片
     */
    public static final int UP_APPOINT_IMAGE = 0x8304;
    /**
     * 上传指定照片应答
     */
    public static final int UP_APPOINT_IMAGE_RESP = 0x0304;


}
