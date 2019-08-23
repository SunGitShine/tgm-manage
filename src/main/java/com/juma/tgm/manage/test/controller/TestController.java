/**
 * 
 */
package com.juma.tgm.manage.test.controller;

import com.giants.common.exception.BusinessException;
import com.juma.auth.authority.service.AuthorityService;
import com.juma.auth.user.domain.EcoUserAuthInfo;
import com.juma.auth.user.domain.LoginUser;
import com.juma.auth.user.domain.User;
import com.juma.auth.user.service.UserService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.waybill.service.WaybillCommonService;
import com.juma.vms.common.Constants;
import com.juma.vms.vendor.domain.Vendor;
import com.juma.vms.vendor.external.VendorTenantExternal;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author vencent.lu
 *
 */
@Controller
@RequestMapping(value = "test")
public class TestController {
    @Resource
    private WaybillCommonService waybillCommonService;
    @Resource
    private AuthorityService authorityService;
    @Resource
    private UserService userService;
    @Resource
    private VmsCommonService vmsCommonService;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "cancel/flightUsage", method = RequestMethod.GET)
    public void cancelFlightUsage(String checkCode, Integer waybillId, Integer flightUsageId) {
        if (StringUtils.isBlank(checkCode)) {
            throw new BusinessException("errors.common.prompt", "errors.common.prompt", "校验码错误，拒绝非法更改");
        }

        if (!checkCode.equals("13575456")) {
            throw new BusinessException("errors.common.prompt", "errors.common.prompt", "校验码错误，拒绝非法更改");
        }

    }

    @ResponseBody
    @RequestMapping(value = "waybillAddVehicleTovendorByTruckId", method = RequestMethod.GET)
    public void waybillAddVehicleTovendorByTruckId(String checkCode, Integer truckId, Integer vendorId) {
        if (StringUtils.isBlank(checkCode)) {
            throw new BusinessException("errors.common.prompt", "errors.common.prompt", "校验码错误，拒绝非法更改");
        }

        if (!checkCode.equals("13575456")) {
            throw new BusinessException("errors.common.prompt", "errors.common.prompt", "校验码错误，拒绝非法更改");
        }

        if (null == truckId && null == vendorId) {
            return;
        }

        waybillCommonService.doWaybillAddVehicleTovendorByTruckId(truckId, vendorId);
    }

    public static void main(String[] args) {
        // int a = 1;
        // System.out.println(a + "");

        // Integer a = null;
        //
        // System.out.println(a instanceof Object);
        // System.out.println(a instanceof Integer);
        // System.out.println((Integer) a);
        //
        // int[] b = {1,2};
        // int[] copyOf = Arrays.copyOf(b, 1);
        // System.out.println();
        //
        // BeanUtils.copyProperties(new CustomerInfo(), new
        // ConsignorCustomerInfo());

        // testDate();

        // List<String> a = new ArrayList<String>();
        // a.add("00");
        // if (a.contains("00")) {
        // a.remove("00");
        // }
        System.out.println(BaseUtil.checkTelephone("02356854256"));
    }

    private static void testMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("b", "123");
        for (String key : map.keySet()) {
            String value = map.get(key);
            System.out.println(key + "  " + value);
        }

        Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        // if (map.containsKey("a")) {
        // Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
        // System.out.println("map iterator has key:" + iterator);
        //// System.out.println("map iterator has value:" + map.keySet());
        // System.out.println("map has key:" + map.keySet() +
        // map.get(map.keySet()));
        // }
        System.out.println("map has not key:" + map.keySet());
    }

    private static void testDate() {
        String plan_delivery_time = "NaN-aN-aN 00:00:00";
        // String plan_delivery_time = "2018-06-01 00:00:00";
        System.out.println(plan_delivery_time.contains("NaN"));
        System.out.println(DateUtil.parse(plan_delivery_time, null));

    }

    @ResponseBody
    @RequestMapping(value = "vendorAuthorizationEcoUser", method = RequestMethod.GET)
    public void vendorAuthorizationEcoUser(Integer vendorId, Integer tenantId, String tenantCode, String checkCode) {
        if (null == vendorId || null == tenantId || StringUtils.isBlank(checkCode) || StringUtils.isBlank(tenantCode)) {
            return;
        }

        if (!checkCode.equals("2342354")) {
            return;
        }

        Vendor vendor = vmsCommonService.loadVendorByVendorId(vendorId);
        if (null == vendor) {
            return;
        }

        VendorTenantExternal external = vmsCommonService
            .loadVendorTenantByVendorId(vendorId, new LoginUser(tenantId, 1));
        if (null == external) {
            return;
        }

        LoginUser loginUser = new LoginUser(tenantId, vendor.getCreateUserId());
        loginUser.setTenantCode(tenantCode);


        EcoUserAuthInfo ecoUserAuthInfo = new EcoUserAuthInfo();
        User user = userService.loadUser(User.UserUniqueAttribute.mobileNumber, vendor.getContactPhone());
        if (user == null) {
            user = new User();
            // 用户基础信息
            user.setMobileNumber(vendor.getContactPhone());
            user.setName(vendor.getVendorName());
        }

        ecoUserAuthInfo.setUser(user);
        // 系统角色
        ecoUserAuthInfo.setAuthKey(Constants.AUTH_KEY_TGM_DRIVER);
        ecoUserAuthInfo.setRoleKey(Constants.ROLE_KEY_VENDOR);

        // 业务区域
        ecoUserAuthInfo.setAreaCode(external.getAreaCode());

        authorityService.authorizationEcoUser(ecoUserAuthInfo, loginUser);
    }

}
