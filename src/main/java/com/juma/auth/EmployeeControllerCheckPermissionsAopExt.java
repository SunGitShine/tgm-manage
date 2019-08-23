package com.juma.auth;

import java.lang.reflect.Method;

/**
 * 
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.GiantsException;
import com.giants.common.exception.NotLoggedInException;
import com.giants.web.utils.WebUtils;
import com.juma.auth.authority.service.AuthorityService;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.customize.scan.CustomizeScanner;
import com.juma.log.domain.OperationLogInfo;
import com.juma.log.service.OperationLogService;
import com.juma.oms.manage.annotation.AuthKey;

/**
 * @author vencent.lu
 *
 */
public class EmployeeControllerCheckPermissionsAopExt {
    
    private final Logger log = LoggerFactory.getLogger(EmployeeControllerCheckPermissionsAopExt.class);
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    
    private List<String> authExcludeList;
    private List<Class<?>> argClassExcludeList;
    private AuthorityService authorityService;
    private OperationLogService operationLogService;
    private boolean isCheckAuth = true;
    
    /**
     * 直接使用用户中心LoginEmployee 检查登录、controller 方法访问权限
     * @param controller
     * @return
     * @throws Throwable
     */
    public Object controllerMethodAuth(ProceedingJoinPoint controller) throws Throwable {
        String authKey = "";
        Method method  =  ((MethodSignature)controller.getSignature()).getMethod();
        MethodBean methodBean = this.buildMethodBean(controller);
        String[] argNames = parameterNameDiscoverer
                .getParameterNames(((MethodSignature) controller.getSignature())
                        .getMethod());
        
        AuthKey methodAnn = method.getAnnotation(AuthKey.class);
        if(methodAnn != null) {
            authKey = methodAnn.value();
        } else {
            authKey = "TGM_MANAGE";
        }
        Map<String, Object> argMap = new HashMap<String, Object>();
        LoginEmployee loginEmployee = null;
        boolean isNeedAuth = false;
        if (methodBean.getArgTypeNames() != null) {
            for(int i=0; i<methodBean.getArgTypeNames().length; i++) {
                if (StringUtils.isEmpty(methodBean.getArgTypeNames()[i])) {
                    break;
                }
                boolean isExcludeArg = false;
                if (methodBean.getArgTypeNames()[i].equals(LoginEmployee.class.getName())) {
                    loginEmployee = (LoginEmployee)methodBean.getArgs()[i];
                    isNeedAuth = true;
                } else {
                    if (CollectionUtils.isNotEmpty(this.argClassExcludeList)) {
                        for (Class<?> cls : this.argClassExcludeList) {
                            if (methodBean.getArgTypeNames()[i].equals(cls.getName())) {
                                isExcludeArg = true;
                                break;
                            }
                        }
                    }                   
                    if (!isExcludeArg) {
                        argMap.put(argNames[i], methodBean.getArgs()[i]);
                    }
                }
            }
        }
        
        OperationLogInfo operationLogInfo = null;
        if (this.operationLogService != null) {
            operationLogInfo = new OperationLogInfo();
            if (loginEmployee != null) {
                operationLogInfo.setUserId(loginEmployee.getUserId());
            }           
            operationLogInfo.setClientIp(WebUtils.getIpAddress());
            operationLogInfo.setMethodName(methodBean.getMethodName());
            if (MapUtils.isNotEmpty(argMap)) {
                operationLogInfo.setParam(JSON.toJSONStringWithDateFormat(argMap, dateFormat));
            }
        }
                
        try {
            if (isNeedAuth) {
                if (loginEmployee == null || StringUtils.isEmpty(loginEmployee.getSessionId())) {
                    throw new NotLoggedInException("notLogin", "没有登录，或登录状态过期！");
                }
                if (this.isCheckAuth
                        && (CollectionUtils.isEmpty(this.authExcludeList) || !this.authExcludeList
                                .contains(methodBean.getMethodName()))) {
                    this.authorityService.checkControllerAuthority(authKey, methodBean.getMethodName(), loginEmployee);
                }
            }
            //定制分发
            CustomizeScanner customizeScanner = CustomizeScanner.getInstance();
            AtomicBoolean success = new AtomicBoolean(false);
           //log.info("调用定制分发接口customizeScanner.invokeControllerCustomized入参args:{}", JSON.toJSONString(methodBean.args));
            Object result = customizeScanner.invokeControllerCustomized(
                    methodBean.methodName, methodBean.argTypeNames, methodBean.args, success);

            if (!success.get()){
                result = controller.proceed();
            }
            //
            if (operationLogInfo != null) {
                operationLogInfo.setSuccess(true);
                if (result != null) {
                    operationLogInfo.setResult(JSON.toJSONStringWithDateFormat(result, dateFormat));
                }               
                operationLogInfo.setTime(new Date());
                this.operationLogService.writingLog(operationLogInfo);
            }
            return result;
        } catch (Exception e) {
            if (operationLogInfo != null) {
                operationLogInfo.setSuccess(false);
                if (e instanceof GiantsException) {
                    operationLogInfo.setResult(JSON.toJSONString(e));
                } else {
                    operationLogInfo.setResult(e.toString());
                }
                operationLogInfo.setTime(new Date());
                this.operationLogService.writingLog(operationLogInfo);
            }
//            if (e instanceof BusinessException) {
//                throw e;
//            }
//            throw e.getCause();
            throw e;
        }
        
    }
    
    private MethodBean buildMethodBean(ProceedingJoinPoint controller){
        String[] argTypeNames = null;
        String[] methodSignatures = controller.getSignature().toLongString().split(" ");
        String methodName = null;
        String[] parses = null;
        if (methodSignatures.length == 4) {
            parses = methodSignatures[3].split("\\(");
        } else {
            parses = methodSignatures[2].split("\\(");
        }
        methodName = parses[0];
        String arguments = parses[1].replace(")", "");
        if (StringUtils.isNotEmpty(arguments)) {
            argTypeNames = arguments.split("\\,");
        }
        
        Object[] args = controller.getArgs();
        return new MethodBean(methodName, argTypeNames, args);
    }

    public void setAuthExcludeList(List<String> authExcludeList) {
        this.authExcludeList = authExcludeList;
    }

    public void setArgClassExcludeList(List<Class<?>> argClassExcludeList) {
        this.argClassExcludeList = argClassExcludeList;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setOperationLogService(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    public void setCheckAuth(boolean isCheckAuth) {
        this.isCheckAuth = isCheckAuth;
    }
    
    private class MethodBean {

        private String methodName;
        private String[] argTypeNames;
        private Object[] args;

        public MethodBean(String methodName, String[] argTypeNames, Object[] args) {
            super();
            this.methodName = methodName;
            this.argTypeNames = argTypeNames;
            this.args = args;
        }

        public String getMethodName() {
            return methodName;
        }

        public String[] getArgTypeNames() {
            return argTypeNames;
        }

        public Object[] getArgs() {
            return args;
        }

    }

}
