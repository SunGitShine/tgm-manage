/**
 * 
 */
package com.juma.tgm.manage.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.giants.common.lang.exception.CategoryCodeFormatException;
import com.juma.conf.domain.Region;
import com.juma.conf.service.RegionService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.version.service.VersionService;

/**
 * @author vencent.lu
 *
 * Create Date:2014年2月24日
 */
@Controller
public class ForwardController {

    private static final Logger log = LoggerFactory.getLogger(ForwardController.class);
    @Autowired
    private RegionService regionService;
    @Autowired
    private VersionService versionService;
    
	@RequestMapping(value="forward/**")
	public ModelAndView forward(HttpServletRequest request) {
		String servletPath = request.getServletPath().intern();
		return new ModelAndView(servletPath.replace("/forward/", "").replaceAll(
				"\\.html", ""));

	}

	@Deprecated
    @ResponseBody
    @RequestMapping(value = "region/{parentRegionId}/regionCode", method = RequestMethod.GET)
    public List<Region> getRegion(@PathVariable Integer parentRegionId) {
        if (parentRegionId == -1) {
            parentRegionId = null;
        }
        return regionService.findChildRegion(parentRegionId);
    }


    @ResponseBody
    @RequestMapping(value = "/region/{regionCode}")
    public List<Region> getChildRegions(@PathVariable String regionCode) {
        return regionService.findChildRegion(regionCode);
    }

    @ResponseBody
    @RequestMapping(value = "/region/level/{regionCode}")
    public List<Region> getLevelRegions(@PathVariable String regionCode) {
        try {
            return regionService.findAllLevelsRegion(regionCode);
        } catch (CategoryCodeFormatException e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }
    
    /** 运费规则默认使用城市 */
    @ResponseBody
    @RequestMapping(value = "default/regionCode", method = RequestMethod.GET)
    public String defaultRegionCode() {
        return versionService.findDefaultRegionCode(Constants.DEFAULT_FRIGHT_CODE_KEY);
    }

    /** 运费规则默认使用城市 */
    @ResponseBody
    @RequestMapping(value = "default/{regionCode}/addRegionCode", method = RequestMethod.GET)
    public void defaultAddRegionCode(@PathVariable String regionCode) {
        versionService.addDefaultRegionCode(Constants.DEFAULT_FRIGHT_CODE_KEY, regionCode);
    }

}
