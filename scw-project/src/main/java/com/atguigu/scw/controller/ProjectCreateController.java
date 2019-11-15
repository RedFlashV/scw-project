package com.atguigu.scw.controller;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.common.utils.ScwAppUtils;
import com.atguigu.scw.common.vo.response.TReturn;
import com.atguigu.scw.common.vo.response.UserResponseVo;
import com.atguigu.scw.project.service.ProjectService;
import com.atguigu.scw.project.utils.OSSTemplate;
import com.atguigu.scw.project.vo.request.ProjectBaseInfoVo;
import com.atguigu.scw.project.vo.request.ProjectCommitVo;
import com.atguigu.scw.project.vo.request.ProjectRedisStorageVo;
import com.atguigu.scw.project.vo.request.ProjectReturnVo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/project")
public class ProjectCreateController {
	@Autowired
	OSSTemplate ossTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	ProjectService projectService;
	
	//发起项目：第3步，确认提交
	@ApiOperation("第三步，确认提交")
	@PostMapping("/initProjectStep3")
	public ResponseVo<Object> initProjectStep3(ProjectCommitVo vo){
		//验证登录
		String accessToken = vo.getAccessToken();
		if(StringUtils.isEmpty(accessToken)) {
			return ResponseVo.fail("请登录后再发布项目");
		}
		//获取redis中的bigVo
		ProjectRedisStorageVo bigVo = ScwAppUtils.getObjFromRedis(stringRedisTemplate, ProjectRedisStorageVo.class, vo.getProjectToken());
		//更新redis中缓存的bigVo对象
		BeanUtils.copyProperties(vo, bigVo);
		//调用业务层实现：将bigVo转为数据库表对应的javabean： 将bigVo拆成 每张表的类的实例
		projectService.createProject(bigVo);
		//删除redis中的缓存
		stringRedisTemplate.delete(vo.getProjectToken());
		//给出响应
		return ResponseVo.ok("项目创建成功");
	}
	
	@ApiOperation("第二步，收集回报信息")
	@PostMapping("/initProjectStep2")
	public ResponseVo<Object> initProjectStep2(@RequestBody List<ProjectReturnVo> vos) {
		//log.debug("回报列表：{}",vos);
		if (CollectionUtils.isEmpty(vos)) {
			return ResponseVo.fail("请创建回复信息");
		}
		ProjectReturnVo vo = vos.get(0);
		String accessToken = vo.getAccessToken();
		if (StringUtils.isEmpty(accessToken)) {
			return ResponseVo.fail("请登录后再发布项目");
		}
		String userResponseVoJsonStr = stringRedisTemplate.opsForValue().get(accessToken);
		if (StringUtils.isEmpty(userResponseVoJsonStr)) {
			return ResponseVo.fail("登录超时，请重新登录");
		}
		String token = vo.getProjectToken();
		ProjectRedisStorageVo bigVo = ScwAppUtils.getObjFromRedis(stringRedisTemplate, ProjectRedisStorageVo.class,token);
		ArrayList<TReturn> rtnList = new ArrayList<TReturn>();
		for (ProjectReturnVo projectReturnVo : vos) {
			TReturn rtn = new TReturn();
			BeanUtils.copyProperties(projectReturnVo, rtn);
			rtnList.add(rtn);
		}
		bigVo.setProjectReturns(rtnList);
		ScwAppUtils.saveObj2Redis(stringRedisTemplate, bigVo, vo.getProjectToken());
		return ResponseVo.ok(bigVo);
		
	}
	
	
	
	
	@ApiOperation("项目及发起人信息")
	@PostMapping("/initProjectStep1")
	public ResponseVo<Object> initProjectStep1(ProjectBaseInfoVo vo) {
		String accessToken = vo.getAccessToken();
		if (StringUtils.isEmpty(accessToken)) {
			return ResponseVo.fail("请登录后再发布项目");
		}
		String userResponseVoJsonStr = stringRedisTemplate.opsForValue().get(accessToken);
		if (StringUtils.isEmpty(userResponseVoJsonStr)) {
			return ResponseVo.fail("登录超时，请重新登录");
		}
		ProjectRedisStorageVo bigVo = ScwAppUtils.getObjFromRedis(stringRedisTemplate, ProjectRedisStorageVo.class, vo.getProjectToken());
		BeanUtils.copyProperties(vo, bigVo);
		ScwAppUtils.saveObj2Redis(stringRedisTemplate, bigVo, vo.getProjectToken());
		return ResponseVo.ok(bigVo);
	}
	
	@ApiOperation("阅读并同意协议")
	@PostMapping("/initProject")
	public ResponseVo<Object> initProject(String accessToken) {
		if (StringUtils.isEmpty(accessToken)) {
			return ResponseVo.fail("请登录后再发布项目");
		}
		String userResponseVoJsonStr = stringRedisTemplate.opsForValue().get(accessToken);
		if (StringUtils.isEmpty(userResponseVoJsonStr)) {
			return ResponseVo.fail("登录超时，请重新登录");
		}
		UserResponseVo userResponseVo = JSON.parseObject(userResponseVoJsonStr, UserResponseVo.class);
		ProjectRedisStorageVo bigVo = new ProjectRedisStorageVo();
		bigVo.setAccessToken(userResponseVo.getAccesstoken());
		bigVo.setMemberid(userResponseVo.getId());
		String projectToken="project:create:temp:"+UUID.randomUUID().toString().replace("-", "")+":token";
		bigVo.setAccessToken(projectToken);
		ScwAppUtils.saveObj2Redis(stringRedisTemplate, bigVo, projectToken);
		return ResponseVo.ok(bigVo);
	}

	@PostMapping("/uploadImgs")
	public ResponseVo<Object> uploadImgs(MultipartFile[] imgs) {
		if (ArrayUtils.isEmpty(imgs)) {
			return ResponseVo.fail("文件上传失败，请选择文件后上传");
		}
		List<String> imgPaths = new ArrayList<String>();
		int count = 0;
		for (MultipartFile file : imgs) {
			String imgPath = ossTemplate.upLoadImg(file);
			if (imgPath == null) {
				count++;
			} else {
				imgPaths.add(imgPath);
			}
		}
		log.debug("一共{}个文件，成功：{}个，失败{}个", imgs.length, (imgs.length - count), count);
		return ResponseVo.ok(imgPaths);
	}

}
