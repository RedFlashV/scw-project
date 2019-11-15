package com.atguigu.scw.project.service;

import java.util.List;

import com.atguigu.scw.common.vo.response.ProjectDetailsResponseVo;
import com.atguigu.scw.project.bean.TProject;
import com.atguigu.scw.project.bean.TProjectImages;
import com.atguigu.scw.project.vo.request.ProjectRedisStorageVo;

public interface ProjectService {

	void createProject(ProjectRedisStorageVo bigVo);

	List<TProject> getAllProjects();

	List<TProjectImages> getProjectImages(Integer id);

	ProjectDetailsResponseVo getProjectDetails(Integer id);
	

}
