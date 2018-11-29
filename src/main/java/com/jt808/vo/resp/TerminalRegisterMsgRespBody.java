package com.jt808.vo.resp;

//终端注册应答
public class TerminalRegisterMsgRespBody {

	public static final byte success = 0;
	public static final byte car_already_registered = 1;
	public static final byte car_not_found = 2;
	public static final byte terminal_already_registered = 3;
	public static final byte terminal_not_found = 4;
	// byte[0-1] 应答流水号(WORD) 对应的终端注册消息的流水号
	private int replyFlowId;
	/***
	 * byte[2] 结果(BYTE) <br>
	 * 0：成功<br>
	 * 1：车辆已被注册<br>
	 * 2：数据库中无该车辆<br>
	 * 3：终端已被注册<br>
	 * 4：数据库中无该终端<br>
	 **/
	private byte replyCode;
	// byte[3-x] 鉴权码(STRING) 只有在成功后才有该字段
	private String replyToken;
    //平台编号
	private String num;
	//培训机构编号
	private String inscode;
	//计时终端编号
	private String devnum;
	//证书口令
	private String pass;
	//终端证书
	private String  credential;
	public TerminalRegisterMsgRespBody() {
	}

	public int getReplyFlowId() {
		return replyFlowId;
	}

	public void setReplyFlowId(int flowId) {
		this.replyFlowId = flowId;
	}

	public byte getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(byte code) {
		this.replyCode = code;
	}

	public String getReplyToken() {
		return replyToken;
	}

	public void setReplyToken(String token) {
		this.replyToken = token;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getInscode() {
		return inscode;
	}

	public void setInscode(String inscode) {
		this.inscode = inscode;
	}

	public String getDevnum() {
		return devnum;
	}

	public void setDevnum(String devnum) {
		this.devnum = devnum;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Override
	public String toString() {
		return "TerminalRegisterMsgRespBody{" +
				"replyFlowId=" + replyFlowId +
				", replyCode=" + replyCode +
				", replyToken='" + replyToken + '\'' +
				", num='" + num + '\'' +
				", inscode='" + inscode + '\'' +
				", devnum='" + devnum + '\'' +
				", pass='" + pass + '\'' +
				", credential='" + credential + '\'' +
				'}';
	}
}
