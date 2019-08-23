package com.juma.tgm.manage.authority.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.user.domain.CurrentUser;

@Controller
public class DepartmentACController {

    
    private final Logger log = LoggerFactory.getLogger(DepartmentACController.class);
//    @Resource
//    private DepartmentService departmentService;
//
//    @Resource
//    private DepartmentACService departmentACService;
//
//    @Resource
//    private CityManageService cityManageService;
//
//    @Resource
//    private ConfParamService confParamService;
//
//    /**
//     * @throws CategoryCodeFormatException
//     * @throws BusinessException
//     *
//     * @Title:       userDepartment
//     * @Description: 用户部门树 session
//     * @param:       @param departmentId
//     * @param:       @return
//     * @return:      Department
//     * @throws
//     */
    @RequestMapping(value="user/department",method= RequestMethod.GET)
    @ResponseBody
    public Set<BusinessAreaNode> userDepartment(@ModelAttribute("currentUser") CurrentUser currentUser, LoginEmployee loginEmployee) {
        log.info("CurrentUser:{}", JSON.toJSONString(currentUser));
        return currentUser.getBusinessAreas();
    }
//
//    @RequestMapping(value = "departmentac/{departmentId}/get", method = RequestMethod.GET)
//    @ResponseBody
//    public DepartmentAC get(@PathVariable Integer departmentId) {
//        return departmentACService.findByDepartmentId(departmentId);
//    }
//
//    @RequestMapping(value = "departmentac/save", method = RequestMethod.POST)
//    @ResponseBody
//    public void save(@RequestBody DepartmentAC departmentAC) {
//        departmentACService.saveDepartmentAC(departmentAC);
//    }
//
//    @RequestMapping(value = "department/node", method = RequestMethod.GET)
//    @ResponseBody
//    public List<Department> departmentNode(Integer id) {
//        return departmentACService.findChildDepartment(id);
//    }
//
//    @RequestMapping(value = "department/all", method = RequestMethod.GET)
//    @ResponseBody
//    public List<Department> departmentAllNode() {
//        return departmentACService.findAllDepartment();
//    }
//
//    @RequestMapping(value = "city/node", method = RequestMethod.GET)
//    @ResponseBody
//    public List<CityManage> regionNode() throws BusinessException, CategoryCodeFormatException {
//        return cityManageService.findCityBy(CityManage.Sign.AREA_MANAGE.getCode());
//    }
//
//    @RequestMapping(value = "rule/keys", method = RequestMethod.GET)
//    @ResponseBody
//    public List<ConfParamOption> ruleKeys() throws BusinessException, CategoryCodeFormatException {
//        return confParamService.findParamOptions("BUZ_RULE");
//    }

}
