package com.juma.tgm.manage.crm.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.auth.user.domain.User;
import com.juma.cms.wx.domain.Chanel;
import com.juma.tgm.authority.service.TgmUserCenterService;
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.crm.domain.CrmCustomerInfo;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.domain.UserUnderCustomer;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.customer.domain.CargoOwnerCustomer;
import com.juma.tgm.customer.domain.TruckCustomer;
import com.juma.tgm.customer.domain.vo.CargoOwnerCustomerVo;
import com.juma.tgm.customer.domain.vo.CargoOwnerVo;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.user.domain.CurrentUser;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author weilibin
 * @version V1.0
 * @Description: 大客户管理
 * @date 2016年8月25日 上午10:58:09
 */

@Controller
@RequestMapping(value = "customerInfo")
public class CustomerInfoController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CustomerInfoController.class);

    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private ExportTaskService exportTaskService;

    @Resource
    private TgmUserCenterService tgmUserCenterService;
    
    @ResponseBody
    @RequestMapping(value = "chanel")
    public List<Chanel> chanel() {
        return customerInfoService.findCustomerSourceByCode("CUSTOMER_MANAGER");
    }

    /**
     * @return Page<ConsignorContactsInfo>
     * @Title: search
     * @Description: 企业用车人
     */
    @ResponseBody
    @RequestMapping(value = "{customerId}/contacts", method = RequestMethod.POST)
    public Page<TruckCustomer> contacts(@PathVariable("customerId") Integer crmCustomerId) {
        return new Page<>(1, 1, 0, new ArrayList<TruckCustomer>());
    }

    /**
     * @return Page<ConsignorContactsInfo>
     * @Title: search
     * @Description: 企业用车人
     */
    @ResponseBody
    @RequestMapping(value = "tgm/{customerId}/contacts", method = RequestMethod.GET)
    public List<TruckCustomer> contactsByCustomerId(@PathVariable("customerId") Integer customerId) {
        return new ArrayList<>();
    }

    /**
     * 添加用车人
     *
     * @param cargoOwnerVo
     * @return
     */
    @RequestMapping(value = "addCargoOwner", method = RequestMethod.POST)
    @ResponseBody
    public CargoOwnerCustomerVo addCargoOwner(@RequestBody CargoOwnerVo cargoOwnerVo, LoginEmployee loginEmployee) {
        //通过企业客户id获取数据
        if (cargoOwnerVo.getCustomerInfo() == null) {
            throw new BusinessException("customerInfoNull", "errors.paramCanNotNullWithName", "所属企业");
        }

        if (cargoOwnerVo.getCustomerInfo().getCustomerId() == null) {
            throw new BusinessException("customerInfoIdNull", "errors.paramCanNotNullWithName", "企业Id");
        }
        CustomerInfo customerInfo = customerInfoService.findByCrmId(cargoOwnerVo.getCustomerInfo().getCustomerId());
        if (customerInfo == null) {
            throw new BusinessException("customerInfo.error.notFound", "customerInfo.error.notFound");
        }
        cargoOwnerVo.setCustomerInfo(customerInfo);

        CargoOwnerCustomerVo vo = tgmUserCenterService.createCargoOwnerBelongEnterprise(cargoOwnerVo.getTruckCustomer(), cargoOwnerVo.getCustomerInfo(), loginEmployee);

        return vo;
    }

    /**
     * 用车人编辑
     */
    @RequestMapping(value = "cargoOwner/update", method = RequestMethod.POST)
    @ResponseBody
    public void cargoOwnerUpdate(@RequestBody TruckCustomer truckCustomer, LoginEmployee loginEmployee) throws BusinessException {
    }

    @ResponseBody
    @RequestMapping(value = "unbinding", method = RequestMethod.DELETE)
    public void unbinding(@RequestBody CargoOwnerCustomer cargoOwnerCustomer, LoginEmployee loginEmployee) {
    }

    /**
     * @return Page<UserUnderCustomer>
     * @Title: search
     * @Description: 用户下客户
     */
    @ResponseBody
    @RequestMapping(value = "searchUserUnderCustomer", method = RequestMethod.POST)
    public Page<UserUnderCustomer> searchUserUnderCustomer(PageCondition pageCondition, LoginEmployee loginEmployee) {
       return customerInfoService.searchUserUnderCustomer(pageCondition, loginEmployee);
    }

    /**
     * @return Page<TruckCustomerBo>
     * @Title: search
     * @Description: 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<CrmCustomerInfo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        return customerInfoService.searchDetails(pageCondition, loginEmployee);
    }

    /**
     * @Title: edit
     * @Description: 跳转到编辑页面
     */
    @ResponseBody
    @RequestMapping(value = "{crmCustomerId}/json/edit", method = RequestMethod.GET)
    public CrmCustomerInfo jsonEdit(@PathVariable Integer crmCustomerId, LoginEmployee loginEmployee) {
        CrmCustomerInfo crmCustomer = customerInfoService.findCrmCustomerInfoByCustomerId(crmCustomerId, loginEmployee);
        BigDecimal rebateRate = crmCustomer.getRebateRate();
        if (null != rebateRate) {
            crmCustomer.setRebateRate(crmCustomer.getRebateRate().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP));
        }
        return crmCustomer;
    }

    /**
     * 根据customerId获取tms客户信息
     */
    @ResponseBody
    @RequestMapping(value = "{customerId}/json/detail", method = RequestMethod.GET)
    public CustomerInfo jsonDetail(@PathVariable Integer customerId, LoginEmployee loginEmployee) {
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(customerId);
        if (null == customerInfo) {
            return customerInfo;
        }

        try {
            User user = employeeService.loadUserByEmployeeId(customerInfo.getCustomerManagerUserId(), loginEmployee);
            customerInfo.setCustomerManagerUserName(user.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return customerInfo;
    }

    /**
     * @Title: update
     * @Description: 编辑
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody CrmCustomerInfo crmCustomer, LoginEmployee loginEmployee) throws BusinessException {
        customerInfoService.update(crmCustomer, loginEmployee);
    }

    /**
     * excel导出
     */
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        // 初始化任务
        Integer exportTaskId = exportTaskService.insertInit(ExportTask.TaskSign.CUSTOMER_INFO, exportParam, loginEmployee);
        try {
            // 获取数据并上传云
            PageCondition pageCondition = new PageCondition();
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
            pageCondition.setFilters(exportParam.getFilters());
            super.formatAreaCodeToList(pageCondition, false);
            customerInfoService.asyncExport(pageCondition, exportTaskId, loginEmployee);
        } catch (Exception e) {
            exportTaskService.failed(exportTaskId, e.getMessage(), loginEmployee);
            log.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "根据客户名称模糊获取当前登录人所有的客户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isAllCustomerStatus", value = "0、不包含已淘汰；1、所有状态", dataType = "Integer"),
            @ApiImplicitParam(name = "customerInfo.customerName", value = "项目名称", dataType = "String"),
            @ApiImplicitParam(name = "backPageSize", value = "返回条数，拼接在URL，非必须，默认15条，最大200条", dataType = "Integer")
    })
    @ResponseBody
    @RequestMapping(value = "all/area/{isAllCustomerStatus}/customerInfo", method = RequestMethod.POST)
    public List<CustomerInfo> allAreaCustomerInfo(@PathVariable Integer isAllCustomerStatus,
                                                  @RequestBody CustomerInfo customerInfo, CurrentUser currentUser,
                                                  Integer backPageSize, LoginEmployee loginEmployee) {
        PageCondition pageCondition = getPageCondition(isAllCustomerStatus, customerInfo, backPageSize, currentUser);
        pageCondition.getFilters().put("tenantId", loginEmployee.getTenantId());

        return customerInfoService.listCustomerInfo(pageCondition);
    }

    /**
     * 根据租户id获取当前登录人所有的业务区域的客户信息
     */
    @ResponseBody
    @RequestMapping(value = "all/area/{isAllCustomerStatus}/customerInfoByTenantId", method = RequestMethod.POST)
    public List<CustomerInfo> allAreaCustomerInfoByTenantId(@PathVariable Integer isAllCustomerStatus,
                                                            @RequestBody CustomerInfo customerInfo,
                                                            Integer backPageSize, CurrentUser currentUser) {
        if (null == customerInfo.getTenantId()) {
            return null;
        }
        PageCondition pageCondition = getPageCondition(isAllCustomerStatus, customerInfo, backPageSize, currentUser);
        pageCondition.getFilters().put("tenantId", customerInfo.getTenantId());
        return customerInfoService.listCustomerInfo(pageCondition);
    }

    /**
     * 根据租户id获取客户信息
     */
    @ResponseBody
    @RequestMapping(value = "all/customerInfo", method = RequestMethod.POST)
    public List<CustomerInfo> allCustomerInfo(@RequestBody CustomerInfo customerInfo, Integer backPageSize) {
        if(null == customerInfo.getTenantId()){
            return null;
        }
        PageCondition pageCondition = getPageCondition(0, customerInfo, backPageSize, null);
        pageCondition.getFilters().put("tenantId", customerInfo.getTenantId());
        return customerInfoService.listCustomerInfo(pageCondition);
    }

    private PageCondition getPageCondition(@PathVariable Integer isAllCustomerStatus,
                                           @RequestBody CustomerInfo customerInfo, Integer backPageSize,
                                           CurrentUser currentUser) {
        backPageSize = backPageSize == null ? 15 : (NumberUtils.compare(backPageSize, 200) == 1 ? 200 : backPageSize);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(backPageSize);
        pageCondition.getFilters().put("customerName", customerInfo.getCustomerName());
        pageCondition.getFilters().put("customerType",
                com.juma.crm.customer.domain.CustomerInfo.CustomerType.CONSIGNOR.getValue());
        if (null == isAllCustomerStatus || !isAllCustomerStatus.equals(1)) {
            pageCondition.getFilters().put("statusNotEquals",
                    com.juma.crm.customer.domain.CustomerInfo.CustomerStatus.ELIMINATED.getValue());
        }
        List<String> areaCodeList = new ArrayList<String>();
        if (null != currentUser) {
            Set<BusinessAreaNode> businessAreas = currentUser.getBusinessAreas();
            for (BusinessAreaNode businessAreaNode : businessAreas) {
                areaCodeList.add(businessAreaNode.getAreaCode());
            }
            pageCondition.getFilters().put("areaCodeList", areaCodeList);
        }
        return pageCondition;
    }
}
