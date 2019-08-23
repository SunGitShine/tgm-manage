package com.juma.tgm.manage.importantNotice.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.User;
import com.juma.auth.user.service.UserService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.importantNotice.domain.ImportantNotice;
import com.juma.tgm.importantNotice.service.ImportantNoticeService;
import com.juma.tgm.manage.web.controller.BaseController;

@Controller
@RequestMapping(value = "/importantNotice")
public class ImportantNoticeController extends BaseController {

    @Resource
    private ImportantNoticeService importantNoticeService;
    @Resource
    private UserService userService;

    /**
     * 分页查询
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ImportantNotice> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        return importantNoticeService.search(pageCondition, loginEmployee);
    }

    /**
     * 未读人员列表
     */
    @ResponseBody
    @RequestMapping(value = "not/read/user/list", method = RequestMethod.POST)
    public Page<User> hasNotReadUserList(PageCondition pageCondition, LoginEmployee loginEmployee) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null == filters || filters.isEmpty() || null == filters.get("noticeId")) {
            return new Page<User>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0, new ArrayList<User>());
        }

        int noticeId = BaseUtil.strToNum(filters.get("noticeId").toString());
        return importantNoticeService.unReadUserList(noticeId, pageCondition.getPageNo(), pageCondition.getPageSize());
    }

    /**
     * 新增
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody ImportantNotice importantNotice, LoginEmployee loginEmployee) {
        importantNotice.setTenantCode(loginEmployee.getTenantCode());
        importantNoticeService.insert(importantNotice, loginEmployee);
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "/{noticeId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer noticeId, LoginEmployee loginEmployee) {
        importantNoticeService.updateToEnable(noticeId, loginEmployee);
    }

    /**
     * 停用
     */
    @ResponseBody
    @RequestMapping(value = "/{noticeId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer noticeId, LoginEmployee loginEmployee) {
        importantNoticeService.updateToDisable(noticeId, loginEmployee);
    }
}
