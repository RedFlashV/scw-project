package com.atguigu.scw.project.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class OSSTemplate {
	// Endpoint以杭州为例，其它Region请按实际情况填写。
	String schme;
	String endpoint;
	// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录
	// https://ram.console.aliyun.com 创建。
	String accessKeyId;
	String accessKeySecret;
	String foldname;
	String bucketname;

	public String upLoadImg(MultipartFile file) {

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(schme + endpoint, accessKeyId, accessKeySecret);

		// 上传文件流。
		InputStream inputStream = null;
		String path = null;
		try {
			inputStream = file.getInputStream();
			String filename = System.currentTimeMillis()+"_"+UUID.randomUUID().toString().replace("-", "")+"_"+file.getOriginalFilename();
			ossClient.putObject(bucketname, foldname + filename, inputStream);
			path = schme + bucketname + "." + endpoint + "/" + foldname + filename;
			// 关闭OSSClient。
			log.debug("上传图片的地址为：{}", path);
			ossClient.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

}
