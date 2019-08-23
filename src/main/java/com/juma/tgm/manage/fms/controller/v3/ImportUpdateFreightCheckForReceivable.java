package com.juma.tgm.manage.fms.controller.v3;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.domain.v3.vo.ReconciliationWaybillExcelVo;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybillReconciliation.domain.WaybillReconciliation;
import me.about.poi.reader.XlsxReader;

/***
 * @author huangxing
 *
 *
 * 导入进行批量改价的时候，首先对导入文件进行校验，包括文件类型和数据的必要型字段进行校验
 *
 * */
@Component
public class ImportUpdateFreightCheckForReceivable {

    @Resource
    private WaybillService waybillService;

    @Resource
    private TaxRateService taxRateService;

    /***
     *
     *
     *检查文件类型, 如果错误则直接抛出异常
     *
     * @param uploadXls 上传文件描述
     * */
    public List<ReconciliationWaybillExcelVo> checkImportFileAndFrom(MultipartFile uploadXls, LoginEmployee loginEmployee) throws BusinessException {
        if (uploadXls == null || uploadXls.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXls.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }
        List<ReconciliationWaybillExcelVo> waybillReconciliations;
        try {
             waybillReconciliations = XlsxReader.fromInputStream(uploadXls.getInputStream(), ReconciliationWaybillExcelVo.class, 1);
        } catch (Exception e) {
            // 当 excel 的数据类型等填写错误时，会抛出该异常
            throw new BusinessException("file from input stream to object error", "import.xlsx.from.error");
        }
        checkWaybillReconciliations(waybillReconciliations,loginEmployee );
        return waybillReconciliations;// 检查数据后如果没有问题就返回
    }


    /***
     * 检查 数据完整度
     *
     *
     * */
    private void checkWaybillReconciliations(List<ReconciliationWaybillExcelVo> waybillReconciliations, LoginEmployee loginEmployee) throws BusinessException {

        if (waybillReconciliations == null || waybillReconciliations.size() == 0) {
            throw new BusinessException("object error ", "import.xlsx.object.error", "没有可用的数据");
        } else if (waybillReconciliations.size() > 300) {
            throw new BusinessException("object error ", "import.xlsx.object.error", "单次上传数据条数不能超过300");
        } else {
            // 循环的验证每一条数据，只要有一条有问题，那么就抛出异常
            for (ReconciliationWaybillExcelVo waybillReconciliation : waybillReconciliations) {
                Integer index = waybillReconciliations.indexOf(waybillReconciliation);
                // 如果没有运单号
                if (StringUtils.isBlank(waybillReconciliation.getWaybillNo())) {
                    throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据缺少，运单编号");
                }
                else {
                    Waybill waybill = waybillService.findWaybillByWaybillNo(waybillReconciliation.getWaybillNo(), loginEmployee);
                    if( waybill == null ) {
                        //运单不存在
                        throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据运单编号:" + waybillReconciliation.getWaybillNo() +",不存在!");
                    }
                    else {
                        waybillReconciliation.setWaybillNo( waybill.getWaybillNo() );
                        waybillReconciliation.setWaybillId(waybill.getWaybillId());
                        // 判断税率为空
                        if( waybillReconciliation.getTaxRateValue() == null ) {
                            throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据税率不能为空");
                        }
                        // 判断含税金额为空
                        if( waybillReconciliation.getEstimateFreight() == null ) {
                            throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据含税金额不能为空");
                        }
                        // 判断改价备注为空
                        if(StringUtils.isBlank(waybillReconciliation.getUpdateFreightRemark())) {
                            throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据改价备注不能为空");
                        }

                        //税率不能超出0~1范围
                        if(waybillReconciliation.getTaxRateValue().compareTo(BigDecimal.ZERO) == -1
                            || waybillReconciliation.getTaxRateValue().compareTo(BigDecimal.ONE) == 1){
                            throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据税率不能超出0~1范围");
                        }

//                        TaxRate taxRate = taxRateService.findTaxRateBy(waybillReconciliation.getTaxRateValue(), loginEmployee);
//                        // 判断税率正确性
//                        if (taxRate == null) {
//                            throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据税率不正确");
//                        }

                        // 判断 运单所处的 对账状态
                        if( !waybill.getReceivableReconcilicationStatus().equals( Waybill.ReconciliationStatus.NOT_RECONCILIATION.getCode())) {
                            throw new BusinessException("object error ", "import.xlsx.object.error", "第" + (index+1) + "条数据不是未对账状态");
                        }
                    }
                }
            }
        }
    }

}
