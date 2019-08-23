package com.juma.tgm.manage.fms.controller.v2;


import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.domain.v2.bo.ReconciliationStatistics;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationWaybillDetailVo;
import com.juma.tgm.fms.service.v2.ReconciliationBootstrapService;
import com.juma.tgm.fms.service.v2.ReconciliationService;
import com.juma.tgm.manage.fms.controller.v2.vo.FiltersVo;
import com.juma.tgm.manage.fms.controller.v2.vo.ProjectReconciliationVo;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.TruckRequireService;
import com.juma.tgm.waybillReconciliation.domain.WaybillReconciliation;
import me.about.poi.writer.XssfWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/v2/reconciliation/bootstrap")
public class ReconciliationBootstrapController {

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private ImportUpdateFreightCheck importUpdateFreightCheck;

    @Resource
    private TruckRequireService truckRequireService;

    @Resource
    private ReconciliationService reconciliationServiceV2;

    @Resource
    private ReconciliationBootstrapService reconciliationBootstrapService;

    @RequestMapping( value="create" , method = RequestMethod.POST)
    @ResponseBody
    public String create(  @RequestBody ArrayList<Integer> waybillIdList , LoginEmployee loginEmployee) {
        return reconciliationServiceV2.createReconciliation( waybillIdList , loginEmployee );
    }

    @RequestMapping(value = "statistics/search",method = RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationStatistics> search(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) throws BusinessException{
        return reconciliationBootstrapService.search( pageCondition , loginEmployee );
    }

    @RequestMapping(value = "statistics/find",method = RequestMethod.POST)
    @ResponseBody
    public List<ReconciliationStatistics> find(@RequestBody FiltersVo<ProjectReconciliationVo> filtersVo, LoginEmployee loginEmployee) throws BusinessException {
        return reconciliationBootstrapService.find(filtersVo.getFilters() != null  ? filtersVo.getFilters().getCustomerId() : null
                , filtersVo.getFilters() != null ? filtersVo.getFilters().getProjectName() : null , loginEmployee );
    }

    @RequestMapping(value = "waybills/search",method = RequestMethod.POST)
    @ResponseBody
    public   Page<ReconciliationWaybillDetailVo> searchWaybills(LoginEmployee loginEmployee ,@RequestBody PageCondition pageCondition ) throws BusinessException{
//        PageCondition pageCondition = new PageCondition();
//        pageCondition.setPageNo(1);
//        pageCondition.setPageSize( Integer.MAX_VALUE);;
//        pageCondition.setFilters( filtersVo.getFilters() );
        return reconciliationBootstrapService.searchWaybills( loginEmployee , pageCondition );
    }

    @ResponseBody
    @RequestMapping(value = "freight/import-update", method = RequestMethod.POST)
    public void importResult(@RequestParam(required = false) MultipartFile uploadPic, CurrentUser currentUser, LoginEmployee loginEmployee) throws BusinessException{
        List<WaybillReconciliation> waybillReconciliations = importUpdateFreightCheck.checkImportFileAndFrom( uploadPic, loginEmployee );
        List<String> areaNodeList = new ArrayList<>();
        if ( currentUser != null ) {
            for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
                areaNodeList.add(businessAreaNode.getAreaCode());
            }
        }
        reconciliationBootstrapService.update( waybillReconciliations , areaNodeList , loginEmployee );
    }

    @RequestMapping( value="freight/export-update-model" , method = RequestMethod.POST)
    public void exportUpdateModel(  Integer [] waybillIds , LoginEmployee loginEmployee,HttpServletResponse httpServletResponse )  throws BusinessException{
        List<Integer> waybillIdList = Arrays.asList( waybillIds );
        List<Waybill> waybills = reconciliationBootstrapService.findByWaybillIds( waybillIdList , loginEmployee );
        List<WaybillReconciliation> waybillReconciliations = new ArrayList<>();
        for( Waybill waybill : waybills ) {
            WaybillReconciliation waybillReconciliation = new WaybillReconciliation();
            buildWaybillReconciliation( waybill , waybillReconciliation);
            TruckRequire truckRequire = truckRequireService.findTruckRequireByWaybillId( waybill.getWaybillId(), loginEmployee);
            waybillReconciliation.setTaxRateValue(truckRequire.getTaxRateValue());
            waybillReconciliations.add( waybillReconciliation );
        }
        try {
            httpServletResponse.setContentType(CONTENT_TYPE );
            httpServletResponse.setHeader("Content-disposition", "attachment; filename=update-model.xlsx");
            new XssfWriter().appendToSheet("运单改价模板" , waybillReconciliations).writeToOutputStream(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            throw new BusinessException("export error " , "import.xlsx.export.error" );
        }
    }


    private void buildWaybillReconciliation( Waybill waybill, WaybillReconciliation waybillReconciliation ) {
        BeanUtils.copyProperties( waybill , waybillReconciliation );
        if(waybill.getVendorId() == null ) {
            waybillReconciliation.setSettlementObject("司机");
        }
        else {
            // 承运商
           // waybillReconciliation.setPlateNumber("-");
            waybillReconciliation.setSettlementObject("承运商");
            waybillReconciliation.setDriverName( waybillReconciliation.getVendorName());
        }
    }

}
