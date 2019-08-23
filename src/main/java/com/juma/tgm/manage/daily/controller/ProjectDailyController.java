package com.juma.tgm.manage.daily.controller;

import com.giants.common.tools.Page;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.daily.service.ProjectDailyService;
import com.juma.tgm.daily.vo.ProjectDailyFilter;
import com.juma.tgm.daily.vo.ProjectDailyVo;
import javax.annotation.Resource;

import com.juma.tgm.imageUploadManage.domain.UploadFile;
import com.juma.tgm.waybill.domain.WaybillVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @ClassName ProjectDailyController
 * @Description 项目日报
 * @Author weilibin
 * @Date 2019-07-18 21:39
 * @Version 1.0.0
 */

@Controller
@RequestMapping("projectDaily")
public class ProjectDailyController {

    @Resource
    private ProjectDailyService projectDailyService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ProjectDailyVo> search(@RequestBody QueryCond<ProjectDailyFilter> queryCond,
        LoginEmployee loginEmployee) {
        return projectDailyService.search(queryCond, loginEmployee);
    }

    /**
     * 显示日报详情
     *
     * @param dailyVo
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fill/daily/detail", method = RequestMethod.POST)
    public ProjectDailyVo searchProjectDailyById(@RequestBody ProjectDailyVo dailyVo) {
        return projectDailyService.searchProjectDaily(dailyVo);
    }

    /**
     * 显示运营台账文件
     *
     * @param projectDailyId
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fill/account/images/{projectDailyId}", method = RequestMethod.POST)
    public List searchStandingBook(@PathVariable Integer projectDailyId) {
        if (projectDailyId == null) {
            return new ArrayList();
        }
        return projectDailyService.searchStandingBook(projectDailyId);
    }

    /**
     * 上传运营台账文件
     *
     * @param uploadFiles
     * @param projectDailyId
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "/fill/account/images/upload/{projectDailyId}", method = RequestMethod.POST)
    public void uploadStandingBook(@RequestBody List<UploadFile> uploadFiles,
        @PathVariable Integer projectDailyId, LoginEmployee loginEmployee) {
        projectDailyService.uploadStandingBook(uploadFiles, projectDailyId, loginEmployee);
    }

    /**
     * 查询运单列表
     *
     * @param queryCond
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fill/daily/waybills", method = RequestMethod.POST)
    public List<WaybillVO> searchProjectDailyWaybills(
        @RequestBody QueryCond<ProjectDailyVo> queryCond) {
        return projectDailyService.searchProjectDailyWaybills(queryCond.getFilters());
    }

    /**
     * 修改运单信息
     *
     * @param waybillVO
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "/fill/daily/waybill/{projectDailyId}", method = RequestMethod.POST)
    public void updateProjectDailyWaybil(@RequestBody WaybillVO waybillVO,
        @PathVariable Integer projectDailyId, LoginEmployee loginEmployee) {
        projectDailyService.updateProjectDailyWaybil(waybillVO, projectDailyId, loginEmployee);
    }

}
