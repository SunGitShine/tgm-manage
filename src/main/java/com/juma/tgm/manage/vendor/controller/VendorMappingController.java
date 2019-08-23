package com.juma.tgm.manage.vendor.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.tenant.domain.Tenant;
import com.juma.auth.tenant.service.TenantService;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.vendor.domain.VendorMapping;
import com.juma.tgm.vendor.domain.VendorMappingBo;
import com.juma.tgm.vendor.domain.VendorProjectMappingBo;
import com.juma.tgm.vendor.service.VendorMappingService;
import com.juma.vms.external.service.VmsService;
import com.juma.vms.vendor.vo.VendorQuery;

@Controller
@RequestMapping("vendor")
public class VendorMappingController {
    @Resource
    private TenantService tenantService;
    @Resource
    private VendorMappingService vendorMappingService;
    @Resource
    private VmsService vmsService;

    /**
     * 运单共享关系配置列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<VendorMappingBo> search(PageCondition pageCondition) {
        return vendorMappingService.search(pageCondition);
    }

    /**
     * 运单共享关系配置新增或保存
     */
    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Integer saveOrUpdate(@RequestBody VendorMappingBo vendorMappingBo, LoginEmployee loginEmployee) throws BusinessException {
        if(null == vendorMappingBo.getVendorMappingId()){
           return vendorMappingService.save(vendorMappingBo, loginEmployee);
        }else {
           return vendorMappingService.update(vendorMappingBo, loginEmployee);
        }
    }

    /**
     * 运关系维护删除承
     */
    @ResponseBody
    @RequestMapping(value = "{vendorMappingId}/deleteVendor", method = RequestMethod.DELETE)
    public void deleteVendor(@PathVariable Integer vendorMappingId) {
        vendorMappingService.deleteVendor(vendorMappingId);
    }

    /**
     * 主表id搜索项目关系配置列表
     */
    @ResponseBody
    @RequestMapping(value = "listByVendorMappingId", method = RequestMethod.POST)
    public Page<VendorProjectMappingBo> listByVendorMappingId(PageCondition pageCondition) {
        return vendorMappingService.listByVendorMappingId(pageCondition);
    }

    /**
     * 项目关系配置新增或保存
     */
    @ResponseBody
    @RequestMapping(value = "saveOrUpdateVendorProjectMapping", method = RequestMethod.POST)
    public void saveOrUpdateVendorProjectMapping(@RequestBody VendorProjectMappingBo vendorProjectMappingBo) throws BusinessException {
        vendorMappingService.saveOrUpdateVendorProjectMapping(vendorProjectMappingBo);
    }

    /**
     * 项目关系配置删除
     */
    @ResponseBody
    @RequestMapping(value = "{vendorProjectMappingId}/delete", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer vendorProjectMappingId) {
        vendorMappingService.delete(vendorProjectMappingId);
    }

    /**
     * 运单共享关系配置编辑
     */
    @ResponseBody
    @RequestMapping(value = "{vendorMappingId}/edit", method = RequestMethod.GET)
    public VendorMappingBo edit(@PathVariable Integer vendorMappingId) {
        return vendorMappingService.edit(vendorMappingId);
    }

    /**
     * 获取所有租户
     */
    @ResponseBody
    @RequestMapping(value = "allTenant", method = RequestMethod.GET)
    public List<Tenant> findAllTenant(){
        return tenantService.findAllTenant();
    }

    /**
     * 获取承运商名称
     *
     * pageCondition可使用查询条件如下
     * <p>
     * name: 承运商名称
     * </p>
     */
    @ResponseBody
    @RequestMapping(value = "findVendorName", method = RequestMethod.POST)
    public List<VendorQuery> findVendorName(@RequestBody VendorQuery vendorQuery) {
        if (null == vendorQuery.getTenantId()) {
            return new ArrayList<VendorQuery>();
        }

        return vmsService.listVendorByVendorNameLike(vendorQuery.getVendorName(), null, 20,
                new LoginUser(vendorQuery.getTenantId(), 1));
    }

    @ResponseBody
    @RequestMapping(value = "{vendorId}/get", method = RequestMethod.GET)
    public VendorMapping getById(@PathVariable Integer vendorId, LoginEmployee loginEmployee) {
        return vendorMappingService.getVendorMapping(vendorId, loginEmployee);
    }
    
}
