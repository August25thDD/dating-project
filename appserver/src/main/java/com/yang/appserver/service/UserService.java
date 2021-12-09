package com.yang.appserver.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.yang.appserver.config.NoAuthorization;
import com.yang.appserver.exception.ErrorResult;
import com.yang.appserver.exception.MyException;
import com.yang.commons.constants.Constants;

import com.yang.commons.pojo.User;
import com.yang.dubbo.interfaces.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 22:08
 **/

@Component
@Service
public class UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Reference
    private UserApi userAPI;


    public void sendLoginMsg(String phone) {
        // 判断这个手机号码是否是合法的
        // 如果不合法直接返回错误就可以了
        if (!Validator.isMobile(phone)) {
            // 抛自定义异常
            throw new MyException(ErrorResult.phoneNumError());
        }

        // PC端的JS校验 APP端的校验 都是不靠谱的
        // 对于我们后台来说  不要把希望寄托与任何人 一定要自己亲自校验

        // 判断这个人之前是否已经发送过验证码 还没有失效  5
        String code = redisTemplate.opsForValue().get(Constants.SMS_CODE + phone);
        if (!StrUtil.isBlank(code)) {
            // 之前发送过验证码
            throw new MyException(ErrorResult.sendCodeError());
        }

        // 如果是合法的并且之前没有发送过验证码才去发验证码
        // 发送验证码
        // 调用阿里云的平台去发送
        // 生成一个随机的验证码
        /*String randomCode = RandomUtil.randomNumbers(6);
        Boolean result = SendMsg.sendMsgUtil(phone, Constants.SMS_CODE_TEMPLATE, randomCode);
        if (!result) {
            // 之前发送过验证码
            throw new MyException(ErrorResult.sendMSGError());
        }*/

        String randomCode = "123456";
        // 把验证码存储到redis中
        redisTemplate.opsForValue().set(Constants.SMS_CODE + phone, randomCode, 20, TimeUnit.MINUTES);
    }

    public Map loginVerification(String phone, String verificationCode) {
        // 先要判断手机号码是否合法
        if (!Validator.isMobile(phone)) {
            // 抛自定义异常
            throw new MyException(ErrorResult.phoneNumError());
        }
        // 判断验证码是否一致
        String code = redisTemplate.opsForValue().get(Constants.SMS_CODE + phone);
        if (null == code || null == verificationCode || !code.equals(verificationCode)) {
            throw new MyException(ErrorResult.verificationCodeError());
        }

        // 删除验证码
        redisTemplate.delete(Constants.SMS_CODE + phone);

        // 判断用户是否新用户
        // 如果是新用户  执行新增用户的动作
        User user = userAPI.findUserByPhoneNum(phone);
        HashMap<String, Object> hashMap = new HashMap<>();
        // 默认是老用户
        hashMap.put("isNew", false);
        if (ObjectUtil.isNull(user)) {
            // 新用户
            user = userAPI.addUser(User.builder()
                    .mobile(phone).password(SecureUtil.md5(Constants.INIT_PASSWORD))
                    .created(new Date())
                    .updated(new Date())
                    .build());
            hashMap.put("isNew", true);
        }

        // 生成Token
        Map<String, Object> map = new HashMap<>();
        map.put("uid", user.getId());
        map.put("phoneNum", user.getMobile());
        map.put(JWTPayload.EXPIRES_AT, DateTime.of(System.currentTimeMillis() + Constants.JWT_TIME_OUT));
        String token = JWTUtil.createToken(map, Constants.JWT_SECRET.getBytes());

        hashMap.put("token", token);
        return hashMap;
    }

}
