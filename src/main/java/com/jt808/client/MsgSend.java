package com.jt808.client;

import com.jt808.server.SessionManager;
import com.jt808.vo.Session;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：刘洋
 * 向终端发送数据，从SessionManager中获取指定终端通道
 */
public class MsgSend {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SessionManager sessionManager;

    public MsgSend() {
        this.sessionManager = SessionManager.getInstance();
    }

    public String send(byte[] arr, String phone) {
        try {
            if (phone.length() < 16) {
                //手机号码长度补，全统一长度16
                phone = String.format("%016d", Long.parseLong(phone));
            }
            logger.info("终端手机号：{}", phone);
            //跟就终端手机号获取对应的session
            Session session = this.sessionManager.findByTerminalPhone(phone);
            if(null!=session) {
                // Channel channel = ChannelMap.getChannelByName("000000000000123");
                //获取与终端的连接通道
                Channel channel = session.getChannel();
                //检查通道是否连通
                if (channel.isActive()) {
                    //发送数据
                    ChannelFuture future = channel.writeAndFlush(Unpooled.copiedBuffer(arr)).sync();
                    if (future.isSuccess()) {
                        logger.info("发送数据成功:{}", arr);
                        return "发送数据成功";
                    } else {
                        logger.error("发送数据出错:{}", future.cause());
                        return "发送数据出错";
                    }
                } else {
                    return "设备不在线";
                }
            }else {
                return "设备不在线";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "发送数据成功";
    }

}
