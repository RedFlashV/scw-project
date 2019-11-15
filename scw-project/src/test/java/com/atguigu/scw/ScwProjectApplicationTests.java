package com.atguigu.scw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.atguigu.scw.project.mapper.TProjectMapper;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ScwProjectApplicationTests {
	@Autowired
	TProjectMapper mapper;
	@Test
	public void contextLoads() throws Exception {
		long l = mapper.countByExample(null);
		System.out.println(l);
		/*// Endpoint以杭州为例，其它Region请按实际情况填写。
		String schme = "http://";
		String endpoint = "oss-cn-shanghai.aliyuncs.com";
		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
		String accessKeyId = "LTAI4FmQ9pPYSznx7Z8Hevhy";
		String accessKeySecret = "eJfFa86AlE5eBBviXok1iCKWlKadTR";

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(schme+endpoint, accessKeyId, accessKeySecret);

		// 上传文件流。
		InputStream inputStream = new FileInputStream(new File("C:\\Users\\lenovo\\Pictures\\Saved Pictures\\02.jpg"));
		String bucketname= "scw-y";
		String filename = "02.jpg";
		String foldname= "projectimgs/";
		ossClient.putObject(bucketname, foldname+filename ,inputStream);
		String path =   schme+bucketname+"."+endpoint+"/"+foldname+filename;
		// 关闭OSSClient。
		log.debug("上传图片的地址为：{}",path);
		ossClient.shutdown();*/
	}

}
