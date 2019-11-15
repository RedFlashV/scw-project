package com.atguigu.scw.project.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atguigu.scw.common.utils.AppDateUtils;
import com.atguigu.scw.common.vo.response.ProjectDetailsResponseVo;
import com.atguigu.scw.common.vo.response.TReturn;
import com.atguigu.scw.project.bean.TProject;
import com.atguigu.scw.project.bean.TProjectImages;
import com.atguigu.scw.project.bean.TProjectImagesExample;
import com.atguigu.scw.project.bean.TProjectInitiator;
import com.atguigu.scw.project.bean.TProjectInitiatorExample;
import com.atguigu.scw.project.bean.TProjectTag;
import com.atguigu.scw.project.bean.TProjectType;
import com.atguigu.scw.project.bean.TReturnExample;
import com.atguigu.scw.project.mapper.TProjectImagesMapper;
import com.atguigu.scw.project.mapper.TProjectInitiatorMapper;
import com.atguigu.scw.project.mapper.TProjectMapper;
import com.atguigu.scw.project.mapper.TProjectTagMapper;
import com.atguigu.scw.project.mapper.TProjectTypeMapper;
import com.atguigu.scw.project.mapper.TReturnMapper;
import com.atguigu.scw.project.service.ProjectService;
import com.atguigu.scw.project.vo.request.ProjectRedisStorageVo;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ProjectServiceimpl implements ProjectService{
	@Autowired
	TProjectMapper projectMapper;
	@Autowired
	TReturnMapper returnMapper;
	@Autowired
	TProjectTagMapper projectTagMapper;
	@Autowired
	TProjectTypeMapper projectTypeMapper;
	@Autowired
	TProjectInitiatorMapper projectInitiatorMapper;
	@Autowired
	TProjectImagesMapper projectImagesMapper;

	@Override
	public void createProject(ProjectRedisStorageVo bigVo) {
		
		//1、project:必须先保存project
		TProject project = new TProject();
		BeanUtils.copyProperties(bigVo, project);
		//手动初始化默认值
		project.setFollower(0);
		project.setSupporter(0);
		project.setSupportmoney(0L);
		project.setStatus(0+"");//0 - 即将开始， 1 - 众筹中， 2 - 众筹成功， 3 - 众筹失败
		project.setCreatedate(AppDateUtils.getFormatTime());
		project.setCompletion(0);//完成度
	/*	//将拷贝失败的数据 手动拷贝
		project.setMoney((long)bigVo.getMoney());*/
		
		log.debug("项目对象：{}", project);
		//调用mapper保存数据
		projectMapper.insertSelective(project);//保存其他数据时需要使用project 的id
		Integer projectId = project.getId();
		log.debug("保存后的项目对象：{}", project);
		//project_images  图片信息都存在一张表中，根据imgtype决定图片是头图还是详情图 0-头部图片 1-详情图片
		//2、保存图片：遍历将所有的图片信息封装为对应的TProjectImage对象，然后批量插入到数据库中
		List<TProjectImages> imgs = new ArrayList<TProjectImages>();
		//头图
		TProjectImages projectImages = new TProjectImages(null, projectId, bigVo.getHeaderImage(), (byte)0);
		imgs.add(projectImages);
		//详情图集合
		for (String tProjectImagesUrl : bigVo.getDetailsImage()) {
			projectImages = new TProjectImages(null, projectId, tProjectImagesUrl, (byte)1);
			imgs.add(projectImages);
		}
		//调用mapper的批量插入的方法
		projectImagesMapper.batchSaveProjectImgs(imgs);
		//3、保存项目发起人信息
		//project_initiator
		TProjectInitiator projectInitiator = bigVo.getProjectInitiator();
		projectInitiator.setProjectid(projectId);
		projectInitiatorMapper.insertSelective(projectInitiator);
		//project_tag
		//4、保存项目的标签   推荐批量插入
		for (Integer tagId : bigVo.getTagids()) {
			projectTagMapper.insertSelective(new TProjectTag(null, projectId, tagId));
		}
		//project_type
		//5、保存项目的分类
		for (Integer typeId : bigVo.getTypeids()) {
			projectTypeMapper.insertSelective(new TProjectType(null, projectId, typeId));
		}
		//return
		//6、保存回报
		for (TReturn rtn : bigVo.getProjectReturns()) {
			rtn.setProjectid(projectId);
			returnMapper.insertSelective(rtn);
		}
	}

	@Override
	public List<TProject> getAllProjects() {
		return projectMapper.selectByExample(null);
	}

	@Override
	public List<TProjectImages> getProjectImages(Integer id) {
		TProjectImagesExample example = new TProjectImagesExample();
		example.createCriteria().andProjectidEqualTo(id);
		return projectImagesMapper.selectByExample(example);
	}

	@Override
	public ProjectDetailsResponseVo getProjectDetails(Integer id) {
		ProjectDetailsResponseVo vo = new ProjectDetailsResponseVo();
		TProject project = projectMapper.selectByPrimaryKey(id);
		BeanUtils.copyProperties(project, vo);
		TProjectImagesExample example = new TProjectImagesExample();
		example.createCriteria().andProjectidEqualTo(id);
		List<TProjectImages> imgs = projectImagesMapper.selectByExample(example );
		for (TProjectImages img : imgs) {
			if(img.getImgtype()==0) {
				vo.setHeaderImage(img.getImgurl());
			}else {
				vo.getDetailsImage().add(img.getImgurl());
			}
		}

		TProjectInitiatorExample example2 = new TProjectInitiatorExample();
		example2.createCriteria().andProjectidEqualTo(id);
		List<TProjectInitiator> list = projectInitiatorMapper.selectByExample(example2);
		if(!CollectionUtils.isEmpty(list)) {
			TProjectInitiator projectInitiator = list.get(0);
			com.atguigu.scw.common.vo.response.TProjectInitiator voprojectInitiator = new com.atguigu.scw.common.vo.response.TProjectInitiator();
			BeanUtils.copyProperties(projectInitiator, voprojectInitiator);
			vo.setProjectInitiator(voprojectInitiator);
		}

		TReturnExample example3 = new TReturnExample();
		example3.createCriteria().andProjectidEqualTo(id);
		List<TReturn> returns = returnMapper.selectByExample(example3);
		vo.setReturns(returns);
		return vo;
	}

}
