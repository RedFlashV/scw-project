package com.atguigu.scw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.common.vo.response.ProjectDetailsResponseVo;
import com.atguigu.scw.common.vo.response.ProjectResponseVo;
import com.atguigu.scw.project.bean.TProject;
import com.atguigu.scw.project.bean.TProjectImages;
import com.atguigu.scw.project.service.ProjectService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class ProjectInfoController {
	@Autowired
	ProjectService projectInfoService;

	@ApiOperation("查询项目详情的方法")
	@GetMapping("/getProjectDetails")
	public ResponseVo<ProjectDetailsResponseVo> getProjectDetails(@RequestParam("id")Integer id) {
	ProjectDetailsResponseVo vo	=projectInfoService. getProjectDetails(id);
	log.info("查询到的项目详情vo:{}",vo);
	return ResponseVo.ok(vo);
	}

	@ApiOperation("查询项目列表的方法")
	@GetMapping("/getAlllProjects")
	public ResponseVo<List<ProjectResponseVo>> getAllProjects() {
		List<ProjectResponseVo> prosVo = new ArrayList<>();
		List<TProject> pros = projectInfoService.getAllProjects();

		for (TProject tProject : pros) {
			Integer id = tProject.getId();
			List<TProjectImages> images = projectInfoService.getProjectImages(id);
			ProjectResponseVo projectVo = new ProjectResponseVo();
			BeanUtils.copyProperties(tProject, projectVo);

			for (TProjectImages tProjectImages : images) {
				if (tProjectImages.getImgtype() == 0) {
					projectVo.setHeaderImage(tProjectImages.getImgurl());
				} else {
					List<String> detailsImage = projectVo.getDetailsImage();
					detailsImage.add(tProjectImages.getImgurl());
				}
			}
			prosVo.add(projectVo);
		}
		return ResponseVo.ok(prosVo);
	}

}
