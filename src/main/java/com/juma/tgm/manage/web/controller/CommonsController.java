package com.juma.tgm.manage.web.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.juma.auth.employee.domain.PermissionEmployeeFilter;
import com.juma.auth.support.service.EmployeeSupportService;
import com.juma.tgm.tools.service.AuthCommonService;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.cache.redis.RedisClient;
import com.giants.common.Assert;
import com.giants.common.collections.CollectionUtils;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageQueryCondition;
import com.giants.web.utils.WebUtils;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.EmployeeFilter;
import com.juma.auth.employee.domain.EmployeeInfo;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.auth.user.domain.User;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.domain.Region;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.common.Base62;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.gaode.domain.IpAddressComponent;
import com.juma.tgm.manage.web.vo.CommonsVo;
import com.juma.tgm.manage.web.vo.DesignDateVo;
import com.juma.tgm.region.service.RegionTgmService;
import com.juma.tgm.tools.service.BusinessAreaCommonService;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.service.GaoDeMapService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName CommonController.java
 * @Description 公共controller
 * @Date 2017年5月22日 下午6:31:26
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("commons")
@PropertySource("classpath:tgm.properties")
public class CommonsController {

    private static final Logger log = LoggerFactory.getLogger(CommonsController.class);
    @Resource
    private EmployeeService employeeService;
    @Resource
    private GaoDeMapService gaoDeMapService;
    @Resource
    private RegionTgmService regionTgmService;
    @Resource
    private RedisClient redisClient;

    @Autowired
    private Environment environment;

    @Resource
    private BusinessAreaCommonService businessAreaCommonService;

    @Resource
    private ConfParamService confParamService;
    @Resource
    private EmployeeSupportService employeeSupportService;

    /**
     * 所有客户经理，作为条件等使用,单个业务区域
     */
    @ResponseBody
    @RequestMapping(value = "customer/manager/search", method = RequestMethod.GET)
    public List<EmployeeInfo> customerManagerSearch(String areaCode, LoginEmployee loginEmployee) {
        List<EmployeeInfo> result = new ArrayList<EmployeeInfo>();
        List<Integer> cheakList = new ArrayList<Integer>();
        try {
            List<EmployeeInfo> employeeInfos = employeeService.findEmployeeInfos(areaCode, Constants.AUTH_KEY_TGM_MANAGE,
                    Constants.CUSTOMER_MANAGER_PERMISSION_KEY, loginEmployee);
            for (EmployeeInfo employeeInfo : employeeInfos) {
                if (cheakList.contains(employeeInfo.getEmployeeId())) {
                    continue;
                }
                cheakList.add(employeeInfo.getEmployeeId());
                result.add(employeeInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @ApiOperation(value = "项目经理关键字查询", notes = "callbackPageSize,最大返回条数，拼接与URL")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "员工名称")
    })
    @ResponseBody
    @RequestMapping(value = "project/manager/search", method = RequestMethod.POST)
    public List<EmployeeInfo> customerManagerSearch(@RequestBody PermissionEmployeeFilter filter, Integer callbackPageSize, LoginEmployee loginEmployee) {
        if (null == filter) {
            filter = new PermissionEmployeeFilter();
        }

        filter.setDepartmentId(loginEmployee.getCompanyId());
//        filter.setAuthKey(Constants.AUTH_KEY_TGM_MANAGE);
//        filter.setPermissionKey(Constants.CUSTOMER_MANAGER_PERMISSION_KEY);

        return employeeSupportService.findSpecifiedPermissionEmployee(filter,callbackPageSize == null ? 15 : callbackPageSize);
    }

    /**
     * 所有客户经理，作为条件等使用,多业务区域
     */
    @ResponseBody
    @RequestMapping(value = "customer/manager/areaCodes/search", method = RequestMethod.POST)
    public List<EmployeeInfo> customerManagerAreaCodesSearch(@RequestBody CommonsVo commonsVo,
                                                             LoginEmployee loginEmployee) {
        try {
            String areaCodes = commonsVo.getAreaCodes();
            if (StringUtils.isBlank(areaCodes)) {
                return new ArrayList<EmployeeInfo>();
            }

            return employeeService.findEmployeeInfos(areaCodes.split(","), Constants.AUTH_KEY_TGM_MANAGE,
                    Constants.CUSTOMER_MANAGER_PERMISSION_KEY, loginEmployee);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<EmployeeInfo>();
    }

    /**
     * 根据客户经理ID获取客户经理
     */
    @ResponseBody
    @RequestMapping(value = "{managerId}/load/customeManage", method = RequestMethod.GET)
    public User loadManageById(@PathVariable Integer managerId, LoginEmployee loginEmployee) {
        if (null == managerId) {
            return null;
        }

        try {
            return employeeService.loadUserByEmployeeId(managerId, loginEmployee);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 所有客户经理，作为条件等使用,多业务区域（当前登录人所有的业务区域）
     */
    @ResponseBody
    @RequestMapping(value = "customer/manager/areaCodes/all/search", method = RequestMethod.GET)
    public List<EmployeeInfo> customerManagerAreaCodesAllSearch(CurrentUser currentUser, LoginEmployee loginEmployee) {
        try {
            Set<BusinessAreaNode> businessAreas = currentUser.getBusinessAreas();
            if (businessAreas.isEmpty()) {
                return new ArrayList<EmployeeInfo>();
            }

            String[] areaCodes = new String[businessAreas.size()];
            int index = 0;
            for (BusinessAreaNode businessAreaNode : businessAreas) {
                areaCodes[index] = businessAreaNode.getAreaCode();
                index++;
            }

            return employeeService.findEmployeeInfos(areaCodes, Constants.AUTH_KEY_TGM_MANAGE,
                    Constants.CUSTOMER_MANAGER_PERMISSION_KEY, loginEmployee);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<EmployeeInfo>();
    }

    /**
     * 所有后台人员，作为条件等使用，可以根据名字查询，每次返回15条
     */
    @ApiOperation(value = "根据用户姓名模糊查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "employeeFilter.name", value = "用户姓名，可不传", dataType = "String"),
            @ApiImplicitParam(name = "backPageSize", value = "返回数据数量,默认15条，最大200条,拼接到URL后面，非必须",
                    dataType = "Integer", defaultValue = "15")})
    @ResponseBody
    @RequestMapping(value = "employee/search", method = RequestMethod.POST)
    public Object employeeSearch(@RequestBody EmployeeFilter employeeFilter, Integer backPageSize, LoginEmployee loginEmployee) {
        try {
            backPageSize = backPageSize == null ? 15 : (NumberUtils.compare(backPageSize, 200) == 1 ? 200 : backPageSize);
            PageQueryCondition<EmployeeFilter> pageQueryCondition = new PageQueryCondition<EmployeeFilter>(
                    employeeFilter);
            pageQueryCondition.setPageNo(1);
            pageQueryCondition.setPageSize(backPageSize);
            Page<EmployeeInfo> page = employeeService.searchEmployees(pageQueryCondition, loginEmployee);

            if (null == page || CollectionUtils.isEmpty(page.getResults())) {
                return new ArrayList<EmployeeInfo>();
            }
            return page.getResults();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<EmployeeInfo>();
    }

    /**
     * 获取向上第一个逻辑区域及自己的名字
     */
    @ResponseBody
    @RequestMapping(value = "load/{areaCode}/logicAndSelfAreaName")
    public String loadLogicAndSelfAreaName(@PathVariable String areaCode, LoginEmployee loginEmployee) {
        return businessAreaCommonService.loadLogicAndSelfAreaName(areaCode, loginEmployee);
    }

    /**
     * 设置redis缓存信息
     */
    @ResponseBody
    @RequestMapping(value = "setting/redis/info", method = RequestMethod.GET)
    public void settingRedis(String key, String value, Integer checkCode) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value) || null == checkCode) {
            return;
        }

        if (!checkCode.equals(894519)) {
            return;
        }

        redisClient.set(key, value);
    }

    /**
     * 获取redis缓存信息
     */
    @ResponseBody
    @RequestMapping(value = "getting/redis/info", method = RequestMethod.GET)
    public Object gettingRedis(String key, Integer checkCode) {
        if (StringUtils.isBlank(key) || null == checkCode) {
            return null;
        }

        if (!checkCode.equals(898619)) {
            return null;
        }

        return redisClient.get(key);
    }

    /**
     * 删除redis缓存信息
     */
    @ResponseBody
    @RequestMapping(value = "del/redis/info", method = RequestMethod.GET)
    public void delRedis(String key, Integer checkCode) {
        if (StringUtils.isBlank(key) || null == checkCode) {
            return;
        }

        if (!checkCode.equals(894519856)) {
            return;
        }

        redisClient.del(key);
    }

    /**
     * 服务器当前时间
     */
    @ResponseBody
    @RequestMapping(value = "cdate", method = RequestMethod.GET)
    public Date cdate() {
        return new Date();
    }

    /**
     * 根据服务器当前时间获取指定时间:可以指定服务器当前时间前后N天
     */
    @ResponseBody
    @RequestMapping(value = "design/date", method = RequestMethod.GET)
    public DesignDateVo designDate(Integer day) {
        DesignDateVo vo = new DesignDateVo();
        if (null == day) {
            vo.setStartTime(new Date());
            vo.setEndTime(DateUtil.parse(DateUtil.dayAddEnd(1), null));
            return vo;
        }

        String dayAddStart = DateUtil.dayAddStart(day);
        if (NumberUtils.compare(0, day) == -1) {
            vo.setStartTime(new Date());
            vo.setEndTime(DateUtil.parse(dayAddStart, null));
        } else {
            vo.setStartTime(DateUtil.parse(dayAddStart, null));
            vo.setEndTime(DateUtil.parse(DateUtil.dayAddEnd(1), null));
        }
        return vo;
    }
    
    /**
     * 根据服务器当前时间获取指定时间:可以指定服务器当前时间前后N个月
     */
    @ApiOperation(value = "根据服务器当前时间获取指定时间", notes = "可以指定服务器当前时间前后N个月")
    @ApiImplicitParam(name = "month", value = "当前月加、减的月数， 拼接于URL", paramType = "query")
    @ResponseBody
    @RequestMapping(value = "design/month/date", method = RequestMethod.GET)
    public DesignDateVo designMonthDate(Integer month) {
        DesignDateVo vo = new DesignDateVo();
        // 用户没有参数回传，设置默认值
        if (null == month || NumberUtils.compare(0, month) == 0) {
            vo.setStartTime(DateUtil.getFirstDayOfMonth(new Date()));
            vo.setEndTime(new Date());
            return vo;
        }

        Date date = DateUtil.getFirstDayOfMonth(new Date());
        if (NumberUtils.compare(0, month) == -1) {
            vo.setStartTime(DateUtil.getFirstDayOfMonth(new Date()));
            vo.setEndTime(DateUtil.addMonths(date, month));
            return vo;
        }
        
        vo.setStartTime(DateUtil.getFirstDayOfMonth(DateUtil.addMonths(date, month)));
        vo.setEndTime(new Date());
        return vo;
    }

    /**
     * 获取客户端的IP以及默认城市
     */
    @ResponseBody
    @RequestMapping(value = "ip/address", method = RequestMethod.GET)
    public CommonsVo ipAddress() {
        CommonsVo vo = new CommonsVo();
        String ipAddress = WebUtils.getIpAddress();
        if (StringUtils.isBlank(ipAddress)) {
            return vo;
        }
        vo.setIpAddress(ipAddress);

        IpAddressComponent component = gaoDeMapService.findRegionCodeByIpAddress(ipAddress);
        if (null == component) {
            return vo;
        }

        Region region = regionTgmService.findByProvinceAndCityName(component.getProvince(), component.getCity());
        if (null == region) {
            return vo;
        }
        vo.setRegionCode(region.getRegionCode());
        vo.setRegionName(region.getRegionName());
        return vo;
    }

    @ResponseBody
    @RequestMapping(value = "url", method = RequestMethod.GET)
    public String getUrl(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String url = request.getRequestURL().toString();
        return url.replace(servletPath, "/");
    }

    /**
     * 获取配置文件内容
     * @param property
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{property}/getProperty", method = RequestMethod.GET)
    public String getProperty(@PathVariable("property") String property) {
        String rst = environment.getProperty(property);
        return rst;
    }

    /**
     * 
     * 获取模板下载地址
     * 
     * @author Libin.Wei
     * @Date 2018年6月27日 上午10:32:18
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{templetTypeKey}/loadTempletDownLoadUrl", method = RequestMethod.GET)
    public String loadTempletDownLoadUrl(@PathVariable("templetTypeKey") String templetTypeKey) {
        if (StringUtils.isBlank(templetTypeKey)) {
            return null;
        }

        ConfParamOption option = confParamService.findParamOption("TEMPLET_EXCEL", templetTypeKey);
        if (null == option) {
            return null;
        }

        String url = option.getOptionDescribed();
        if (StringUtils.isBlank(url)) {
            return null;
        }

        // 阿里云地址
        String rst = environment.getProperty("aliyun.oss.image.host");
        if (StringUtils.isBlank(rst)) {
            return null;
        }

        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        return "http://" + rst + url;
    }

    /**
     * 
     * base62加密
     * 
     * @author Libin.Wei
     * @Date 2018年8月17日 上午10:54:13
     * @param number
     * @param isMultiply1000
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "base62/encode", method = RequestMethod.GET)
    public String base62Encode(Integer number, boolean isMultiply1000) {
        Assert.notNull(number, "base62Encode param number must be not null");
        return isMultiply1000 ? Base62.encodeByMultiply(number) : Base62.encode(BigInteger.valueOf(number));
    }

    /**
     * 
     * base62解密
     * 
     * @author Libin.Wei
     * @Date 2018年8月17日 上午10:54:39
     * @param key
     * @param isDivide1000
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "base62/decode", method = RequestMethod.GET)
    public BigInteger base62Decode(String key, boolean isDivide1000) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        if (!StringUtils.isNumeric(key)) {
            return null;
        }

        return isDivide1000 ? Base62.decodeByDivide(key) : Base62.decode(key);
    }

    @ApiOperation(value = "公共方法，用于校验权限", notes = "当方法为非ResponseBody返回时，先调用此接口校验权限，确保提示正常")
    @ResponseBody
    @RequestMapping(value = "check/operatingAuthority", method = RequestMethod.GET)
    public void checkOperatingAuthority(LoginEmployee loginEmployee) {
    }

    @ApiOperation(value = "判断当前登录人有没有城市经理数据权限", notes = "true：有；false：没有")
    @ResponseBody
    @RequestMapping(value = "check/loginUser/isCityManage", method = RequestMethod.GET)
    public boolean checkLoginUserIsCityManage(LoginEmployee loginEmployee) {
        return employeeService.isPermission(Constants.AUTH_KEY_TGM_MANAGE, Constants.CITY_MANAGER_PERMISSION_KEY, loginEmployee);
    }
}
