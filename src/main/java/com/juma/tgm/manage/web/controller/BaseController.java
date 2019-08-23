package com.juma.tgm.manage.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.PageCondition;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.Constants.WaybillQuickQueryParameterEnum;
import com.juma.tgm.operateLog.domain.OperateLog;
import com.juma.tgm.operateLog.enumeration.LogSignEnum;
import com.juma.tgm.operateLog.enumeration.OperateApplicatoinEnum;
import com.juma.tgm.operateLog.enumeration.OperateTypeEnum;
import com.juma.tgm.operateLog.service.OperateLogService;
import com.juma.tgm.waybill.domain.Waybill;

import net.sf.jxls.transformer.XLSTransformer;

/**
 * @author weilibin
 * @version V1.0
 * @date 2016年7月15日 下午3:56:25
 */

public class BaseController {

    @Resource
    private OperateLogService operateLogService;

    protected void exportExcel(Map<String, Object> data, Class<?> T, String exportName, String modelName,
            HttpServletResponse response) {
        XLSTransformer transformer = new XLSTransformer();
        OutputStream os = null;
        String date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        exportName = exportName + date + ".xls";
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + exportName);
            os = response.getOutputStream();
            InputStream is = T.getResourceAsStream("/template/" + modelName + ".xls");
            Workbook workbook = transformer.transformXLS(is, data);
            workbook.write(os);
            os.flush();
            is.close();
        } catch (Exception e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 逗号分隔的字符串转String List
     *
     * @param targetStr
     * @return
     */
    protected List<String> splitStringByComma(String targetStr) {

        if (StringUtils.isBlank(targetStr)) {
            return null;
        }

        return Arrays.asList(StringUtils.split(targetStr, ","));

    }

    /**
     * filters 当是map时，去掉value为空的项
     */
    protected void filtersIsMapThenRemoveVulueIsNull(PageCondition pageCondition) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null == filters || filters.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<String, Object>> it = filters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            Object obj = entry.getValue();
            if (null == obj || StringUtils.isBlank(obj.toString())) {
                it.remove();
            }
        }
    }

    /**
     * 转换pagecondition中areaCodeList字符串到List对象
     *
     * @param pageCondition
     */
    protected void formatAreaCodeToList(PageCondition pageCondition, boolean removeAreaCode00) {
        if (pageCondition == null) {
            return;
        }

        Map<String, Object> filter = pageCondition.getFilters();

        this.checkDateNaN(filter);
        pageCondition.setFilters(handleAreaCode(filter, removeAreaCode00));

        // 运单状态的处理
        this.handleWaybillQuickQueryStatus(filter);
    }

    protected Map<String, Object> handleAreaCode(Map<String, Object> filter, boolean removeAreaCode00) {
        if (filter == null) {
            filter = new HashMap<String, Object>();
            List<String> target = new ArrayList<String>();
            target.add("-999");
            filter.put("areaCodeList", target);
            return filter;
        }

        if (filter.get("areaCodeList") == null) {
            List<String> target = new ArrayList<String>();
            target.add("-999");
            filter.put("areaCodeList", target);
            return filter;
        }

        String str = filter.get("areaCodeList").toString();
        List<String> target = this.splitStringByComma(str);
        if (target == null) {
            target = new ArrayList<String>();
            target.add("-999");
            filter.put("areaCodeList", target);
            return filter;
        }

        if (!removeAreaCode00) {
            filter.put("areaCodeList", target);
            return filter;
        }

        // target是Arrays.asList生成的，不能使用remove方法
        List<String> arrList = new ArrayList<String>(target);
        // 去掉业务范围全国00
        if (arrList.contains("00")) {
            arrList.remove("00");
        }

        // 若业务范围只包含全国，则不使用业务范围条件
        if (arrList.isEmpty()) {
            filter.remove("areaCodeList");
        } else {
            filter.put("areaCodeList", arrList);
        }

        return filter;
    }

    // 校验时间是否正确
    private void checkDateNaN(Map<String, Object> filter) {
        if (null == filter) {
            return;
        }

        if ((null == filter.get("startTime") && null != filter.get("endTime"))
                || (null != filter.get("startTime") && null == filter.get("endTime"))) {
            throw new BusinessException("startTimeAndEndTimeCoexistence", "errors.startTimeAndEndTimeCoexistence");
        }

        if (null == filter.get("startTime") && null == filter.get("endTime")) {
            return;
        }

        String startTime = filter.get("startTime").toString();
        String endTime = filter.get("endTime").toString();
        if (startTime.contains("NaN") || startTime.contains("aN") || endTime.contains("NaN")
                || endTime.contains("aN")) {
            throw new BusinessException("timeParseException", "errors.timeParseException");
        }
    }

    protected void formatAreaCodeToList(PageQueryCondition pageQueryCondition, boolean removeAreaCode00) {
        Object filterT = pageQueryCondition.getFilters();
        if (filterT == null) {
            return;
        }

        if (filterT instanceof Map) {
            this.handleAreaCode((Map<String, Object>) filterT, removeAreaCode00);
        }
    }

    // 处理运单快捷查询条件
    protected void handleWaybillQuickQueryStatus(Map<String, Object> filter) {
        if (null == filter) {
            return;
        }

        for (WaybillQuickQueryParameterEnum p : Constants.WaybillQuickQueryParameterEnum.values()) {
            String lowerCase = p.toString().toLowerCase();
            if (null != filter.get(lowerCase)) {
                List<Integer> list = new ArrayList<Integer>();
                this.handleList(list, filter.get(lowerCase).toString());
                if (list.isEmpty()) {
                    filter.remove(lowerCase);
                    continue;
                }

                // 配送状态为待配送是有两种status_view in (-2, 2)
                if (Constants.WaybillQuickQueryParameterEnum.STATUS_VIEW_KEY.toString().toLowerCase().equals(lowerCase)
                        && list.contains(Waybill.StatusView.WATING_DELIVERY.getCode())) {
                    list.add(Waybill.StatusView.TEMP.getCode());
                }
                filter.put(p.getKey(), list);
            }
        }
    }

    // 字符串转为list集合
    private void handleList(List<Integer> list, String str) {
        if (StringUtils.isBlank(str)) {
            return;
        }

        if (!str.contains(",")) {
            list.add(Integer.parseInt(str));
            return;
        }

        String[] split = str.split(",");
        for (String string : split) {
            if (StringUtils.isBlank(string)) {
                continue;
            }
            list.add(Integer.parseInt(string));
        }
    }

    /**
     * 项目:添加操作记录
     */
    protected void insertLog(OperateTypeEnum operateTypeEnum, Integer relationTableId, String remark,
            LoginUser loginUser) {
        OperateLog log = new OperateLog();
        log.setLogSign(LogSignEnum.PROJECT.getCode());
        log.setOperateType(operateTypeEnum.getCode());
        log.setOperateApplicatoin(OperateApplicatoinEnum.BACKSTAGE.getCode());
        log.setRelationTableId(relationTableId);
        log.setRemark(remark);
        operateLogService.insertByDubboAsync(log, loginUser);
    }

    /**
     * 添加操作记录
     */
    protected void insertLog(LogSignEnum logSignEnum, OperateTypeEnum operateTypeEnum, Integer relationTableId,
                             String remark,
            LoginUser loginUser) {
        OperateLog log = new OperateLog();
        log.setLogSign(logSignEnum.getCode());
        log.setOperateType(operateTypeEnum.getCode());
        log.setOperateApplicatoin(OperateApplicatoinEnum.BACKSTAGE.getCode());
        log.setRelationTableId(relationTableId);
        log.setRemark(remark);
        operateLogService.insertByDubboAsync(log, loginUser);
    }
}
