package com.jt808.common;

/**
 * @author ：刘洋
 */
public enum TerminalArgIdEnums {
    /**
     * 客户端心跳发送间隔
     */
    D_0X0001(0x0001, "int", "客户端心跳发送间隔"),
    /**
     * TCP消息应答超时时间
     */
    D_0X0002(0x0002, "int", "TCP消息应答超时时间"),
    /**
     * TCP消息重传次数
     */
    D_0X0003(0x0003, "int", "TCP消息重传次数"),
    /**
     * 主服务器地址，ip或域名
     */
    D_0X0013(0x0013, "String", "主服务器地址，ip或域名"),
    /**
     * 服务器TCP端口
     */
    D_0X0018(0x0018, "int", "服务器TCP端口"),
    /**
     * 位置汇报策略：0：定时汇报，1：定距离汇报，2：定时和定距汇报
     */
    D_0X0020(0x0020, "int", "位置汇报策略：0：定时汇报，1：定距离汇报，2：定时和定距汇报"),
    /**
     * 位置汇报方案：0：根据ACC状态，1：根据登录状态和ACC状态
     */
    D_0X0021(0x0021, "int", "位置汇报方案：0：根据ACC状态，1：根据登录状态和ACC状态"),
    /**
     * 驾驶员未登录汇报时间间隔，单位（s）
     */
    D_0X0022(0x0022, "int", "驾驶员未登录汇报时间间隔，单位（s）"),
    /**
     * 紧急报警时汇报时间间隔，单位（s）
     */
    D_0X0028(0x0028, "int", "紧急报警时汇报时间间隔，单位（s）"),
    /**
     * 缺省时间汇报间隔，单位（s）
     */
    D_0X0029(0x0029, "int", "缺省时间汇报间隔，单位（s）"),
    /**
     * 缺省距离汇报间隔，单位（m）
     */
    D_0X002C(0x002C, "int", "缺省距离汇报间隔，单位（m）"),
    /**
     * 驾驶员未登录汇报距离间隔，单位（m）
     */
    D_0X002D(0x002D, "int", "驾驶员未登录汇报距离间隔，单位（m）"),
    /**
     * 休眠时汇报距离间隔，单位（m）
     */
    D_0X002E(0x002E, "int", "休眠时汇报距离间隔，单位（m）"),
    /**
     * 紧急报警时汇报距离间隔，单位（m）
     */
    D_0X002F(0x002F, "int", "紧急报警时汇报距离间隔，单位（m）"),
    /**
     * 最高时速，单位（km/h）
     */
    D_0X0055(0x0055, "int", "最高时速，单位（km/h）"),
    /**
     * 超速持续时间，单位（s）
     */
    D_0X0056(0x0056, "int", "超速持续时间，单位（s）"),
    ;
    private Integer id;
    private String type;
    private String msg;

    TerminalArgIdEnums(Integer id, String type, String msg) {
        this.id = id;
        this.type = type;
        this.msg = msg;
    }

    public static String msg(int id) {
        if (D_0X0001.getId() == id) {
            return D_0X0001.getMsg();
        } else if (D_0X0002.getId() == id) {
            return D_0X0002.getMsg();
        } else if (D_0X0003.getId() == id) {
            return D_0X0003.getMsg();
        } else if (D_0X0013.getId() == id) {
            return D_0X0013.getMsg();
        } else if (D_0X0018.getId() == id) {
            return D_0X0018.getMsg();
        } else if (D_0X0020.getId() == id) {
            return D_0X0020.getMsg();
        } else if (D_0X0021.getId() == id) {
            return D_0X0021.getMsg();
        } else if (D_0X0022.getId() == id) {
            return D_0X0022.getMsg();
        } else if (D_0X0028.getId() == id) {
            return D_0X0028.getMsg();
        } else if (D_0X0029.getId() == id) {
            return D_0X0029.getMsg();
        } else if (D_0X002C.getId() == id) {
            return D_0X002C.getMsg();
        } else if (D_0X002D.getId() == id) {
            return D_0X002D.getMsg();
        } else if (D_0X002E.getId() == id) {
            return D_0X002E.getMsg();
        } else if (D_0X002F.getId() == id) {
            return D_0X002F.getMsg();
        } else if (D_0X0055.getId() == id) {
            return D_0X0055.getMsg();
        } else if (D_0X0056.getId() == id) {
            return D_0X0056.getMsg();
        } else {
            return "未知的参数ID";
        }
    }

    public Integer getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public String getType() {
        return type;
    }
}
