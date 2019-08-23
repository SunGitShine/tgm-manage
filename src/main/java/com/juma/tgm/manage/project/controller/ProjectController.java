package com.juma.tgm.manage.project.controller;

import com.alibaba.fastjson.JSON;
import com.giants.common.tools.Page;
import com.juma.auth.employee.domain.DepartmentCompany;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.auth.user.domain.LoginUser;
import com.juma.auth.user.domain.User;
import com.juma.conf.domain.ConfParamOption;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.FreightEnum;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.configure.domain.FreightFactor;
import com.juma.tgm.configure.service.FreightFactorService;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.manage.project.controller.dto.ProjectDepotDto;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.operateLog.enumeration.LogSignEnum;
import com.juma.tgm.operateLog.enumeration.OperateTypeEnum;
import com.juma.tgm.project.domain.Project;
import com.juma.tgm.project.domain.RoadMapPriceRule;
import com.juma.tgm.project.domain.ValuationWay;
import com.juma.tgm.project.domain.v2.ProjectDepot;
import com.juma.tgm.project.domain.v2.enums.ProjectEnum.ProjectType;
import com.juma.tgm.project.service.ProjectMemberService;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.project.service.RoadMapPriceRuleService;
import com.juma.tgm.project.vo.ContractVo;
import com.juma.tgm.project.vo.ProjectFilter;
import com.juma.tgm.project.vo.ProjectFreightRuleVo;
import com.juma.tgm.project.vo.RoadMapPriceRuleVo;
import com.juma.tgm.project.vo.v2.ProjectMemberVo;
import com.juma.tgm.project.vo.v2.ProjectVo;
import com.juma.tgm.tools.service.AuthCommonService;
import com.juma.tgm.tools.service.BusinessAreaCommonService;
import com.juma.tgm.tools.service.CrmCommonService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName ProjectController.java
 * @Description 项目管理
 * @Date 2017年9月28日 下午2:11:58
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */
@Controller
@RequestMapping("project")
public class ProjectController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(ProjectController.class);
    @Resource
    private ProjectService projectService;
    @Resource
    private CustomerInfoService customerInfoService;
    @Resource
    private EmployeeService employeeService;
    @Resource
    private BusinessAreaCommonService businessAreaCommonService;
    @Resource
    private FreightFactorService freightFactorService;
    @Resource
    private RoadMapPriceRuleService roadMapPriceRuleService;
    @Resource
    private AuthCommonService authCommonService;
    @Resource
    private ProjectMemberService projectMemberService;
    @Resource
    private CrmCommonService crmCommonService;

    /**
     * 分页
     */
    @ApiOperation(value = "项目分页列表", notes = "项目分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectFilter.areaCodeList", value = "业务范围list集合"),
            @ApiImplicitParam(name = "projectFilter.projectNo", value = "项目编号", dataType = "String"),
            @ApiImplicitParam(name = "projectFilter.customerId", value = "客户ID", dataType = "Integer"),
            @ApiImplicitParam(name = "projectFilter.projectId", value = "项目ID", dataType = "Integer"),
            @ApiImplicitParam(name = "projectFilter.managerId", value = "客户经理ID", dataType = "Integer"),
            @ApiImplicitParam(name = "projectFilter.projectStatusList", value = "项目状态List集合"),
            @ApiImplicitParam(name = "projectFilter.isRunning", value = "开跑状态", dataType = "boolean"),
            @ApiImplicitParam(name = "projectFilter.projectType", value = "项目类型", dataType = "Integer"),
    })
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ProjectVo> search(@RequestBody QueryCond<ProjectFilter> queryCond, LoginEmployee loginEmployee) {
        return projectService.searchBackSysV2(queryCond, loginEmployee);
    }

    @ApiOperation(value = "项目核心信息与基础信息详情")
    @ApiImplicitParam(paramType = "path", name = "projectId", value = "项目ID")
    @ResponseBody
    @RequestMapping(value = "base/{projectId}/detail", method = RequestMethod.GET)
    public ProjectVo baseProjectDetail(@PathVariable Integer projectId, LoginEmployee loginEmployee) {
        return projectService.getProjectVo(projectId, loginEmployee);
    }

    @ApiOperation(value = "转正式运行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", dataType = "Integer"),
            @ApiImplicitParam(name = "contractNo", value = "合同编号", dataType = "String"),
            @ApiImplicitParam(name = "projectEndDate", value = "项目截止时间（合同截止时间）", dataType = "Date"),
            @ApiImplicitParam(name = "payToCompany", value = "运营主体", dataType = "Integer"),
            @ApiImplicitParam(name = "contractToCompany", value = "签约主体", dataType = "Integer"),
    })
    @ResponseBody
    @RequestMapping(value = "toRealRun", method = RequestMethod.POST)
    public void projectToRealRun(@RequestBody com.juma.tgm.project.domain.v2.Project project,
                                 LoginEmployee loginEmployee) {
        project = this.buildCreditCode(project, loginEmployee);
        projectService.projectToRealRun(project.getProjectId(), project.getContractNo(), project.getPayToCompany(),
                project.getPayToCompanyCreditCode(), project.getContractToCompany(),
                project.getContractToCompanyCreditCode(),
                project.getProjectStartDate(), project.getProjectEndDate(), loginEmployee);
        super.insertLog(OperateTypeEnum.PROJECT_TO_REAL_RUN, project.getProjectId(),
                "合同编号：" + project.getContractNo(), loginEmployee);
    }

    @ApiOperation(value = "项目变更合同")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", dataType = "Integer"),
            @ApiImplicitParam(name = "contractNo", value = "合同编号", dataType = "String"),
            @ApiImplicitParam(name = "projectStartDate", value = "项目开始时间（合同开始时间）", dataType = "Date"),
            @ApiImplicitParam(name = "projectEndDate", value = "项目截止时间（合同截止时间）", dataType = "Date"),
            @ApiImplicitParam(name = "payToCompany", value = "运营主体", dataType = "Integer"),
            @ApiImplicitParam(name = "contractToCompany", value = "签约主体", dataType = "Integer"),
    })
    @ResponseBody
    @RequestMapping(value = "changeContract", method = RequestMethod.POST)
    public void projectChangeContract(@RequestBody com.juma.tgm.project.domain.v2.Project project,
                                      @ApiParam(hidden = true) LoginEmployee loginEmployee) {
        project = this.buildCreditCode(project, loginEmployee);
        projectService.projectChangeContract(project.getProjectId(), project.getContractNo(), project.getPayToCompany(),
                project.getPayToCompanyCreditCode(), project.getContractToCompany(),
                project.getContractToCompanyCreditCode(),
                project.getProjectStartDate(), project.getProjectEndDate(), loginEmployee);
        super.insertLog(OperateTypeEnum.PROJECT_CHANGE_CONTRACT, project.getProjectId(),
                "合同编号：" + project.getContractNo(), loginEmployee);
    }

    @ApiOperation(value = "项目续签合同")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", dataType = "Integer"),
            @ApiImplicitParam(name = "contractNo", value = "合同编号", dataType = "String"),
            @ApiImplicitParam(name = "projectStartDate", value = "项目开始时间（合同开始时间）", dataType = "Date"),
            @ApiImplicitParam(name = "projectEndDate", value = "项目截止时间（合同截止时间）", dataType = "Date"),
            @ApiImplicitParam(name = "payToCompany", value = "运营主体", dataType = "Integer"),
            @ApiImplicitParam(name = "contractToCompany", value = "签约主体", dataType = "Integer"),
    })
    @ResponseBody
    @RequestMapping(value = "renewalContract", method = RequestMethod.POST)
    public void projectRenewalContract(@RequestBody com.juma.tgm.project.domain.v2.Project project,
                                       @ApiParam(hidden = true) LoginEmployee loginEmployee) {
        project = this.buildCreditCode(project, loginEmployee);
        projectService
                .projectRenewalContract(project.getProjectId(), project.getContractNo(), project.getPayToCompany(),
                        project.getPayToCompanyCreditCode(), project.getContractToCompany(),
                        project.getContractToCompanyCreditCode(),
                        project.getProjectStartDate(), project.getProjectEndDate(), loginEmployee);
        super.insertLog(OperateTypeEnum.PROJECT_RENEWAL_CONTRACT, project.getProjectId(),
                "合同编号：" + project.getContractNo(), loginEmployee);
    }

    @ApiOperation(value = "删除项目，逻辑删除")
    @ApiImplicitParam(paramType = "path", name = "projectId", value = "项目ID")
    @ResponseBody
    @RequestMapping(value = "{projectId}/delete", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer projectId, LoginEmployee loginEmployee) {
        projectService.del(projectId, loginEmployee);
        super.insertLog(OperateTypeEnum.MODIFY_PROJECT, projectId, null, loginEmployee);
    }

    /**
     * 计价维度
     */
    @ResponseBody
    @RequestMapping(value = "list/freightFactor", method = RequestMethod.GET)
    public List<FreightFactor> freightFactor(LoginEmployee loginEmployee) {
        return freightFactorService.findByFreightWay(FreightEnum.PROJECT.getCode(), loginEmployee);
    }

    /**
     * 查看项目详情
     */
    @ResponseBody
    @RequestMapping(value = "rest/{projectId}/viewDetail", method = RequestMethod.GET)
    public Project getProjectViewDetail(@PathVariable("projectId") Integer projectId, LoginEmployee loginEmployee) {
        Project project = projectService.getProject(projectId);
        if (null == project) {
            return null;
        }

        CustomerInfo customerInfo = customerInfoService.findCusInfoById(project.getCustomerId());
        if (null != customerInfo) {
            project.setCustomerName(customerInfo.getCustomerName());
        }

        User user = employeeService.loadUserByEmployeeId(project.getManagerId(), loginEmployee);
        if (null != user) {
            project.setCustomerManagerName(user.getName());
        }

        project.setAreaName(businessAreaCommonService.loadLogicAndSelfAreaName(project.getAreaCode(), loginEmployee));

        return project;
    }

    @ApiOperation(value = "对账权限")
    @ResponseBody
    @RequestMapping(value = "{projectId}/{isReceivableFirst}/isReceivableFirst", method = RequestMethod.GET)
    public void isReceivableFirst(@PathVariable Integer projectId, @PathVariable Boolean isReceivableFirst,
                                  LoginEmployee loginEmployee) {
        projectService.updateIsReceivableFirst(projectId, isReceivableFirst, loginEmployee);
        super.insertLog(OperateTypeEnum.PROJECT_IS_RECEIVABLE_FIRST, projectId, null, loginEmployee);
    }

    @ApiOperation(value = "根据项目名称模糊查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "项目名称，可不传", dataType = "String"),
            @ApiImplicitParam(name = "isEnable", value = "项目是否启用，true: 启用， false：停用", dataType = "Boolean"),
            @ApiImplicitParam(name = "backPageSize", value = "返回数据数量,默认15条，最大200条，非必须",
                    dataType = "Integer")})
    @ResponseBody
    @RequestMapping(value = "list/project", method = RequestMethod.POST)
    public List<com.juma.tgm.project.domain.v2.Project> listProject(@RequestBody ProjectFilter projectFilter,
                                                                    LoginEmployee loginEmployee) {
        return projectService.listProjectBy(projectFilter.getName(), projectFilter.getCustomerId(), projectFilter.getBackPageSize(),
                projectFilter.getIsEnable(), loginEmployee);
    }

    @ApiOperation(value = "根据项目名称模糊查询,下单使用")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "项目名称，可不传", dataType = "String"),
            @ApiImplicitParam(name = "backPageSize", value = "返回数据数量,默认15条，最大200条，非必须",
                    dataType = "Integer")})
    @ResponseBody
    @RequestMapping(value = "list/project/byLoginUser", method = RequestMethod.POST)
    public List<ProjectVo> listProjectByLoginUser(@RequestBody ProjectFilter projectFilter,
                                                  LoginEmployee loginEmployee) {
        return projectService.listProjectByLoinUser(projectFilter.getName(), projectFilter.getBackPageSize(),
                loginEmployee);
    }

    @Deprecated
    @ResponseBody
    @RequestMapping(value = "list/projectByCustomerId", method = RequestMethod.POST)
    public List<Project> listProjectByCustomerId(@RequestBody Project project) {
        return projectService.listProjectByCustomerId(project, 50);
    }

    /**
     * 获取项目的价格规则
     */
    @ResponseBody
    @RequestMapping(value = "freight/rule", method = RequestMethod.POST)
    public ProjectFreightRuleVo loadFreightRule(@RequestBody RoadMapPriceRuleVo roadMapPriceRuleVo,
                                                LoginEmployee loginEmployee) {
        ProjectFreightRuleVo result = new ProjectFreightRuleVo();
        String factorJson = null;

        // 运单中没有价格规则或不是再次用车通过线路ID和车型ID获取
        if (StringUtils.isBlank(factorJson) && null != roadMapPriceRuleVo.getRoadMapId()
                && null != roadMapPriceRuleVo.getTruckTypeId()) {
            RoadMapPriceRule roadMapPriceRule = roadMapPriceRuleService
                    .findByRoadMapIdAndTypeId(roadMapPriceRuleVo.getRoadMapId(), roadMapPriceRuleVo.getTruckTypeId());
            if (null == roadMapPriceRule) {
                return result;
            }
            // result.setProjectFreightRuleId(projectFreightRule.getProjectFreightRuleId());
            result.setRoadMapId(roadMapPriceRule.getRoadMapId());
            result.setTruckTypeId(roadMapPriceRule.getTruckTypeId());
            factorJson = roadMapPriceRule.getValuationModelJson();
        }

        List<ValuationWay> valuationWays = new ArrayList<ValuationWay>();
        if (StringUtils.isBlank(factorJson)) {
            return null;
        }

        Map<String, Object> map = JSON.parseObject(factorJson, Map.class);

        // 计费方式
        List<FreightFactor> freightFactors = freightFactorService.findByFreightWay(FreightEnum.PROJECT.getCode(),
                loginEmployee);
        if (freightFactors.isEmpty()) {
            return null;
        }

        for (FreightFactor freightFactor : freightFactors) {
            if (null == map.get(freightFactor.getLabelInputName())) {
                continue;
            }
            ValuationWay vo = new ValuationWay();
            vo.setLabelInputName(freightFactor.getLabelInputName());
            vo.setLabelName(freightFactor.getLabelName());
            vo.setValue(map.get(freightFactor.getLabelInputName()).toString());
            if (StringUtils.isNotBlank(freightFactor.getLabelInputName())
                    && freightFactor.getLabelInputName().equals("initiateRate")) {
                valuationWays.add(0, vo);
            } else {
                valuationWays.add(vo);
            }
        }
        result.setValuationWays(valuationWays);

        return result;
    }

    /**
     * 核心信息添加或修改
     */
    @ApiOperation(value = "新建项目-核心信息", notes = "核心信息添加或修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectType", value = "项目类型 1:试运行 2:正式运行", dataType = "Integer"),
            @ApiImplicitParam(name = "customerId", value = "客户名称id", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "项目名称", dataType = "String"),
            @ApiImplicitParam(name = "projectManagerUserId", value = "项目经理id", dataType = "Integer"),
            @ApiImplicitParam(name = "projectStartDate", value = "试运行开始时间", dataType = "Date"),
            @ApiImplicitParam(name = "projectEndDate", value = "试运行截止时间", dataType = "Date"),
            @ApiImplicitParam(name = "areaCode", value = "业务区域（commons/load/{areaCode}/logicAndSelfAreaName.html）", dataType = "Date"),
            @ApiImplicitParam(name = "contractToCompany ", value = "签约方", dataType = "String"),
            @ApiImplicitParam(name = "payToCompany", value = "运作方", dataType = "String"),
            @ApiImplicitParam(name = "tryWorkPassAttachment", value = "已审批项目评估表", dataType = "String"),
            @ApiImplicitParam(name = "tryWorkProtocol", value = "试运行协议", dataType = "String"),
            @ApiImplicitParam(name = "contractNo", value = "合同编号", dataType = "String"),
            @ApiImplicitParam(name = "签约方、运作方、项目开始时间、项目截止时间、合同编号、合同附件", value = "后面由crm提供", dataType = "String"),
    })
    @ResponseBody
    @RequestMapping(value = "core/update", method = RequestMethod.POST)
    public Integer coreInfoUpdate(@RequestBody com.juma.tgm.project.domain.v2.Project project, LoginEmployee loginEmployee) {
        project = this.buildCreditCode(project, loginEmployee);
        if (null == project.getProjectId()) {
            Integer projectId = projectService.coreInfoAdd(project, loginEmployee);
            super.insertLog(OperateTypeEnum.ADD_PROJECT, projectId, null, loginEmployee);
            return projectId;
        }
        projectService.coreInfoUpdate(project, loginEmployee);
        super.insertLog(OperateTypeEnum.MODIFY_PROJECT, project.getProjectId(), null, loginEmployee);
        return project.getProjectId();
    }

    /**
     * 基本信息修改
     */
    @ApiOperation(value = "新建项目-基本信息", notes = "基本信息修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", dataType = "Integer"),
            @ApiImplicitParam(name = "goodsType", value = "货物类型", dataType = "String"),
            @ApiImplicitParam(name = "estimateTimeConsumption", value = "预估配送时间", dataType = "String"),
            @ApiImplicitParam(name = "taxRateValue", value = "税率", dataType = "String"),
            @ApiImplicitParam(name = "deliveryAddressType", value = "配送地类型", dataType = "Integer"),
            @ApiImplicitParam(name = "logisticsLabel", value = "物流产品", dataType = "String"),
            @ApiImplicitParam(name = "additionalFunctionIds", value = "用车要求", dataType = "Array"),
            @ApiImplicitParam(name = "businessLinkman", value = "业务对接人", dataType = "String"),
            @ApiImplicitParam(name = "businessLinktel", value = "业务对接人电话", dataType = "String"),
            @ApiImplicitParam(name = "businessLinkemail", value = "业务对接人邮箱", dataType = "String"),
            @ApiImplicitParam(name = "financeLinkman", value = "财务对接人", dataType = "String"),
            @ApiImplicitParam(name = "financeLinktel", value = "财务对接人电话", dataType = "String"),
            @ApiImplicitParam(name = "financeLinkemail", value = "财务对接人邮箱", dataType = "String"),
            @ApiImplicitParam(name = "isReceiveDailySms", value = "是否发送短信日报 0否、1是", dataType = "Integer"),
    })
    @ResponseBody
    @RequestMapping(value = "basic/update", method = RequestMethod.POST)
    public Integer basicInfoUpdate(@RequestBody com.juma.tgm.project.domain.v2.Project project, LoginEmployee loginEmployee) {
        projectService.basicInfoUpdate(project, loginEmployee);
        super.insertLog(OperateTypeEnum.MODIFY_PROJECT, project.getProjectId(), null, loginEmployee);
        return project.getProjectId();
    }

    /**
     * 修改毛利率
     */
    @ApiOperation(value = "修改毛利率", notes = "修改毛利率")
    @ResponseBody
    @RequestMapping(value = "update/profitRate", method = RequestMethod.POST)
    public Integer updateProfitRate(@RequestBody com.juma.tgm.project.domain.v2.Project project, LoginEmployee loginEmployee) {
        com.juma.tgm.project.domain.v2.Project oldProject = projectService.getProjectV2(project.getProjectId());
        BigDecimal oldProfitRate = null;
        if(null != oldProject){
            oldProfitRate = oldProject.getProfitRate();
        }
        projectService.updateProfitRate(project);
        super.insertLog(LogSignEnum.PROFIT_RATE,OperateTypeEnum.MODIFY_PROFIT_RATE, project.getProjectId(), "承诺毛利率："+oldProfitRate+"->"+project.getProfitRate(), loginEmployee);
        return project.getProjectId();
    }

    /**
     * 运营专员添加或修改
     */
    @ApiOperation(value = "新建项目-运营专员", notes = "运营专员添加或修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", dataType = "Integer"),
            @ApiImplicitParam(name = "userId", value = "运营专员id", dataType = "Integer"),
            @ApiImplicitParam(name = "isStayWarehosue", value = "是否驻仓 0否、1是", dataType = "Integer"),
    })
    @ResponseBody
    @RequestMapping(value = "operate/update", method = RequestMethod.POST)
    public void operateAddOrUpdate(@RequestBody ProjectMemberVo projectMember, LoginEmployee loginEmployee) {
        projectMemberService.operateAddOrUpdate(projectMember, loginEmployee);
        super.insertLog(OperateTypeEnum.ADD_OR_MOFIFY_PROJECT_MEMBER, projectMember.getProjectId(), JSON.toJSONString(projectMember), loginEmployee);
    }

    @ApiOperation(value = "货物类型列表")
    @ResponseBody
    @RequestMapping(value = "goods/list", method = RequestMethod.GET)
    public List<ConfParamOption> goodsList() {
        return authCommonService.listOption(Constants.TMS_GOODS_TYPE);
    }

    @ApiOperation(value = "配送地类型列表")
    @ResponseBody
    @RequestMapping(value = "deliveryAddress/list", method = RequestMethod.GET)
    public List<ConfParamOption> deliveryAddressList() {
        return authCommonService.listOption(Constants.TMS_DELIVERY_ADDRESS_TYPE);
    }

    @ApiOperation(value = "运营专员详情")
    @ResponseBody
    @RequestMapping(value = "member/{projectId}/detail", method = RequestMethod.GET)
    public ProjectMemberVo projectMemberDetail(@PathVariable Integer projectId) {
        return projectMemberService.getProjectMemer(projectId);
    }

    @ApiOperation(value = "项目信息补录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", dataType = "Integer"),
            @ApiImplicitParam(name = "contractNo", value = "合同编号", dataType = "String"),
            @ApiImplicitParam(name = "projectEndDate", value = "项目截止时间（合同截止时间）", dataType = "Date"),
            @ApiImplicitParam(name = "payToCompany", value = "运营主体", dataType = "Integer"),
            @ApiImplicitParam(name = "contractToCompany", value = "签约主体", dataType = "Integer"),
            @ApiImplicitParam(name = "tryWorkPassAttachment", value = "项目评估表", dataType = "Integer")
    })
    @ResponseBody
    @RequestMapping(value = "supplement", method = RequestMethod.POST)
    public void projectSupplement(@RequestBody com.juma.tgm.project.domain.v2.Project project,
                                  LoginEmployee loginEmployee) {
        project = this.buildCreditCode(project, loginEmployee);
        projectService.projectSupplement(project.getProjectId(), project.getContractNo(), project.getPayToCompany(),
                project.getPayToCompanyCreditCode(), project.getContractToCompany(),
                project.getContractToCompanyCreditCode(),
                project.getProjectStartDate(), project.getProjectEndDate(), project.getTryWorkPassAttachment(),
                loginEmployee);
        super.insertLog(OperateTypeEnum.PROJECT_SUPPLEMENT, project.getProjectId(),
                "合同编号：" + project.getContractNo(), loginEmployee);
    }


    // 补全社会统一信用代码
    private com.juma.tgm.project.domain.v2.Project buildCreditCode(com.juma.tgm.project.domain.v2.Project project,
                                                                   LoginUser loginUser) {
        if (StringUtils.isBlank(project.getContractNo())) {
            // 试运行
            if (null != project.getProjectType() && project.getProjectType().equals(ProjectType.TEST_RUN.getCode())) {
                if (null != project.getPayToCompany() && StringUtils.isBlank(project.getPayToCompanyCreditCode())) {
                    DepartmentCompany company = authCommonService.findDepartmentCompanyByDepartmentId(project.getPayToCompany());
                    project.setPayToCompanyCreditCode(company == null ? "" : company.getUniformSocialCreditCode());
                    project.setContractToCompanyCreditCode(project.getPayToCompanyCreditCode());
                }
            }
            return project;
        }

        if (StringUtils.isNotBlank(project.getContractToCompanyCreditCode()) && StringUtils
                .isNotBlank(project.getPayToCompanyCreditCode())) {
            return project;
        }

        ContractVo contractVo = crmCommonService.loadByContractNo(project.getContractNo(), loginUser);
        project.setContractToCompanyCreditCode(contractVo == null ? "" : contractVo.getContractToCompanyCreditCode());
        project.setPayToCompanyCreditCode(contractVo == null ? "" : contractVo.getPayToCompanyCreditCode());
        return project;
    }

    @ApiOperation(value = "添加仓库")
    @ResponseBody
    @RequestMapping(value = "depot/add", method = RequestMethod.POST)
    public void addProjectDepots(@RequestBody List<ProjectDepotDto> projectDepotDtos, LoginEmployee loginEmployee) {
        List<ProjectDepot> projectDepots = new ArrayList<>();
        if (projectDepotDtos != null && !projectDepotDtos.isEmpty()) {
            for ( ProjectDepotDto projectDepotDto : projectDepotDtos ) {
                ProjectDepot projectDepot = new ProjectDepot();
                projectDepot.setProjectId(projectDepotDto.getProjectId());
                projectDepot.setDepotName(projectDepotDto.getDepotName());
                projectDepot.setDepotAddress(projectDepotDto.getDepotAddress());
                projectDepot.setDepotCoordinates(projectDepotDto.getDepotCoordinates());
                projectDepot.setLinkMan(projectDepotDto.getLinkMan());
                projectDepot.setLinkManPhone(projectDepotDto.getLinkManPhone());
                projectDepots.add(projectDepot);
            }
        }
        projectService.addProjectDepots(projectDepots,loginEmployee);
    }

    @ApiOperation(value = "删除仓库")
    @ResponseBody
    @RequestMapping(value = "depot/{depotId}", method = RequestMethod.DELETE)
    public void deleteProjectDepot(@PathVariable Integer depotId,LoginEmployee loginEmployee) {
        projectService.deleteProjectDepot(depotId,loginEmployee);
    }

    @ApiOperation(value = "项目仓库列表")
    @ResponseBody
    @RequestMapping(value = "{projectId}/depot", method = RequestMethod.GET)
    public List<ProjectDepot> listProjectDepot(@PathVariable Integer projectId,LoginEmployee loginEmployee) {
        return projectService.listProjectDepot(projectId);
    }
}
