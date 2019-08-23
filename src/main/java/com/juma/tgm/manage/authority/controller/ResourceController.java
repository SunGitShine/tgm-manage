/**
 *
 */
package com.juma.tgm.manage.authority.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author vencent.lu
 */
@Controller
@RequestMapping(value = "resource")
public class ResourceController {

//    @Autowired
//    private ResourceService resourceService;

//    private String getSystemAuthKey(String authKey) {
//        switch (authKey) {
//            case "TGM_MANAGE":
//                return Constants.AUTH_KEY_TGM_MANAGE;
//            case "TGM_DRIVER":
//                return Constants.AUTH_KEY_TGM_DRIVER;
//            case "TGM_CUSTOMER":
//                return Constants.AUTH_KEY_TGM_CUSTOMER;
//            default:
//                return null;
//        }
//    }
//
//    @RequestMapping(value = "{authKey}/tree", method = RequestMethod.GET)
//    @ResponseBody
//    public List<ResourceBo> tree(@PathVariable String authKey,
//                                 LoginEmployee loginEmployee) {
//        return this.resourceService
//                .findResourceTree(this.getSystemAuthKey(authKey));
//    }
//
//    @RequestMapping(value = "menu", method = RequestMethod.GET)
//    @ResponseBody
//    public List<ResourceBo> menu(LoginEmployee loginEmployee) {
//        if (!loginEmployee.isSysUser()) {
//            return this.resourceService.findMenuResource(Constants.AUTH_KEY_TGM_MANAGE,
//                    loginEmployee);
//        } else {
//            return this.resourceService.findResourceTree(Constants.AUTH_KEY_TGM_MANAGE);
//        }
//    }

}
