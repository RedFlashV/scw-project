package com.atguigu.scw.project.vo.request;
 
import java.util.List;

import com.atguigu.scw.common.vo.response.TReturn;
import com.atguigu.scw.project.bean.TProjectInitiator;

import lombok.Data;
 
@Data
public class ProjectRedisStorageVo{ 
	private String accessToken;//用户成功登录的token 验证是否登录和获取信息
     //全量   增量    
     private String projectToken;//项目的临时token 
     private Integer memberid;//会员id 
     private List<Integer> typeids; //项目的分类id 
     private List<Integer> tagids; //项目的标签id 
     private String name;//项目名称 
     private String remark;//项目简介 
     private Integer money;//筹资金额 
     private Integer day;//筹资天数 
     private String headerImage;//项目头部图片 
     private List<String> detailsImage;//项目详情图片 
     private List<TReturn> projectReturns;//项目回报 
     //发起人信息：自我介绍，详细自我介绍，联系电话，客服电话
     private TProjectInitiator projectInitiator;//项目发起人信息
     private String  alipayAccount;//项目发起人支付宝账号
     private String idCard;//项目发起人身份证号
     
}
