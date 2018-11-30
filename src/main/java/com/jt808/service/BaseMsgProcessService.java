package com.jt808.service;

import com.jt808.common.TPMSConsts;
import com.jt808.server.SessionManager;
import com.jt808.util.BitOperator;
import com.jt808.util.DigitalUtils;
import com.jt808.vo.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author ：liuyang
 */
public class BaseMsgProcessService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected SessionManager sessionManager;
	private BitOperator bitOperator;

	public BaseMsgProcessService() {
		this.sessionManager = SessionManager.getInstance();
		this.bitOperator = new BitOperator();
	}

	protected ByteBuf getByteBuf(byte[] arr) {
		ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(arr.length);
		byteBuf.writeBytes(arr);
		return byteBuf;
	}
	public void send2Client(Channel channel, byte[] arr) throws InterruptedException {
		byte[] bs = new byte[arr.length-2];
		System.arraycopy(arr,1,bs,0,bs.length);
		byte[] tem = this.bitOperator.concatAll(Arrays.asList(
				// 0x7e
				new byte[]{TPMSConsts.PKG_DELIMITER},
				// 消息头+ 消息体
				DigitalUtils.transferMean(bs),
				// 0x7e
				new byte[]{TPMSConsts.PKG_DELIMITER}
		));
		ChannelFuture future = channel.writeAndFlush(Unpooled.copiedBuffer(tem)).sync();
		if (!future.isSuccess()) {
			log.error("发送数据出错:{}", future.cause());
		}else {
			log.info("发送数据成功:{}",arr);
		}
	}

	protected int getFlowId(Channel channel, int defaultValue) {
		Session session = this.sessionManager.findBySessionId(Session.buildId(channel));
		if (session == null) {
			return defaultValue;
		}

		return session.currentFlowId();
	}

	protected int getFlowId(Channel channel) {
		return this.getFlowId(channel, 0);
	}

}
