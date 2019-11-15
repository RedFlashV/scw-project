package com.atguigu.scw.project.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjectCommitVo extends BaseVo {
	@ApiModelProperty("项目之前的临时token")
	private String projectToken;// 项目的临时token
	@ApiModelProperty("商家支付宝账号")
	private String alipayAccount;// 项目的临时token
	@ApiModelProperty("项目发起者的身份证号")
	private String idCard;// 项目发起人的身份证号
}
