package com.yang.commons.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.commons.utils
 * @作者: 李云飞
 * @日期: 2021-12-04
 */
public class UploadPicUtil {

    private static String accessKey_id;
    private static String accessKey_secret;
    private static String endpoint;
    private static String bucketname;
    private static String picurl;


    static {
        Properties properties = new Properties();
        try {
            properties.load(UploadPicUtil.class.getClassLoader().getResourceAsStream("aliyun.properties"));
            accessKey_secret = properties.getProperty("oss.accessKey_secret");
            accessKey_id = properties.getProperty("oss.accessKey_id");
            endpoint = properties.getProperty("oss.endpoint");
            bucketname = properties.getProperty("oss.bucketname");
            picurl = properties.getProperty("oss.picurl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传用户注册图片到阿里云的OOS对象存储空间
     * @param imageData
     * @param fileSuffixName
     * @return 返回上传成功后的图片地址
     */
    public static String uploadPicture(byte[] imageData, String fileSuffixName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey_id, accessKey_secret);
        // 我们在进行存储文件的时候 不能把所有的文件都存储到同一个目录中
        // 对上传目录进行分离 目录分离算法 根据一个文件名称 --> 自动生成目录层级
        String filePath = getFilePath(fileSuffixName);
        PutObjectResult result = ossClient.putObject(bucketname, filePath, new ByteArrayInputStream(imageData));

        // https://hztanhua.oss-cn-hangzhou.aliyuncs.com/images/2021/12/04/06a5a91e2cd941539605b41aacc7d1c6.jpg
        ossClient.shutdown();
        // 返回上传成功后的图片地址
        return picurl + filePath;
    }

    /**
     *
     * @param fileSuffixName
     * @return
     */
    public static String getFilePath(String fileSuffixName) {
        DateTime dateTime = new DateTime();
        return "images/" + dateTime.toString("yyyy") + "/"
                + dateTime.toString("MM") + "/" + dateTime.toString("dd") + "/"
                + UUID.fastUUID().toString(true) + "." + fileSuffixName;
    }

}
