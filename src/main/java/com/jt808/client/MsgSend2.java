package com.jt808.client;

import com.jt808.server.SessionManager;
import com.jt808.service.codec.Decoder;
import com.jt808.service.codec.Encoder;
import com.jt808.vo.req.PlatformLoginMsg;
import com.jt808.vo.req.PlatformLoginMsg.PlatformLoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MsgSend2 {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Decoder decoder;
    private Encoder encoder;
    private Socket socket;
    private DataOutputStream out;
    private InputStream is;
    private SessionManager sessionManager;


    public MsgSend2(int port, String host) throws IOException {
        this.decoder = new Decoder();
        this.encoder = new Encoder();
        this.socket = new Socket(host, port);
        this.out = new DataOutputStream(this.socket.getOutputStream());
        this.is = this.socket.getInputStream();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * 平台登录
     */
    public void login() {
        try {
            PlatformLoginInfo info = new PlatformLoginInfo();
            info.setNum("12345");
            info.setPass("12341234");
            info.setCode("123456");
            byte[] bs = this.encoder.encode4Resp(info);
            logger.info("发送的数据:{}", bs);
            this.out.write(bs);
            byte[] data = new byte[1024];
            this.is.read(data);
            logger.info("返回数据:{}", data);
            PlatformLoginMsg resp = this.decoder.toLoginMsg(data);
            int result = resp.getPlatformLoginInfo().getResult();
            switch (result) {
                case 0:
                    logger.info(">>>>>成功<<<<<");
                    break;
                case 1:
                    logger.info(">>>>>ip地址不正确<<<<<");
                    break;
                case 2:
                    logger.info(">>>>>接入码不正确<<<<<");
                    break;
                case 3:
                    logger.info(">>>>>改平台没有注册<<<<<");
                    break;
                case 4:
                    logger.info(">>>>>密码错误<<<<<");
                    break;
                case 5:
                    logger.info(">>>>>资源紧张，稍后再链接（已占用）<<<<<");
                    break;
                case 9:
                    logger.info(">>>>>其他<<<<<");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(this.socket, this.out, this.is);
        }
    }

    /**
     * 平台登出
     */
    public void loginOut() {
        try {
            PlatformLoginMsg.PlatformLoginInfo info = new PlatformLoginMsg.PlatformLoginInfo();
            info.setNum("12345");
            info.setPass("12341234");
            byte[] bs = this.encoder.encode4OutResp(info);
            logger.info("发送的数据:{}", bs);
            out.write(bs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(this.socket, this.out, this.is);
        }
    }




    public void close(Socket socket, DataOutputStream out, InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
