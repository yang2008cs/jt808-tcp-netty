package com.jt808.vo.req;

import com.jt808.common.TPMSConsts;
import com.jt808.vo.PackageData;

/**
 * 终端鉴权消息
 * 
 * @author hylexus
 *
 */
public class TerminalAuthenticationMsg extends PackageData {
	//时间戳
	private int timestamp;
	//鉴权密文
	private String authCode;

	public TerminalAuthenticationMsg() {
	}

	public TerminalAuthenticationMsg(PackageData packageData) {
		this();
		this.channel = packageData.getChannel();
		this.checkSum = packageData.getCheckSum();
		this.msgBodyBytes = packageData.getMsgBodyBytes();
		this.msgHeader = packageData.getMsgHeader();
		this.authCode = new String(packageData.getMsgBodyBytes(), TPMSConsts.STRING_CHARSET);
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthCode() {
		return authCode;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TerminalAuthenticationMsg{" +
				"timestamp=" + timestamp +
				", authCode='" + authCode + '\'' +
				'}';
	}
}
