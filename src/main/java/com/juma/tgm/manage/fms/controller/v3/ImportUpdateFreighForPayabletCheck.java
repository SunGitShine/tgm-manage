package com.juma.tgm.manage.fms.controller.v3;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.giants.common.exception.BusinessException;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.fms.domain.v3.bo.WaybillAdjustFrightForPayable;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.tgm.waybill.service.WaybillService;

import me.about.poi.reader.XlsxReader;

/***
 * @author huangxing
 *
 *
 *         导入进行批量改价的时候，首先对导入文件进行校验，包括文件类型和数据的必要型字段进行校验
 *
 */
@Component
public class ImportUpdateFreighForPayabletCheck {

    @Resource
    private WaybillService waybillService;

    @Resource
    private TaxRateService taxRateService;

    /***
     *
     *
     * 检查文件类型, 如果错误则直接抛出异常
     *
     * @param uploadXls
     *            上传文件描述
     */
    public List<WaybillAdjustFrightForPayable> checkImportFileAndFrom(MultipartFile uploadXls,
            LoginUser loginUser) throws BusinessException {
        if (uploadXls == null || uploadXls.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXls.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }
        List<WaybillAdjustFrightForPayable> waybillAdjustFrightForPayables;
        try {
            waybillAdjustFrightForPayables = XlsxReader.fromInputStream(uploadXls.getInputStream(),
                    WaybillAdjustFrightForPayable.class, 1);
        } catch (Exception e) {
            // 当 excel 的数据类型等填写错误时，会抛出该异常
            throw new BusinessException("file from input stream to object error", "import.xlsx.from.error");
        }
        checkWaybillAdjustFrightForPayables(waybillAdjustFrightForPayables, loginUser);
        return waybillAdjustFrightForPayables;// 检查数据后如果没有问题就返回
    }

    /***
     * 检查 数据完整度
     */
    private void checkWaybillAdjustFrightForPayables(List<WaybillAdjustFrightForPayable> waybillAdjustFrightForPayables,
            LoginUser loginUser) throws BusinessException {

        if (CollectionUtils.isEmpty(waybillAdjustFrightForPayables)) {
            throw new BusinessException("object error ", "import.xlsx.object.error", "没有可用的数据");
        }

        if (waybillAdjustFrightForPayables.size() > 300) {
            throw new BusinessException("object error ", "import.xlsx.object.error", "单次上传数据条数不能超过300");
        }
        // 循环的验证每一条数据，只要有一条有问题，那么就抛出异常
        for (WaybillAdjustFrightForPayable wfp : waybillAdjustFrightForPayables) {
            Integer index = waybillAdjustFrightForPayables.indexOf(wfp);
            // 如果没有运单号
            if (StringUtils.isBlank(wfp.getWaybillNo())) {
                throw new BusinessException("object error ", "import.xlsx.object.error",
                        "第" + (index + 1) + "条数据缺少，运单编号");
            } else {
                Waybill waybill = waybillService.findWaybillByWaybillNo(wfp.getWaybillNo(), loginUser);
                if (waybill == null) {
                    // 运单不存在
                    throw new BusinessException("object error ", "import.xlsx.object.error",
                            "第" + (index + 1) + "条数据运单编号:" + wfp.getWaybillNo() + ",不存在!");
                }
                wfp.setWaybillId(waybill.getWaybillId());

                // 判断 运单所处的 对账状态
                if (!waybill.getReconciliationStatus()
                        .equals(Waybill.ReconciliationStatus.NOT_RECONCILIATION.getCode())) {
                    throw new BusinessException("object error ", "import.xlsx.object.error",
                            "第" + (index + 1) + "条数据不是未对账状态");
                }
            }
        }
    }

}
