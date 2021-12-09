package com.yang.commons.utils;

import cn.hutool.core.codec.Base64;
import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.commons.utils
 * @作者: 李云飞
 * @日期: 2021-12-03
 * <p>
 * 百度SDK完成人脸识别功能
 */
public class FaceDetect {
    public static String APP_ID;
    public static String API_KEY;
    public static String SECRET_KEY;

    /**
     * 读取配置文件 初始化参数
     */
    static {
        Properties properties = new Properties();
        try {
            properties.load(FaceDetect.class.getClassLoader().getResourceAsStream("baiduapi.properties"));
            APP_ID = properties.getProperty("BAIDU_APP_ID");
            API_KEY = properties.getProperty("BAIDU_API_KEY");
            SECRET_KEY = properties.getProperty("BAIDU_SECRET_KEY");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 人脸识别功能接口
     *
     * @param imageBytes 图片数据
     * @return true 代表图片中有人脸
     */
    public static Boolean detectFace(byte[] imageBytes) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 人脸检测
        JSONObject res = client.detect(Base64.encode(imageBytes), "BASE64", null);
        return res.getString("error_msg").equalsIgnoreCase("SUCCESS");
    }
}