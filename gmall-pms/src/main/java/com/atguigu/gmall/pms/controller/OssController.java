package com.atguigu.gmall.pms.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.gmall.common.bean.ResponseVo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName OssController
 * @Author 鳄鱼魜
 * @Date 2023/3/9 16:58
 * @Version 1.0
 * @Description 图片上传到阿里云OSS
 */

@RestController
@RequestMapping("pms/oss")
public class OssController {

    @GetMapping("policy")
    public ResponseVo<Object> queryObjectBy() {
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessId = "LTAI5tFCfUQy2JgwZTkbH9ea";
        String accessKey = "iseWdd6lMZMpbvtsUdsuYKPWXJyrk4";
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-shanghai.aliyuncs.com";
        // 填写Bucket名称，例如examplebucket。
        String bucket = "gmall-eyvren";
        // 填写Host地址，格式为https://bucketname.endpoint。
        String host = "https://" + bucket + "." + endpoint;
        // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
        Date date = new Date();
        // 以 yyyy-MM-dd 名称方式创建文件夹来存储
        String dir = new SimpleDateFormat("yyyy-MM-dd").format(date);

        // 创建ossClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

            return ResponseVo.ok(respMap);

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        }
        return ResponseVo.fail("获取签名失败！");

    }

}