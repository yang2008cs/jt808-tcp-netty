package com.jt808.vo.req;

import com.jt808.vo.PackageData;

import java.util.List;

public class UpQueryImageMsg extends ExtendedTimekeepingTrainingMsg {
    /**
     * 应答代码
     */
    private Integer replyCode;
    /**
     * 是否上报结束 byte
     */
    private Integer isUpEnd;
    /**
     * 符合条件的照片总数 byte
     */
    private Integer imgTotal;
    /**
     * 此次发送的照片数目 byte
     */
    private Integer sendImgNum;
    /**
     * 照片编号 byte[10]
     */
    private List<String> imgNum;

    public UpQueryImageMsg() {
    }

    public UpQueryImageMsg(PackageData packageData) {
        super(packageData);
    }

    public Integer getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(Integer replyCode) {
        this.replyCode = replyCode;
    }

    public Integer getIsUpEnd() {
        return isUpEnd;
    }

    public void setIsUpEnd(Integer isUpEnd) {
        this.isUpEnd = isUpEnd;
    }

    public Integer getImgTotal() {
        return imgTotal;
    }

    public void setImgTotal(Integer imgTotal) {
        this.imgTotal = imgTotal;
    }

    public Integer getSendImgNum() {
        return sendImgNum;
    }

    public void setSendImgNum(Integer sendImgNum) {
        this.sendImgNum = sendImgNum;
    }

    public List<String> getImgNum() {
        return imgNum;
    }

    public void setImgNum(List<String> imgNum) {
        this.imgNum = imgNum;
    }

    @Override
    public String toString() {
        return "UpQueryImageMsg{" +
                "replyCode=" + replyCode +
                ", isUpEnd=" + isUpEnd +
                ", imgTotal=" + imgTotal +
                ", sendImgNum=" + sendImgNum +
                ", imgNum=" + imgNum +
                '}';
    }
}
