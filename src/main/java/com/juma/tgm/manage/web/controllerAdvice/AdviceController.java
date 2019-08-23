package com.juma.tgm.manage.web.controllerAdvice;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.juma.auth.authority.service.AuthorityService;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.authority.controller.DepartmentACController;
import com.juma.tgm.manage.configure.controller.ConfigParamOptionController;
import com.juma.tgm.manage.crm.controller.CustomerInfoController;
import com.juma.tgm.manage.flightUsage.controller.FlightUsageController;
import com.juma.tgm.manage.landing.controller.FreightRuleController;
import com.juma.tgm.manage.landing.controller.PackFreightRuleController;
import com.juma.tgm.manage.maps.controller.MapsController;
import com.juma.tgm.manage.truck.controller.TruckController;
import com.juma.tgm.manage.truck.controller.TruckFleetController;
import com.juma.tgm.manage.truck.controller.TruckTypeCityController;
import com.juma.tgm.manage.truck.controller.TruckTypeFreightController;
import com.juma.tgm.manage.waybill.controller.DeliveryPointSupplementController;
import com.juma.tgm.manage.waybill.controller.SupplementDeliveryController;
import com.juma.tgm.manage.waybill.controller.WaybillAssigneController;
import com.juma.tgm.manage.waybill.controller.WaybillAutoMatchController;
import com.juma.tgm.manage.waybill.controller.WaybillController;
import com.juma.tgm.manage.waybill.controller.WaybillPendingController;
import com.juma.tgm.manage.waybillAccount.controller.WaybillReconciliationController;
import com.juma.tgm.manage.waybillAccount.controller.WaybillReportController;
import com.juma.tgm.manage.web.controller.CommonsController;
import com.juma.tgm.user.domain.CurrentUser;

/**
 * @ClassName: AdviceController
 * @Description:
 * @author: liang
 * @date: 2017-03-06 17:38
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
// @ControllerAdvice(basePackages = {"com.juma.tgm.manage"})
@ControllerAdvice(assignableTypes = { WaybillController.class, WaybillAutoMatchController.class,
        WaybillReportController.class, DepartmentACController.class, TruckFleetController.class, TruckController.class,
        FlightUsageController.class, MapsController.class, CommonsController.class,
        WaybillReconciliationController.class, WaybillAssigneController.class, DeliveryPointSupplementController.class,
        SupplementDeliveryController.class, CustomerInfoController.class, TruckTypeFreightController.class,
        WaybillPendingController.class, TruckTypeCityController.class, FreightRuleController.class,
        PackFreightRuleController.class, ConfigParamOptionController.class })
public class AdviceController {

    private static final Logger log = LoggerFactory.getLogger(AdviceController.class);

    @Resource
    private AuthorityService authorityService;

    @ModelAttribute("currentUser")
    public CurrentUser buildCurrentUser(LoginEmployee loginEmployee) {
        if (loginEmployee == null) {
            return null;
        }
        CurrentUser cu = new CurrentUser();
        cu.setTenantCode(loginEmployee.getTenantCode());
        cu.setTenantId(loginEmployee.getTenantId());
        try {
            this.findBusinessAreaTree(loginEmployee, cu);
        } catch (Exception e) {
            log.error("构造业务区域数据错误.", e);
        }
        return cu;
    }

    // 生成业务区域树
    private void findBusinessAreaTree(LoginEmployee loginEmployee, CurrentUser currentUser) {
        // Integer areaSize = null;
        // try {
        // areaSize =
        // loginEmployee.getLoginDepartment().getBusinessAreas().size();
        // } catch (Exception e) {
        // log.error("当前登录人业务区域数据错误.", e);
        // }
        // if (areaSize == null || areaSize.equals(0)) {
        // return;
        // }

        // for (LoginEmployee.LoginDepartment.BusinessArea ba :
        // loginEmployee.getLoginDepartment().getBusinessAreas()) {
        // List<BusinessArea> currentUserArea =
        // authorityService.findChildBusinessArea(ba.getAreaCode(),
        // loginEmployee);
        // currentUser.addBusinessAreas(currentUserArea);
        // }
        Set<BusinessAreaNode> rows = new HashSet<>();

        List<BusinessAreaNode> areaNodeTree = authorityService.findBusinessAreaTree(loginEmployee);
        this.parallelTree(areaNodeTree, rows);
        currentUser.addBusinessAreas(rows);
    }

    private void parallelTree(List<BusinessAreaNode> areaNodeTree, Set<BusinessAreaNode> rows) {
        if (areaNodeTree == null) {
            return;
        }
        // 深度优先算法
        Deque<BusinessAreaNode> dfs = new LinkedList<>();
        for (BusinessAreaNode areaNode : areaNodeTree) {
            dfs.push(areaNode);
        }
        while (!dfs.isEmpty()) {
            BusinessAreaNode node = dfs.pop();

            rows.add(node);
            if (CollectionUtils.isNotEmpty(node.getChildren())) {
                List<BusinessAreaNode> nodeLeafs = node.getChildren();

                if (CollectionUtils.isEmpty(nodeLeafs)) {
                    continue;
                }

                for (BusinessAreaNode n : nodeLeafs) {
                    dfs.add(n);
                }
            }
        }
        // 去掉孩子节点
        for (BusinessAreaNode node : rows) {
            node.setChildren(null);
        }
    }
}
