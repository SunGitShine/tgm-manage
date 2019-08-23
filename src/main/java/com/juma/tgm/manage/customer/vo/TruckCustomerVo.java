package com.juma.tgm.manage.customer.vo;

import java.io.Serializable;

/**
 * 
 * @Description: 用户VO
 * @author weilibin
 * @date 2016年5月20日 上午10:40:52
 * @version V1.0
 */

public class TruckCustomerVo implements Serializable {

    private static final long serialVersionUID = 7294721383753791364L;
    private Integer truckCustomerId;
    /** 等级 */
    private Integer classId;
    /** 用户 */
    private Integer userId;
    /** 地区编号 */
    private String regionCode;
    /** 地区ID */
    private Integer regionId;
    /** 状态 */
    private Byte status;
    /** 名字 */
    private String nickname;
    /** 联系方式 */
    private String contactPhone;
    /** 身份证号 */
    private String identityCardNo;
    /** 身份证照片 */
    private String identityCardPhotoUrl;
    /** 头像 */
    private String headPortrait;
    private String inviteCode;
    private Integer inviteUserId;
    private Integer departmentId;

    public Integer getTruckCustomerId() {
        return truckCustomerId;
    }

    public void setTruckCustomerId(Integer truckCustomerId) {
        this.truckCustomerId = truckCustomerId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getIdentityCardNo() {
        return identityCardNo;
    }

    public void setIdentityCardNo(String identityCardNo) {
        this.identityCardNo = identityCardNo;
    }

    public String getIdentityCardPhotoUrl() {
        return identityCardPhotoUrl;
    }

    public void setIdentityCardPhotoUrl(String identityCardPhotoUrl) {
        this.identityCardPhotoUrl = identityCardPhotoUrl;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Integer getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(Integer inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

}
