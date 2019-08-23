package com.juma.oms.manage.openApp.vo;

import com.juma.auth.user.domain.OpenAPIUserBo;

import java.io.Serializable;

/**
 * @ClassName: OpenAPIUserVo
 * @Description:
 * @author: liang
 * @date: 2018-05-23 14:27
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class OpenAPIUserVo implements Serializable {

    private OpenAPIUserBo openAPIUserBo;


    public OpenAPIUserVo(OpenAPIUserBo openAPIUserBo) {
        this.openAPIUserBo = openAPIUserBo;
    }

    /**
     * 获取userKey
     *
     * @return
     */
    public String getUserKey() {
        if (this.openAPIUserBo == null) return null;

        return this.openAPIUserBo.getUserKey();
    }

    /**
     * 获取用户姓名
     *
     * @return
     */
    public String getUserName() {
        if (this.openAPIUserBo == null) return null;

        if (this.openAPIUserBo.getUser() == null) return null;

        return this.openAPIUserBo.getUser().getName();
    }

    /**
     * 用户id
     * @return
     */
    public Integer getUserId() {
        if (this.openAPIUserBo == null) return null;

        if (this.openAPIUserBo.getUser() == null) return null;

        return this.openAPIUserBo.getUser().getUserId();
    }

}
