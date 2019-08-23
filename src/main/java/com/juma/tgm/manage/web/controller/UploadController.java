/**
 * 
 */
package com.juma.tgm.manage.web.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.common.storage.service.DistributedFileStorageService;

/**
 * @author vencent.lu
 *
 */
@Controller
public class UploadController {
	
	@Autowired
	private DistributedFileStorageService distributedFileStorageService;
	
	@RequestMapping(value = "upload")
	@ResponseBody
	public String upload(@RequestParam MultipartFile uploadPic,
						 LoginEmployee loginEmployee) throws IOException {
		return this.distributedFileStorageService.putInputBytes("upload/images",
				uploadPic.getOriginalFilename(), uploadPic.getBytes(),
				uploadPic.getContentType(), true);
	}
	
	@RequestMapping(value = "image/selector")
	public ModelAndView uploadModal() {
		ModelAndView modelAndView = new ModelAndView("inc/common/dialog/imageSelector");
		return modelAndView;
	}

}
