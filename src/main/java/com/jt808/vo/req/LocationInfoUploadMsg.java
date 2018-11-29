package com.jt808.vo.req;


import com.jt808.vo.PackageData;

import java.util.Date;

/**
 * 位置信息汇报消息
 *
 * @author hylexus
 *
 */
public class LocationInfoUploadMsg extends PackageData {
	// 报警标识
	// byte [0-4] (DWORD(32))
	private int warningFlagField;
	// byte[4-8] 状态(DWORD(32))
	private int statusField;
	// byte[8-12] 纬度(DWORD(32))
	private float latitude;
	// byte[12-16] 经度(DWORD(32))
	private float longitude;
	// byte [16-18]行驶记录速度 (WORD)1/10km/h
	private int runSpeed;
	// byte[18-20] 卫星定位速度(WORD) 1/10km/h
	private int satelliteSpeed;
	// byte[20-22] 方向(WORD) 0-359，正北为 0，顺时针
	private int direction;
	// byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
	// GMT+8 时间，本标准中之后涉及的时间均采用此时区
	private Date time;

	public LocationInfoUploadMsg() {
	}

	public LocationInfoUploadMsg(PackageData packageData) {
		this();
		this.channel = packageData.getChannel();
		this.checkSum = packageData.getCheckSum();
		this.msgBodyBytes = packageData.getMsgBodyBytes();
		this.msgHeader = packageData.getMsgHeader();
	}





	public int getWarningFlagField() {
		return warningFlagField;
	}

	public void setWarningFlagField(int warningFlagField) {
		this.warningFlagField = warningFlagField;
	}

	public int getStatusField() {
		return statusField;
	}

	public void setStatusField(int statusField) {
		this.statusField = statusField;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public int getRunSpeed() {
		return runSpeed;
	}

	public void setRunSpeed(int runSpeed) {
		this.runSpeed = runSpeed;
	}

	public int getSatelliteSpeed() {
		return satelliteSpeed;
	}

	public void setSatelliteSpeed(int satelliteSpeed) {
		this.satelliteSpeed = satelliteSpeed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "LocationInfoUploadMsg{" +
				"warningFlagField=" + warningFlagField +
				", statusField=" + statusField +
				", latitude=" + latitude +
				", longitude=" + longitude +
				", runSpeed=" + runSpeed +
				", satelliteSpeed=" + satelliteSpeed +
				", direction=" + direction +
				", time=" + time +
				'}';
	}
}
