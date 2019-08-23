package com.juma.tgm.manage.authority.vo;

import com.juma.auth.conf.domain.BusinessArea;

/**
 * @ClassName: BusinessAreaVo
 * @Description:
 * @author: liang
 * @date: 2018-01-16 15:55
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class BusinessAreaVo extends BusinessArea {

    public Boolean getDisable() {
        if(!this.isLogic() && this.isLeaf()) return true;

        return null;
    }

}
