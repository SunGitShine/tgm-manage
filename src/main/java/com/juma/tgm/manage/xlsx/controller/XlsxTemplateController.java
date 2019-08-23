package com.juma.tgm.manage.xlsx.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.giants.common.exception.BusinessException;
import com.juma.tgm.manage.xlsx.controller.vo.TitleFieldMappingVo;
import com.juma.tgm.manage.xlsx.controller.vo.XlsxTitleFieldMappingVo;
import com.juma.tgm.waybillReport.domain.OfflineWaybill;
import com.juma.tgm.xlsx.domain.XlsxTemplate;
import com.juma.tgm.xlsx.domain.XlsxTitleFieldMapping;
import com.juma.tgm.xlsx.service.XlsxTemplateService;

import me.about.poi.ExcelColumn;
import me.about.poi.reader.XlsxReadTitle;

@Controller
@RequestMapping("xlsx")
public class XlsxTemplateController {

    @Resource
    private XlsxTemplateService xlsxTemplateService;

    private List<TitleFieldMappingVo> titleFieldMapping() {
        List<TitleFieldMappingVo> mapping = new ArrayList<TitleFieldMappingVo>();
        Field[] fields = OfflineWaybill.class.getDeclaredFields();
        for (Field field : fields) {
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann != null) {
                TitleFieldMappingVo m = new TitleFieldMappingVo();
                m.setTitle(ann.name());
                m.setField(field.getName());
                m.setRequired(ann.required());
                mapping.add(m);
            }
        }
        Collections.sort(mapping, new Comparator<TitleFieldMappingVo>() {
            @Override
            public int compare(TitleFieldMappingVo o1, TitleFieldMappingVo o2) {
                int i1 = o1.isRequired() ? 1 : 0;
                int i2 = o2.isRequired() ? 1 : 0;
                return i2 - i1;
            }
        });
        return mapping;
    }
    
    
    @ResponseBody
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public List<String> xlsxTitle(@RequestParam(required = false) MultipartFile uploadXlsx, Integer startDataIndex)
            throws IOException, Exception {
        if (startDataIndex == null) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (uploadXlsx == null || uploadXlsx.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXlsx.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }
        return XlsxReadTitle.fromInputStream(uploadXlsx.getInputStream(), startDataIndex);
    }
    
    @RequestMapping("template/setting")
    public ModelAndView settingTemplate(Model model) {
        ModelAndView modeAndView = new ModelAndView("pages/xlsx/template");
        List<XlsxTemplate> templates = xlsxTemplateService.findAllTemplate();
        
        modeAndView.addObject("templates", templates);
        modeAndView.addObject("mappings", titleFieldMapping());
        return modeAndView;
    }
    
    @ResponseBody
    @RequestMapping("templates")
    public List<XlsxTemplate> templates() {
        List<XlsxTemplate> templates = xlsxTemplateService.findAllTemplate();
        return templates;
    }
    
    @ResponseBody
    @RequestMapping("template/{templateId}/load")
    public Map<String,Object> loadTemplate(@PathVariable Integer templateId) {
        Map<String,Object> out = new HashMap<String,Object>();
        List<String> titles = new ArrayList<String>();
        List<String> fields = new ArrayList<String>();
        List<XlsxTitleFieldMapping> fieldMappings = xlsxTemplateService.findTitleFieldMapping(templateId);
        for(XlsxTitleFieldMapping fileMapping : fieldMappings ) {
            titles.add(fileMapping.getTitle());
            fields.add(fileMapping.getField());
        }
        out.put("mappings", titleFieldMapping());
        out.put("titles", titles);
        out.put("fields", fields);
        return out;
    }
    
    @ResponseBody
    @RequestMapping("template/save")
    public void saveTemplate(@RequestBody XlsxTemplate template) {
        xlsxTemplateService.saveTemplate(template);
    }

    @ResponseBody
    @RequestMapping("mapping/save")
    public void saveMapping(@RequestBody XlsxTitleFieldMapping mapping) {
        xlsxTemplateService.saveMapping(mapping);
    }
    
    @ResponseBody
    @RequestMapping("mappings/save")
    public void saveMappings(@RequestBody XlsxTitleFieldMappingVo mappingsVo) {
        xlsxTemplateService.batchSaveMapping(mappingsVo.getMappings());
    }

    @ResponseBody
    @RequestMapping("template/{templateId}/remove")
    public void deleteTemplate(@PathVariable Integer templateId) {
        xlsxTemplateService.deleteTemplate(templateId);
    }

    @ResponseBody
    @RequestMapping("mapping/{mappingId}/remove")
    public void deleteMapping(@PathVariable Integer mappingId) {
        xlsxTemplateService.deleteMapping(mappingId);
    }

    
    public static void main(String[] args) {
        List<TitleFieldMappingVo> mapping = new ArrayList<TitleFieldMappingVo>();
        Field[] fields = OfflineWaybill.class.getDeclaredFields();
        for (Field field : fields) {
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            if (ann != null) {
                TitleFieldMappingVo m = new TitleFieldMappingVo();
                m.setTitle(ann.name());
                m.setField(field.getName());
                m.setRequired(ann.required());
                mapping.add(m);
            }
        }
        Collections.sort(mapping, new Comparator<TitleFieldMappingVo>() {
            @Override
            public int compare(TitleFieldMappingVo o1, TitleFieldMappingVo o2) {
                int i1 = o1.isRequired() ? 1 : 0;
                int i2 = o2.isRequired() ? 1 : 0;
                return i2 - i1;
            }
        });
        
        System.out.println(mapping);
    }
    
}
