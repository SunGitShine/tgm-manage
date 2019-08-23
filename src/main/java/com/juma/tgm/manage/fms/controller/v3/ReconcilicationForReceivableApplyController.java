package com.juma.tgm.manage.fms.controller.v3;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.giants.common.tools.PageQueryCondition;
import com.google.common.collect.Lists;
import com.juma.auth.employee.domain.Department;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.DepartmentService;
import com.juma.tgm.fms.domain.Task;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationQueryVo;
import com.juma.tgm.fms.domain.v3.AdjustForReceivable;
import com.juma.tgm.fms.domain.v3.ReconcilicationForCompany;
import com.juma.tgm.fms.domain.v3.ReconcilicationForReceivable;
import com.juma.tgm.fms.domain.v3.enums.ReconcilicationForReceivableEnum;
import com.juma.tgm.fms.domain.v3.vo.*;
import com.juma.tgm.fms.service.v3.ReconcilicationForReceivableService;
import com.juma.tgm.imageUploadManage.domain.FileUploadParameter;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.imageUploadManage.service.ImageUploadManageService;
import com.juma.tgm.manage.fms.controller.v2.vo.excelVo.ReconciliationOverViewExcelVo;
import com.juma.tgm.manage.fms.controller.vo.ReconcilicationForCompanyVo;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.project.domain.v2.Project;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.project.vo.ContractVo;
import com.juma.tgm.tools.service.CrmCommonService;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.TruckRequireService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.about.poi.writer.XssfWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="ReconcilicationForReceivableApplyController",tags={"ReconcilicationForReceivableApplyController"})
@Controller
@RequestMapping("reconcilicationForReceivable/apply/v3")
public class ReconcilicationForReceivableApplyController extends BaseController{

	private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	@Resource
	private ReconcilicationForReceivableService receivableService;

	@Resource
	private TruckRequireService truckRequireService;

	@Autowired
	private ImportUpdateFreightCheckForReceivable importUpdateFreightCheckForReceivable;

	@Resource
	private ImageUploadManageService imageUploadManageService;

	@Resource
	private DepartmentService departmentService;
	@Resource
	private CrmCommonService crmCommonService;
	@Resource
	private ProjectService projectService;

	/**
	 * 对账单列表queryVo
	 */
	public static class ReconciliationManageQueryVo extends PageQueryCondition<ReconciliationQueryVo> {

		public ReconciliationManageQueryVo() {
		}
	}

	/**
	 * 分页查询客户对账申请列表
	 * 条件：
	 * 	选传：areaCodeList、customerName、projectName
	 * @param pageCondition
	 * @param loginEmployee
	 * @return
	 */
	@ApiOperation(value="分页查询客户对账申请列表")
	@ResponseBody
	@RequestMapping(value = "/findApplyPage",method = RequestMethod.GET)
	public Page<ReceivableApplyVo> findApplyPage(PageCondition pageCondition, LoginEmployee loginEmployee){

		super.formatAreaCodeToList(pageCondition, false);
		super.filtersIsMapThenRemoveVulueIsNull(pageCondition);
		return receivableService.findReceivableApplyPage(pageCondition, loginEmployee);
	}

	/**
	 * 查询对账运单列表
	 * 条件：
	 * 	必传：customerId、projectId、
	 * 	选传：waybillNo、plateNumber、driverName、startTime、endTime、roadMapName
	 * @param pageCondition
	 * @param loginEmployee
	 * @return
	 */
	@ApiOperation(value="分页查询对账运单列表",notes = "customerId、projectId必传")
	@ResponseBody
	@RequestMapping(value = "/searchWaybills",method = RequestMethod.GET)
	public Page<ReconciliationWaybillDetailVo> searchWaybills(PageCondition pageCondition, LoginEmployee loginEmployee){

		return receivableService.searchWaybills(loginEmployee, pageCondition);
	}

	/**
	 * 改价
	 * @param adjustForReceivable
	 * @param loginEmployee
	 */
	@ApiOperation(value="改价",notes = "只传waybillId、receivableWithTaxAdjust、taxRateAdjust、adjustRemark")
	@ResponseBody
	@RequestMapping(value = "/updateFreight",method = RequestMethod.POST)
	public void updateFreight(@RequestBody AdjustForReceivable adjustForReceivable, LoginEmployee loginEmployee){

		if(adjustForReceivable.getAdjustRemark().length() > 255){
			throw new BusinessException("error.adjustRemark","改价备注不能超过255个字");
		}
		receivableService.updateFreight(adjustForReceivable, loginEmployee);
	}

	@ApiOperation(value="查询初始含税金额及税率")
	@ResponseBody
	@RequestMapping(value = "/findInitialValue",method = RequestMethod.GET)
	public AdjustForReceivable findInitialValue(@ApiParam(name="waybillId",value="运单id",required=true) Integer waybillId,
		LoginEmployee loginEmployee){

		return receivableService.findOldWaybillDate(waybillId, loginEmployee);
	}

	@ApiOperation(value="分页查询改价记录",notes = "waybillId必传")
	@ResponseBody
	@RequestMapping(value = "/findAdjustByPage",method = RequestMethod.GET)
	public Page<AdjustForReceivable> findAdjustByPage(PageCondition pageCondition, LoginEmployee loginEmployee){

		return receivableService.findAdjustByPage(pageCondition);
	}

	@ApiOperation(value="创建对账单",notes = "waybillIds必传、对账单基础信息必传（areaCode、customerId、customerName、projectId、projectName）")
	@ResponseBody
	@RequestMapping(value = "/createReceivableReconciliation",method = RequestMethod.POST)
	public String createReceivableReconciliation(@RequestBody ArrayList<Integer> waybillIds,
		LoginEmployee loginEmployee){

		return receivableService.createReceivableReconciliation(waybillIds, loginEmployee);
	}

	@ApiOperation(value="生成对账单",notes = "waybillIds必传")
	@RequestMapping( value="/freight/export-update-model" , method = RequestMethod.POST)
	public void exportUpdateModel(int[] waybillIds, LoginEmployee loginEmployee, HttpServletResponse httpServletResponse)
		throws BusinessException {
		List<Integer> waybillIdList = Lists.newArrayList();
		for (int waybillId : waybillIds) { waybillIdList.add(waybillId); }
		List<Waybill> waybills = receivableService.getWaybillList(waybillIdList);
		List<ReconciliationWaybillExcelVo> waybillReconciliations = new ArrayList<>();
		for( Waybill waybill : waybills ) {
			ReconciliationWaybillExcelVo reconciliationWaybillExcelVo = new ReconciliationWaybillExcelVo();
			BeanUtils.copyProperties(waybill, reconciliationWaybillExcelVo);
			TruckRequire truckRequire = truckRequireService.findTruckRequireByWaybillId( waybill.getWaybillId(), loginEmployee);
			reconciliationWaybillExcelVo.setTaxRateValue(truckRequire.getTaxRateValue());
			waybillReconciliations.add( reconciliationWaybillExcelVo );
		}
		try {
			httpServletResponse.setContentType(CONTENT_TYPE);
			httpServletResponse.setHeader("Content-disposition", "attachment; filename=update-model.xlsx");
			new XssfWriter().appendToSheet("运单改价模板" , waybillReconciliations).writeToOutputStream(httpServletResponse.getOutputStream());
		} catch (Exception e) {
			throw new BusinessException("export error " , "import.xlsx.export.error" );
		}
	}

	@ApiOperation(value="批量改价")
	@ResponseBody
	@RequestMapping(value = "/freight/import-update", method = RequestMethod.POST)
	public void batchUpdateFreight(@RequestParam(required = false) MultipartFile uploadPic, LoginEmployee loginEmployee) throws BusinessException{
		List<ReconciliationWaybillExcelVo> waybillReconciliations = importUpdateFreightCheckForReceivable.checkImportFileAndFrom( uploadPic, loginEmployee );
		receivableService.batchUpdateFreight( waybillReconciliations, loginEmployee );
	}

	@ApiOperation(value = "分页查询应收对账单")
	@ResponseBody
	@RequestMapping(value = "/findReceivablePage", method = RequestMethod.GET)
	public Page<ReconcilicationForReceivablePageVo> findReceivablePage(PageCondition pageCondition,
		LoginEmployee loginEmployee){

		super.formatAreaCodeToList(pageCondition, false);
		return receivableService.findReceivableReconciliationPage(pageCondition, loginEmployee);
	}

	@ApiOperation(value = "分页查询应收对账单明细", notes = "reconcilicationId必传")
	@ResponseBody
	@RequestMapping(value = "/findReceivableItemPage", method = RequestMethod.GET)
	public Page<ReconcilicationForReceivableItemVo> findReceivableItemPage(PageCondition pageCondition,
		LoginEmployee loginEmployee){

		return receivableService.findReceivableReconciliationItemPage(pageCondition);
	}

	@ApiOperation(value = "对账单提交审核")
	@ResponseBody
	@RequestMapping(value = "/submitApply", method = RequestMethod.POST)
	public Map<String, String> submitApply(@RequestBody List<Integer> reconcilicationForReceivableIds, LoginEmployee loginEmployee){

		if(reconcilicationForReceivableIds == null || reconcilicationForReceivableIds.isEmpty())
			return null;

		List<Integer> incorrectIds = new ArrayList<>();
		StringBuilder noEvidenceErrorNos = new StringBuilder("");
		StringBuilder statusErrorNos = new StringBuilder("");

		this.filterErrorData(reconcilicationForReceivableIds, incorrectIds, noEvidenceErrorNos, statusErrorNos);
		reconcilicationForReceivableIds.removeAll(incorrectIds);
		if (CollectionUtils.isNotEmpty(reconcilicationForReceivableIds)) {
			receivableService.submitToWorkFlow(reconcilicationForReceivableIds, loginEmployee);
		}

		Map<String, String> rst = new HashMap<>();
		if (CollectionUtils.isEmpty(incorrectIds)) {
			rst.put("hasError", "false");
		} else {
			String finalMsg = this.buildNoEvidenceMsg(noEvidenceErrorNos.toString())
			+ "<br/>"
				+ this.buildNoAppendMsg(statusErrorNos.toString());
			rst.put("hasError", "true");
			rst.put("content", finalMsg);
		}

		return rst;
	}

	/**
	 * 对账凭证
	 *
	 * @return
	 */
	@ApiOperation(value = "对账凭证")
	@ResponseBody
	@RequestMapping(value = "/reconciliationManage/findEvidences", method = RequestMethod.POST)
	public List<ImageUploadManage> findEvidenceList(@RequestBody ReconciliationManageQueryVo queryVo) {
		if (queryVo.getFilters() == null) return null;
		if (queryVo.getFilters().getReconciliationId() == null) return null;
		return imageUploadManageService.listByRelationIdAndSign(queryVo.getFilters().getReconciliationId(), ImageUploadManage.ImageUploadManageSign.RECONCILIATION_RECEIVABLE.getCode());
	}


	/**
	 * 单次:上传对账凭证
	 *
	 * @param imageUploadManage
	 * @param loginEmployee
	 */
	@ApiOperation(value = "上传对账凭证")
	@ResponseBody
	@RequestMapping(value = "/reconciliation/addEvidence", method = RequestMethod.POST)
	public void addReconciliationEvidence(@RequestBody ImageUploadManage imageUploadManage, LoginEmployee loginEmployee) {
		if (imageUploadManage == null) return;
		if (StringUtils.isBlank(imageUploadManage.getImageUploadUrl()))
			throw new BusinessException("evidenceUrlNull", "reconciliation.evidence.nullError");
		if (imageUploadManage.getRelationId() == null)
			throw new BusinessException("reconciliationIdNull", "errors.paramCanNotNullWithName", "对账单ID未知，请刷新页面重试或联系客服");

		imageUploadManage.setImageUploadManageSign(ImageUploadManage.ImageUploadManageSign.RECONCILIATION_RECEIVABLE.getCode());

		imageUploadManageService.insert(imageUploadManage, loginEmployee);

	}

	/**批量:上传对账凭证**/
	@ApiOperation(value = "批量上传对账凭证")
	@ResponseBody
	@RequestMapping(value = "/reconciliation/addEvidences", method = RequestMethod.POST)
	public void addReconciliationEvidences(@RequestBody FileUploadParameter fileUploadParameter, LoginEmployee loginEmployee) {
		if (null == fileUploadParameter.getRelationId()) {
			throw new BusinessException("reconciliationIdUnknown", "errors.paramCanNotNullWithName", "对账单ID未知，请刷新页面重试或联系客服");
		}
		imageUploadManageService.batchInsert(fileUploadParameter.getListUploadFile(), fileUploadParameter.getRelationId(), ImageUploadManage.ImageUploadManageSign.RECONCILIATION_RECEIVABLE, loginEmployee);
	}

	/**
	 * 删除已上传的凭证
	 */
	@ApiOperation(value = "删除已上传的凭证")
	@ResponseBody
	@RequestMapping(value = "/reconciliationManage/{imageId}/delEvidence", method = RequestMethod.POST)
	public void deleteEvidence(@PathVariable(value = "imageId") Integer imageId) {
		imageUploadManageService.delByImageUploadManageId(imageId);
	}

	/**
	 * 撤销对账单
	 *
	 * @param id
	 * @param loginEmployee
	 */
	@ApiOperation(value = "撤销对账单")
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{id}/cancel", method = RequestMethod.GET)
	public void cancelReconciliation(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
		receivableService.cancelReconciliation(id, loginEmployee);
	}

	@ApiOperation(value = "撤销审核")
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{id}/cancelWorkFlowTask", method = RequestMethod.GET)
	public void cancelWorkFlowTask(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
		receivableService.cancelWorkFlowTask(id, loginEmployee);
	}

	/**
	 * 对账信息总览
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取对账单详情数据")
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{id}/overview", method = RequestMethod.GET)
	public ReconcilicationForReceivableVo getReconciliationOverview(@PathVariable("id") Integer id) {
		return receivableService.getReconciliationOverView(id);
	}

	/**
	 * 通过工作流实例id获取对账单
	 *
	 * @param processInstanceId
	 * @return
	 */
	@ApiOperation(value = "通过工作流实例id获取对账单")
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{processInstanceId}/processInstanceId", method = RequestMethod.GET)
	public ReconcilicationForReceivable getReconciliationByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) {
		return receivableService.findReconciliationByProcessInstanceId(processInstanceId);
	}

	/**
	 * 通过对账单号获取对账单
	 *
	 * @param reconciliationNo
	 * @return
	 */
	@ApiOperation(value = "通过对账单号获取对账单")
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{reconciliationNo}/reconciliationNo", method = RequestMethod.GET)
	public ReconcilicationForReceivable getReconciliationByreconciliationNo(@PathVariable("reconciliationNo") String reconciliationNo) {
		return receivableService.findReconciliationByReconciliationNo( reconciliationNo );
	}

	/**
	 * 组装导出数据
	 *
	 * @param id
	 * @param httpServletResponse
	 */
	@ApiOperation(value = "导出明细")
	@RequestMapping(value = "reconciliationManage/{id}/exportExcel", method = RequestMethod.GET)
	public void exportExcelReconciliation(@PathVariable("id") Integer id, HttpServletResponse httpServletResponse) {

		ReconcilicationForReceivableVo viewVo = receivableService.getReconciliationOverView(id);

		List<ReconcilicationForReceivableItemExcelVo> itemExcelVos = receivableService.findReceivableItemByReconciliationId(id);

		this.doExport(viewVo, itemExcelVos, httpServletResponse);

	}

	/**
	 * 处理工作流审批
	 *
	 * @param task
	 * @param loginEmployee
	 */
	@RequestMapping(value = "reconciliationManage/doTask", method = RequestMethod.POST)
	@ResponseBody
	public void doWorkFlowTask(@RequestBody Task task, LoginEmployee loginEmployee) {
//		receivableService.finishWorkFlowTask(task, loginEmployee);
	}

	/**
	 * 执行导出
	 *
	 * @param viewVo
	 * @param itemExcelVos
	 * @param httpServletResponse
	 */
	private void doExport(ReconcilicationForReceivableVo viewVo, List<ReconcilicationForReceivableItemExcelVo> itemExcelVos, HttpServletResponse httpServletResponse) {
		//概览
		List<ReconciliationOverViewExcelVo> excelOverViewVos = this.buildExcelOverView(viewVo);
		String fileName = "对账单号" + viewVo.getReconcilicationNo() + "对账明细.xls";

		try {
			XssfWriter xssfWriter = new XssfWriter();
			httpServletResponse.setContentType(CONTENT_TYPE);
			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
			xssfWriter.appendToSheet("对账单概览", excelOverViewVos).appendToSheet("运单明细", itemExcelVos).writeToOutputStream(httpServletResponse.getOutputStream());
		} catch (Exception e) {
			throw new BusinessException("export error ", "import.xlsx.export.error");
		}
	}

	/**
	 * Excel - 组装对账单详情
	 *
	 * @param viewVo
	 * @return
	 */
	private List<ReconciliationOverViewExcelVo> buildExcelOverView(ReconcilicationForReceivableVo viewVo) {
		List<ReconciliationOverViewExcelVo> vos = new ArrayList<>();
		if (viewVo == null) return vos;

		StringBuffer line1Buffer = new StringBuffer("");
		line1Buffer.append("客户:");
		line1Buffer.append(viewVo.getCustomerName());
		line1Buffer.append(",项目:");
		if (StringUtils.isBlank(viewVo.getProjectName())) {
			line1Buffer.append("无");
		} else {
			line1Buffer.append(viewVo.getProjectName());
		}
		line1Buffer.append(",对账单号:");
		line1Buffer.append(viewVo.getReconcilicationNo());
		line1Buffer.append(",共");
		line1Buffer.append(viewVo.getWaybillNum());
		line1Buffer.append("个运单,开票状态:");
		line1Buffer.append(ReconcilicationForReceivableEnum.InvoiceStatus.getInvoiceStatusByCode(viewVo.getInvoiceStatus()).getDesc());
		line1Buffer.append(",收款状态:");
		line1Buffer.append(ReconcilicationForReceivableEnum.ReceiptStatus.getReceiptStatusByCode(viewVo.getReceiveStatus()).getDesc());

		ReconciliationOverViewExcelVo vo1 = new ReconciliationOverViewExcelVo();
		vo1.setOverViewString(line1Buffer.toString());
		vos.add(vo1);
		String line2 = "客户总费用:含税总金额" + viewVo.getReceivableWithTax() + "元,不含税总金额" + viewVo.getReceivableWithoutTax() + "元";
		ReconciliationOverViewExcelVo vo2 = new ReconciliationOverViewExcelVo();
		vo2.setOverViewString(line2);
		vos.add(vo2);
		return vos;

	}

	/**
	 * 过滤错误数据
	 * @param ids
	 * @param incorrectIds
	 * @param noEvidenceErrorNos
	 */
	private void filterErrorData(List<Integer> ids, List<Integer> incorrectIds, StringBuilder noEvidenceErrorNos, StringBuilder statusErrorNos) {
		for (Integer id : ids) {
			//对账单详情
			ReconcilicationForReceivable receivable = receivableService.findReceivableById(id);
			//对账单凭证
			List<ImageUploadManage> images = imageUploadManageService.listByRelationIdAndSign(id, ImageUploadManage.ImageUploadManageSign.RECONCILIATION_RECEIVABLE.getCode());

			if (images == null || images.isEmpty()) {
				//没有上传对账单
				incorrectIds.add(id);
				noEvidenceErrorNos.append(receivable.getReconcilicationNo());
				noEvidenceErrorNos.append(",");
			}
			if(receivable != null && NumberUtils.compare(receivable.getApprovalStatus(), ReconcilicationForReceivableEnum.ApprovalStatusStatus.Append.getCode()) != 0){

				//不是未审核状态不能提交
				incorrectIds.add(id);
				statusErrorNos.append(receivable.getReconcilicationNo());
				statusErrorNos.append(",");
			}

		}
	}

	/**
	 * 未上传凭证错误
	 * @param noEvidenceNos
	 * @return
	 */
	private String buildNoEvidenceMsg(String noEvidenceNos) {
		if(StringUtils.isBlank(noEvidenceNos)) return "";

		String msg = "对账单号:" + noEvidenceNos + "提交失败\n请上传凭证后重新提交";

		return msg;
	}

	/**
	 * 不是未审核状态
	 * @param noAppendNos
	 * @return
	 */
	private String buildNoAppendMsg(String noAppendNos) {
		if(StringUtils.isBlank(noAppendNos)) return "";

		String msg = "对账单号:" + noAppendNos + "提交失败\n不是未审核状态";

		return msg;
	}

	/**
	 * 签约、运营主体不一样
	 *
	 * @param reconciliationId
	 * @return
	 */
	@ApiOperation(value = "签约、运营主体不一样，获取关联单据详情数据")
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{reconciliationId}/link", method = RequestMethod.GET)
	public List<ReconcilicationForCompanyVo> link(@PathVariable("reconciliationId") Integer reconciliationId, LoginEmployee loginEmployee) {
		List<ReconcilicationForCompanyVo> out = new ArrayList<ReconcilicationForCompanyVo>();
		ReconcilicationForReceivable receivable = receivableService.findReceivableById(reconciliationId);
		if (null == receivable) {
			return out;
		}

		List<ReconcilicationForCompany> rows = receivableService
				.findReconcilicationForCompanyByReconcilicationId(reconciliationId);
		for (ReconcilicationForCompany row : rows) {
			ReconcilicationForCompanyVo vo = new ReconcilicationForCompanyVo();
			BeanUtils.copyProperties(row, vo);
			Department payToCompany = departmentService.loadDepartment(row.getPayToCompany());
			Department contractToCompany = departmentService.loadDepartment(row.getContractToCompany());
			vo.setPayToCompanyName(payToCompany == null ? "" : payToCompany.getBusinessLicenceName());
			vo.setContractToCompanyName(contractToCompany == null ? "" : contractToCompany.getBusinessLicenceName());

			Project project = projectService.getProjectV2(receivable.getProjectId());
			if (null == project) {
				continue;
			}

			ContractVo contractVo = crmCommonService.loadByContractNo(project.getContractNo(), loginEmployee);
			vo.setPayToCompanyEnclosureUrl(contractVo == null ? null : contractVo.getPayToCompanyEnclosureUrl());
			out.add(vo);
		}
		return out;
	}

	/**
	 *
	 * @Title: reconcilicationForCompanyLink
	 * @Description: 测试MQ
	 * @param: @param reconciliationId
	 * @return: void
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{reconciliationId}/companyLink", method = RequestMethod.GET)
	public void reconcilicationForCompanyLink(@PathVariable("reconciliationId") Integer reconciliationId) {
		receivableService.sendReconcilicationForCompanyLink(reconciliationId);
	}

	/**
	 *
	 * @Title: sendToFMS
	 * @Description: 后门  发给FMS MQ消息
	 * @param: @param reconcilicationNo
	 * @param: @param loginEmployee
	 * @return: void
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "reconciliationManage/{reconcilicationNo}/sendToFms", method = RequestMethod.GET)
	public void sendToFMS(@PathVariable("reconcilicationNo")String reconcilicationNo) {
		receivableService.sendToFMS(reconcilicationNo);
	}

	/**批量发送开票数据给开票系统**/
	@ResponseBody
	@RequestMapping(value = "batch/send/invoice/message", method = RequestMethod.POST)
	public void batchSendInvoiceMessage(int[] reconciliationIds){
		if( null == reconciliationIds || reconciliationIds.length == 0 ){ return; }
		List<Integer> ids = Lists.newArrayList();
		for (int id : reconciliationIds){
			ids.add(id);
		}
		receivableService.batchSendInvoiceMessage(ids);
	}
}
