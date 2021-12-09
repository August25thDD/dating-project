package com.yang.commons.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import java.io.IOException;
import java.util.Properties;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.commons.utils
 * @作者: 李云飞
 * @日期: 2021-12-04
 */
public class SendMsgUtil {

    private static String accessKey_id;
    private static String accessKey_secret;

    static {
        Properties properties = new Properties();
        try {
            properties.load(SendMsgUtil.class.getClassLoader().getResourceAsStream("aliyun.properties"));
            accessKey_secret = properties.getProperty("accessKey_secret");
            accessKey_id = properties.getProperty("accessKey_id");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 发送短信验证码
     * @param phoneNum 接收验证码的手机号
     * @param templateCode 登录短信模板编码
     * @param code 验证码
     * @return 发送成功返回 true 反之false
     */
    public static Boolean sendMsg(String phoneNum, String templateCode, String code) {

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKey_id, accessKey_secret);

        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("SignName", "杭州黑马传智健康");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        request.putQueryParameter("PhoneNumbers", phoneNum);
        try {
            // {"RequestId":"F8500296-EDD0-519D-9EFF-CAC4541A4C8D","Message":"OK","BizId":"460819338584527898^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            if (response.getData().contains("\"Message\":\"OK\"")) {
                return true;
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return false;
    }

}
