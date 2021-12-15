package com.yang.appserver.exception;

import lombok.Builder;
import lombok.Data;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.excepiton
 * @作者: 李云飞
 * @日期: 2021-12-03
 * <p>
 * 给客户端返回的错误状态码
 */

@Data
@Builder
public class ErrorResult {
    private String errCode;
    private String errMessage;

    public static ErrorResult error() {
        return ErrorResult.builder().errCode("999999").errMessage("未知错误,请稍后重试").build();
    }

    public static ErrorResult phoneNumError() {
        return ErrorResult.builder().errCode("100001").errMessage("手机号码格式不正确").build();
    }

    public static ErrorResult sendCodeError() {
        return ErrorResult.builder().errCode("100002").errMessage("之前发送的验证码还未失效").build();
    }

    public static ErrorResult verificationCodeError() {
        return ErrorResult.builder().errCode("100003").errMessage("验证码错误").build();
    }

    public static ErrorResult sendMSGError() {
        return ErrorResult.builder().errCode("100004").errMessage("发送短信失败").build();
    }

    public static ErrorResult tokenValid() {
        return ErrorResult.builder().errCode("100005").errMessage("Token是无效的").build();
    }

    public static ErrorResult tokenTimeOut() {
        return ErrorResult.builder().errCode("100006").errMessage("Token已经过期").build();
    }

    public static ErrorResult paramesIsEmpty() {
        return ErrorResult.builder().errCode("100006").errMessage("参数为空").build();
    }

    public static ErrorResult photoFormartError() {
        return ErrorResult.builder().errCode("100007").errMessage("图片格式不对").build();
    }

    public static ErrorResult photoSizeError() {
        return ErrorResult.builder().errCode("100008").errMessage("图片大小不对").build();
    }

    public static ErrorResult recommendUserIsEmpty() {
        return ErrorResult.builder().errCode("200001").errMessage("今日佳人为空").build();
    }

    public static ErrorResult contentError() {
        return ErrorResult.builder().errCode("300001").errMessage("发布圈子内容不能为空").build();
    }

    public static ErrorResult smallVideoUploadError() {
        return ErrorResult.builder().errCode("400001").errMessage("上传小视频不能为空").build();
    }

    public static ErrorResult userRegitError() {
        return ErrorResult.builder().errCode("100009").errMessage("用户注册失败, 请重试").build();
    }

    public static ErrorResult sendHXMsgError() {
        return ErrorResult.builder().errCode("500001").errMessage("发送环信消息失败,请稍后重试").build();
    }

    public static ErrorResult addFriendError() {
        return ErrorResult.builder().errCode("500002").errMessage("添加好友失败").build();
    }
}
