package com.juma.tgm.manage.fms.controller.v3;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.fms.domain.v3.AdjustForItem;
import com.juma.tgm.fms.domain.v3.enums.AdjustMasterType;
import com.juma.tgm.fms.domain.v3.vo.*;
import com.juma.tgm.fms.service.v3.AdjustForMasterAddService;
import com.juma.tgm.fms.service.v3.AdjustForMasterService;
import com.juma.tgm.fms.service.v3.AdjustForWaybillService;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.project.domain.v2.Project;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillAmount;
import com.juma.tgm.waybill.domain.WaybillVO;
import com.juma.tgm.waybill.domain.vo.WaybillAmountFilter;
import com.juma.tgm.waybill.service.WaybillAmountService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.vms.driver.domain.Driver;
import com.juma.vms.driver.vo.DriverQuery;
import com.juma.vms.truck.vo.TruckQuery;
import com.juma.vms.vendor.domain.Vendor;
import com.juma.vms.vendor.vo.VendorFilter;
import com.juma.vms.vendor.vo.VendorQuery;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.about.poi.writer.XlsxWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调整单入口
 * 功能 :
 * 1.列表
 * 2.新增调整单
 * 3.编辑调整单
 * 4.调整单信息(详情)
 * 5.运单列表
 * 6.运单导出
 * 7.运单导入
 * 8.
 * @author : Bruce(刘正航) 16:10 2019-05-10
 */
@Controller
@RequestMapping(value = "adjust")
public class AdjustController extends BaseController{

	/**运单导出上限**/
	private static final int WAY_BILL_EXPORT_LIMIT = 500;

	@Resource
	private AdjustForMasterService adjustForMasterService;

	@Resource
	private AdjustForMasterAddService adjustForMasterAddService;

	@Resource
	private AdjustForWaybillService adjustForWaybillService;

	@Resource
	private ProjectService projectService;

	@Resource
	private CustomerInfoService customerInfoService;

	@Resource
	private VmsCommonService vmsCommonService;

	@Resource
	private WaybillService waybillService;

	@Resource
	private WaybillAmountService waybillAmountService;

	@ApiOperation(value = "分页列表")
	@ResponseBody
	@RequestMapping(value = "search", method = RequestMethod.POST)
	public Page<AdjustForMasterVo> search(@RequestBody QueryCond<AdjustForMasterVo> queryCond, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		queryCond.getFilters().setAreaCodeList(this.formatAreaCode(queryCond.getFilters().getAreaCodeList()));
		return adjustForMasterService.findAdjustForMasterPage(queryCond, loginEmployee);
	}

	private List<String> formatAreaCode(List<String> areaCodeList){
		if (areaCodeList == null || areaCodeList.isEmpty()) {
			areaCodeList = new ArrayList<>();
			areaCodeList.add("-999");
			return areaCodeList;
		}

		// 去掉业务范围全国00
		if (areaCodeList.contains("00")) {
			areaCodeList.remove("00");
		}

		return areaCodeList;
	}

	@ApiOperation(value = "运单临时表列表")
	@ResponseBody
	@RequestMapping(value = "temp/search", method = RequestMethod.POST)
	public org.mybatis.generator.my.page.Page<AdjustForWaybillVO> tempSearch(@RequestBody QueryCond<AdjustForWaybillTempFilter> filter, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		return adjustForWaybillService.findPageByFilter(filter,loginEmployee);
	}

	@ApiOperation(value = "编辑时,覆盖运单临时表")
	@ResponseBody
	@RequestMapping(value = "temp/covered", method = RequestMethod.POST)
	public void tempSearch(Integer adjustId, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		adjustForWaybillService.coverTempByUser(adjustId,loginEmployee);
	}

	@ApiOperation(value = "删除运单临时表")
	@ResponseBody
	@RequestMapping(value = "temp/delete", method = RequestMethod.POST)
	public void tempDelete(@RequestBody AdjustForWaybillVO filter, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		adjustForWaybillService.deleteTempById(filter.getAdjustTempId(),loginEmployee);
	}

	/**运单数据导出**/
	@ApiOperation(value = "改价运单导出-添加")
	@RequestMapping(value = "export/add", method = RequestMethod.POST)
	public void adjustExportForAdd(int[] waybillIds, Integer adjustForWho, LoginEmployee loginEmployee, HttpServletResponse response) throws Exception {

		if( null == adjustForWho ){ adjustForWho = AdjustMasterType.CUSTOMER.getCode(); }

		if(null == waybillIds || waybillIds.length == 0){
			exportNullExcel(adjustForWho, response);
			return;
		}
		if( WAY_BILL_EXPORT_LIMIT < waybillIds.length ){
			exportNullExcel(adjustForWho, response);
			return;
		}

		List<Integer> ids = Lists.newArrayList();
		for (int waybillId : waybillIds) { ids.add(waybillId); }
		List<Waybill> list = waybillService.findByWaybillIds(ids,loginEmployee);
		WaybillAmountFilter filter = new WaybillAmountFilter();
		filter.setWaybillIds(ids);
		List<WaybillAmount> amounts = waybillAmountService.findByFilter(filter,loginEmployee);
		Map<Integer,WaybillAmount> waybillAmountMap = Maps.newConcurrentMap();
		for (WaybillAmount amount : amounts){
			waybillAmountMap.put(amount.getWaybillId(),amount);
		}
		exportWaybillByMasterType(adjustForWho, response, list, waybillAmountMap, loginEmployee);
	}

	/**运单数据导出**/
	@ApiOperation(value = "改价运单导出-编辑")
	@RequestMapping(value = "export/edit", method = RequestMethod.POST)
	public void adjustExportForEdit(Integer adjustId, Integer adjustForWho, LoginEmployee loginEmployee, HttpServletResponse response) throws Exception {

		if( null == adjustForWho ){ adjustForWho = AdjustMasterType.CUSTOMER.getCode(); }

		if( null == adjustId ){
			exportNullExcel(adjustForWho, response);
			return;
		}

		AdjustForItemFilter filter = new AdjustForItemFilter();
		filter.setAdjustId(adjustId);
		List<AdjustForItem> items = adjustForMasterAddService.findItemByFilter(filter,loginEmployee);
		if( CollectionUtils.isEmpty(items) ){
			exportNullExcel(adjustForWho, response);
			return;
		}
		List<String> waybillNos = Lists.newArrayList();
		for (AdjustForItem item : items) {
			waybillNos.add(item.getWaybillNo());
		}

		List<Waybill> list = waybillService.findByWaybillNos(waybillNos);
		List<Integer> waybillIds = Lists.newArrayList();
		for (Waybill waybill: list) {
			waybillIds.add(waybill.getWaybillId());
		}

		WaybillAmountFilter amountFilter = new WaybillAmountFilter();
		amountFilter.setWaybillIds(waybillIds);
		List<WaybillAmount> amounts = waybillAmountService.findByFilter(amountFilter,loginEmployee);
		Map<Integer,WaybillAmount> waybillAmountMap = Maps.newConcurrentMap();
		for (WaybillAmount amount : amounts){
			waybillAmountMap.put(amount.getWaybillId(),amount);
		}

		exportWaybillByMasterType(adjustForWho, response, list, waybillAmountMap, loginEmployee);
	}

	/**根据运单-对账单类型-导出不同模板数据**/
	private void exportWaybillByMasterType(final Integer adjustForWho, final HttpServletResponse response, final List<Waybill> list, final Map<Integer, WaybillAmount> waybillAmountMap, final LoginEmployee loginEmployee) throws Exception {
		List<WaybillCustomerExportVO> customers = Lists.newArrayList();
		List<WaybillVendorExportVO> vendors = Lists.newArrayList();
		for (Waybill waybill : list) {
			if (AdjustMasterType.CUSTOMER.getCode().equals(adjustForWho)) {
				WaybillCustomerExportVO vo = new WaybillCustomerExportVO();
				vo.setWaybillNo(waybill.getWaybillNo());
				vo.setCustomerName(waybill.getCustomerName());
				vo.setProjectName(waybill.getProjectName());
				vo.setVendorName(waybill.getVendorName());
				vo.setDriverName(waybill.getDriverName());
				vo.setPlateNumber(waybill.getPlateNumber());
				WaybillAmount waybillAmount = waybillAmountMap.get(waybill.getWaybillId());
				fillAdjustWaybillExtendInfo(vo, waybill, waybillAmount, loginEmployee);
				customers.add(vo);
			}
			if (AdjustMasterType.VENDOR.getCode().equals(adjustForWho)) {
				WaybillVendorExportVO vo = new WaybillVendorExportVO();
				vo.setWaybillNo(waybill.getWaybillNo());
				vo.setCustomerName(waybill.getCustomerName());
				vo.setProjectName(waybill.getProjectName());
				vo.setVendorName(waybill.getVendorName());
				vo.setDriverName(waybill.getDriverName());
				vo.setPlateNumber(waybill.getPlateNumber());
				WaybillAmount waybillAmount = waybillAmountMap.get(waybill.getWaybillId());
				fillAdjustWaybillExtendInfo(vo, waybill, waybillAmount, loginEmployee);
				vendors.add(vo);
			}
		}
		//1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
		response.setContentType("application/form-data");
		// 转码中文
		String fileName = new String((AdjustMasterType.getByCode(adjustForWho).getDesc() + "侧运费调整模板").getBytes(StandardCharsets.UTF_8), "iso8859-1");
		// TODO: 2017/4/27 文件扩展名
		response.setHeader("Content-Disposition", "attachment;fileName=" + fileName + ".xlsx");

		if (AdjustMasterType.CUSTOMER.getCode().equals(adjustForWho)) {
			exportDataExcel(adjustForWho, response, customers);
		}

		if (AdjustMasterType.VENDOR.getCode().equals(adjustForWho)) {
			exportDataExcel(adjustForWho, response, vendors);
		}
	}

	/**导出excel,包含表头和数据**/
	private void exportDataExcel(final Integer adjustForWho, final HttpServletResponse response, List<?> datas) throws Exception {
		if(CollectionUtils.isEmpty(datas)){
			exportNullExcel(adjustForWho, response);
			return;
		}
		try {
			XlsxWriter.toOutputStream(datas,response.getOutputStream());
		} catch (Exception e) {
			throw new BusinessException("ExcelAnalysisExportError",e.getMessage());
		}
	}

	/**导出空excel,只包含表头**/
	private void exportNullExcel(Integer adjustForWho, HttpServletResponse response) throws Exception {
		if( AdjustMasterType.CUSTOMER.getCode().equals(adjustForWho) ){
			XlsxWriter.toOutputStream(Lists.newArrayList(new WaybillCustomerExportVO()),response.getOutputStream());
		}
		if( AdjustMasterType.VENDOR.getCode().equals(adjustForWho) ){
			XlsxWriter.toOutputStream(Lists.newArrayList(new WaybillVendorExportVO()),response.getOutputStream());
		}
	}

	private void fillAdjustWaybillExtendInfo(WaybillVendorExportVO vo, Waybill waybill, WaybillAmount waybillAmount, LoginEmployee loginEmployee) {
		WaybillAmountFilter filter = new WaybillAmountFilter();
		filter.setWaybillId(waybill.getWaybillId());
		if( null == waybillAmount ){
			vo.setFreightWithTax(waybill.getShow4DriverFreight());
		}else{
			vo.setFreightWithTax(waybillAmount.getLastVendorFreightWithTax());
		}
		if( null != waybill.getCustomerId() && StringUtils.isBlank(waybill.getCustomerName()) ){
			// 客户名称
			CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());
			if( null != customerInfo ){
				vo.setCustomerName(customerInfo.getCustomerName());
			}
		}
		if( null != waybill.getProjectId() && StringUtils.isBlank(waybill.getProjectName())){
			// 项目名称
			Project project = projectService.getProjectV2(waybill.getProjectId());
			if( null != project ){
				vo.setProjectName(project.getName());
			}
		}
		if( (null != waybill.getVendorId()|| null != waybill.getVehicleToVendor()) && StringUtils.isBlank(waybill.getVendorName()) ){
			Integer vendorId;
			if( Waybill.ReceiveWay.TRANSFORM_BILL.getCode() == waybill.getReceiveWay() ) {
				vendorId = waybill.getVendorId();
			}else{
				vendorId = waybill.getVehicleToVendor();
			}
			// 承运商名称
			Vendor vendor = vmsCommonService.loadVendorByVendorId(vendorId);
			if( null != vendor ){
				vo.setVendorName(vendor.getVendorName());
			}
		}

		if( null != waybill.getDriverId() && StringUtils.isBlank(waybill.getDriverName()) ){
			// 司机名称
			Driver driver = vmsCommonService.loadDriverByDriverId(waybill.getDriverId());
			if( null != driver ){
				vo.setDriverName(driver.getName());
			}
		}
	}

	private void fillAdjustWaybillExtendInfo(WaybillCustomerExportVO vo, Waybill waybill, WaybillAmount waybillAmount, LoginEmployee loginEmployee) {
		WaybillAmountFilter filter = new WaybillAmountFilter();
		filter.setWaybillId(waybill.getWaybillId());
		if( null == waybillAmount ){
			vo.setFreightWithTax(waybill.getEstimateFreight());
		}else{
			vo.setFreightWithTax(waybillAmount.getLastCustomerFreightWithTax());
		}
		if( null != waybill.getCustomerId() && StringUtils.isBlank(waybill.getCustomerName()) ){
			// 客户名称
			CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());
			if( null != customerInfo ){
				vo.setCustomerName(customerInfo.getCustomerName());
			}
		}
		if( null != waybill.getProjectId() && StringUtils.isBlank(waybill.getProjectName())){
			// 项目名称
			Project project = projectService.getProjectV2(waybill.getProjectId());
			if( null != project ){
				vo.setProjectName(project.getName());
			}
		}
		if( (null != waybill.getVendorId()|| null != waybill.getVehicleToVendor()) && StringUtils.isBlank(waybill.getVendorName()) ){
			Integer vendorId;
			if( Waybill.ReceiveWay.TRANSFORM_BILL.getCode() == waybill.getReceiveWay() ) {
				vendorId = waybill.getVendorId();
			}else{
				vendorId = waybill.getVehicleToVendor();
			}
			// 承运商名称
			Vendor vendor = vmsCommonService.loadVendorByVendorId(vendorId);
			if( null != vendor ){
				vo.setVendorName(vendor.getVendorName());
			}
		}

		if( null != waybill.getDriverId() && StringUtils.isBlank(waybill.getDriverName()) ){
			// 司机名称
			Driver driver = vmsCommonService.loadDriverByDriverId(waybill.getDriverId());
			if( null != driver ){
				vo.setDriverName(driver.getName());
			}
		}
	}

	@ApiOperation(value = "上传待调整运单数据")
	@ResponseBody
	@RequestMapping(value = "upload/waybill", method = RequestMethod.POST)
	public String uploadWaybill(@RequestBody AdjustAttachVO attachInfo, @ApiParam(hidden = true) final LoginEmployee loginEmployee) throws Exception {
		if(StringUtils.isBlank(attachInfo.getAttachUrl()) ){
			throw new BusinessException("attachNotNull","附件为空,请选择附件上传");
		}
		if (attachInfo.getAttachUrl().startsWith("//")) {
			attachInfo.setAttachUrl("http:" + attachInfo.getAttachUrl());
		}
		AdjustItemValidHolder holder = new AdjustItemValidHolder();
		holder = adjustForWaybillService.validWaybillForAdjust(attachInfo,null, holder, loginEmployee);
		if( CollectionUtils.isEmpty(holder.getReconciliations()) ){
			return null;
		}
		return holder.getReconciliations().iterator().next();
	}

	@ApiOperation(value = "新建/编辑调整单")
	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public void createAdjust(@RequestBody AdjustForMasterAddVO vo, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		adjustForWaybillService.validWaybillTempInfo(vo,loginEmployee);
	}

	@ApiOperation(value = "提交之前的信息展示")
	@ResponseBody
	@RequestMapping(value = "valid/before/submit", method = RequestMethod.POST)
	public WaybillStatisticsAmountVO validBeforeSubmit(@RequestBody AdjustForMasterAddVO vo, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		return adjustForMasterAddService.validBeforeSubmit(vo,loginEmployee);
	}

	@ApiOperation(value = "项目下拉列表")
	@ResponseBody
	@RequestMapping(value = "projectList", method = RequestMethod.GET)
	public List<Project> listProjectByName(@RequestParam String name, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		return projectService.listProjectBy(name, null, 15, null, loginEmployee);
	}

	@ApiOperation(value = "司机下拉列表")
	@ResponseBody
	@RequestMapping(value = "driverList", method = RequestMethod.GET)
	public List<DriverQuery> listDriverByName(@RequestParam String name, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		DriverQuery driverQuery = new DriverQuery();
		driverQuery.setName(name);
		return adjustForMasterService.listByDriver(driverQuery, 15, loginEmployee);
	}

	@ApiOperation(value = "车辆下拉列表")
	@ResponseBody
	@RequestMapping(value = "truckList", method = RequestMethod.GET)
	public List<TruckQuery> listTruckByPlateNumber(@RequestParam String plateNumber, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		TruckQuery truckQuery = new TruckQuery();
		truckQuery.setPlateNumber(plateNumber);
		return adjustForMasterService.listByPlateNumber(truckQuery, 15, loginEmployee);
	}

	@ApiOperation(value = "承运商下拉列表")
	@ResponseBody
	@RequestMapping(value = "vendorList", method = RequestMethod.GET)
	public List<VendorQuery> listVendorByName(@RequestParam String vendorName, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		VendorFilter vendorFilter = new VendorFilter();
		vendorFilter.setVendorName(vendorName);
		return adjustForMasterService.listByVendorFilter(vendorFilter, 15, loginEmployee);
	}

	@ApiOperation(value = "客户下拉列表")
	@ResponseBody
	@RequestMapping(value = "customerList", method = RequestMethod.GET)
	public List<CustomerInfo> listCustomerByName(@RequestParam String customerName, @ApiParam(hidden = true) LoginEmployee loginEmployee){

		Map<String, Object> filter = new HashMap<>();
		filter.put("tenantId", loginEmployee.getTenantId());
		filter.put("customerName", customerName);
		filter.put("isDelete", 0);
		PageCondition pageCondition = new PageCondition();
		pageCondition.setFilters(filter);
		pageCondition.setPageNo(1);
		pageCondition.setPageSize(15);
		return customerInfoService.listCustomerInfo(pageCondition);
	}

	@ApiOperation(value = "运单分页列表")
	@ApiImplicitParams({
		@ApiImplicitParam(value = "startFinishTime", name = "配送完成开始时间"),
		@ApiImplicitParam(value = "endFinishTime", name = "配送完成结束时间"),
		@ApiImplicitParam(value = "statusView", name = "运单状态"),
		@ApiImplicitParam(value = "waybillNo", name = "运单号"),
		@ApiImplicitParam(value = "reconciliationNo", name = "承运商对账单号"),
		@ApiImplicitParam(value = "receivableReconcilicationNo", name = "客户对账单号"),
		@ApiImplicitParam(value = "reconciliationStatus", name = "承运商对账状态，1：未对账，2：已对账"),
		@ApiImplicitParam(value = "receivableReconcilicationStatus", name = "客户对账状态，1：未对账，2：已对账"),
		@ApiImplicitParam(value = "customerId", name = "客户ID"),
		@ApiImplicitParam(value = "projectId", name = "项目ID"),
		@ApiImplicitParam(value = "vendorId", name = "承运商ID"),
		@ApiImplicitParam(value = "truckId", name = "车辆ID"),
	})
	@ResponseBody
	@RequestMapping(value = "waybillList", method = RequestMethod.POST)
	public Page<WaybillVO> listWaybillBy(@RequestBody PageCondition pageCondition, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		super.formatAreaCodeToList(pageCondition, true);
		return adjustForMasterService.searchWaybill(pageCondition, loginEmployee);
	}

	@ApiOperation(value = "调整单主数据详情")
	@ApiImplicitParams({
		@ApiImplicitParam(value = "adjustId", name = "调整单id"),
	})
	@ResponseBody
	@RequestMapping(value = "findAdjustDetail", method = RequestMethod.GET)
	public AdjustForMasterDetail findAdjustDetail(@RequestParam Integer adjustId, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		return adjustForMasterService.findAdjustDetail(adjustId, loginEmployee);
	}

	@ApiOperation(value = "调整单主数据详情")
	@ApiImplicitParams({
		@ApiImplicitParam(value = "adjustNo", name = "调整单编号"),
	})
	@ResponseBody
	@RequestMapping(value = "findAdjustDetailByNo", method = RequestMethod.GET)
	public AdjustForMasterDetail findAdjustDetailByNo(@RequestParam String adjustNo, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		return adjustForMasterService.findAdjustDetail(adjustNo, loginEmployee);
	}

	@ApiOperation(value = "调整单副数据列表")
	@ApiImplicitParams({
		@ApiImplicitParam(value = "adjustId", name = "调整单id"),
	})
	@ResponseBody
	@RequestMapping(value = "findAdjustItemPage", method = RequestMethod.POST)
	public Page<AdjustForItemDetail> findAdjustItemPage(@RequestBody QueryCond<AdjustForItem> queryCond, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		return adjustForMasterService.findAdjustItemPage(queryCond, loginEmployee);
	}

	@ApiOperation(value = "撤销调整单")
	@ApiImplicitParams({
		@ApiImplicitParam(value = "adjustId", name = "调整单id"),
	})
	@ResponseBody
	@RequestMapping(value = "cancelWorkFlowTask", method = RequestMethod.GET)
	public void cancelWorkFlowTask(@RequestParam Integer adjustId, @ApiParam(hidden = true) LoginEmployee loginEmployee){
		adjustForMasterService.cancelWorkFlowTask(adjustId, loginEmployee);
	}

	@ApiOperation(value = "撤销调整单")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "adjustId", name = "调整单ID"),
			@ApiImplicitParam(value = "tenantId", name = "租户ID"),
			@ApiImplicitParam(value = "userId", name = "创建人ID"),
	})
	@ResponseBody
	@RequestMapping(value = "cancelWorkFlowTask2", method = RequestMethod.GET)
	public void cancelWorkFlowTask2(@RequestParam Integer adjustId, @RequestParam Integer tenantId, @RequestParam Integer userId){
		LoginEmployee loginEmployee = new LoginEmployee();
		loginEmployee.setUserId(userId);
		loginEmployee.setTenantId(tenantId);
		adjustForMasterService.cancelWorkFlowTask(adjustId, loginEmployee);
	}

	/**运单数据导出**/
	@ResponseBody
	@ApiOperation(value = "批量发送-月账单/普通对账单到FMS")
	@RequestMapping(value = "/fms/batch/send", method = RequestMethod.POST)
	public void sendAdjustBillDatasToFmsOld(int[] adjustIds,int[] adjustItemIds){
		if( null == adjustIds || adjustIds.length == 0 ){ return; }
		List<Integer> adjustIdList = Lists.newArrayList();
		for (int adjustId : adjustIds) { adjustIdList.add(adjustId); }
		List<Integer> adjustItemIdList = Lists.newArrayList();
		for (int adjustItemId : adjustItemIds) { adjustItemIdList.add(adjustItemId); }
		LoginEmployee loginEmployee = new LoginEmployee();
		loginEmployee.setTenantId(19);
		loginEmployee.setUserId(1);
		adjustForMasterAddService.doResendBillDatasToFmsOld(adjustIdList,adjustItemIdList,loginEmployee);
	}
}
