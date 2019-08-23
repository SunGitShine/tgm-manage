package com.juma.tgm.manage.waybill.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.waybill.vo.OfflineWaybillVo;
import com.juma.tgm.waybillReport.domain.OfflineWaybill;
import com.juma.tgm.waybillReport.domain.OfflineWaybillResponse;
import com.juma.tgm.waybillReport.service.OfflineWaybillService;
import me.about.poi.reader.XlsxReader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("offlineWaybill")
public class OfflineWaybillController {

    @Resource
    private OfflineWaybillService offlineWaybillService;

    /**
     * 分页查询
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<OfflineWaybill> search(
            PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.setOrderBy(" status asc, plan_delivery_time desc ");
        return offlineWaybillService.search(pageCondition, loginEmployee);
    }

    /**
     * 导入
     * 
     * @throws Exception
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public OfflineWaybillResponse importOffline(@RequestParam(required = false) MultipartFile uploadXlsx,
                                                Integer isCheckProjectStatus,Integer templateType, LoginEmployee loginEmployee)
            throws IOException, Exception {

        System.out.println("isCheckProjectStatus="+isCheckProjectStatus);
        System.out.println("templateType="+templateType);

        if (uploadXlsx == null || uploadXlsx.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXlsx.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }
        
        OfflineWaybillResponse response = null;
        try {
            response = offlineWaybillService.handleOfflineWaybill(XlsxReader.fromInputStream(uploadXlsx.getInputStream(), OfflineWaybill.class, 2),
                    templateType == null ? false : (templateType == 1 ? true : false), loginEmployee);
        } catch (RuntimeException e) {
            throw new BusinessException("excelError", e.getMessage());
        }
       return response;
    }

    /**
     * 保存
     */
    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public int saveOffline(@RequestBody OfflineWaybillVo offlineWaybillVo,LoginEmployee loginEmployee) {
        return offlineWaybillService.transferToWaybill(offlineWaybillVo.getDepartmentCode(),offlineWaybillVo.getOfflineWaybillIds(),loginEmployee);
    }
    
    /**
     * 删除
     */
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public void delete(@RequestBody OfflineWaybillVo offlineWaybillVo,LoginEmployee loginEmployee) {
        offlineWaybillService.deleteByIds(offlineWaybillVo.getOfflineWaybillIds());
    }

}
