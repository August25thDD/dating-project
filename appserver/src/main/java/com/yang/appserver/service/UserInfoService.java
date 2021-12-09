package com.yang.appserver.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.dubbo.config.annotation.Reference;

import com.yang.appserver.exception.ErrorResult;
import com.yang.appserver.exception.MyException;
import com.yang.commons.constants.Constants;
import com.yang.commons.pojo.UserInfo;
import com.yang.commons.utils.FaceDetect;
import com.yang.commons.utils.UploadPicUtil;
import com.yang.dubbo.interfaces.UserInfoApi;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.service
 * @作者: 李云飞
 * @日期: 2021-12-04
 */
@Service
public class UserInfoService {
    @Reference
    private UserInfoApi userInfoAPI;

    public void loginReginfo(String token, Map<String, String> params) {
        // 先校验Token是否是有效的
        // 校验Token是否正确

        JWT jwt = JWTUtil.parseToken(token);
        jwt.setKey(Constants.JWT_SECRET.getBytes());

        // true代表是有效 正确
        try {
            if (!jwt.verify()) {
                throw new MyException(ErrorResult.tokenValid());
            }
        } catch (MyException myException) {
            throw new MyException(ErrorResult.tokenValid());
        }

        // 校验Token是否已经失效
        // true 有效期内
        if (!jwt.validate(0)) {
            throw new MyException(ErrorResult.tokenTimeOut());
        }


        // 判断参数是否合法
        // 四个参数必须都不为空
        if (StrUtil.hasBlank(params.get("gender"), params.get("nickname"), params.get("birthday"), params.get("city"))) {
            throw new MyException(ErrorResult.paramesIsEmpty());
        }

        // 如果都不为空 调用Dubbo服务插入到数据库中 UserInfo
        userInfoAPI.addUserInfo(UserInfo.builder()
                .userId(Convert.toLong(jwt.getPayload("uid")))
                .nickName(params.get("nickname"))
                .birthday(params.get("birthday"))
                .city(params.get("city"))
                .sex(params.get("gender").equalsIgnoreCase("man") ? 1 : 2)
                .build());
    }

    /**
     * 上传用户头像
     *
     * @param token
     * @param headPhoto
     */
    public void loginReginfoHead(String token, MultipartFile headPhoto) {
        // 先校验Token是否是有效的
        // 校验Token是否正确
        JWT jwt = JWTUtil.parseToken(token);
        jwt.setKey(Constants.JWT_SECRET.getBytes());

        // true代表是有效 正确
        try {
            if (!jwt.verify()) {
                throw new MyException(ErrorResult.tokenValid());
            }
        } catch (MyException myException) {
            throw new MyException(ErrorResult.tokenValid());
        }

        // 校验Token是否已经失效
        // true 有效期内
        if (!jwt.validate(0)) {
            throw new MyException(ErrorResult.tokenTimeOut());
        }

        // 校验文件的格式
        // png  jpg jpeg  abcpng
        if (!StrUtil.endWithAnyIgnoreCase(headPhoto.getOriginalFilename(), ".png", ".jpg", ".jpeg")) {
            throw new MyException(ErrorResult.photoFormartError());
        }
        // 校验文件的大小
        if ((headPhoto.getSize() / 1024 / 1024) >= 2) {
            throw new MyException(ErrorResult.photoSizeError());
        }


        // 校验人脸
        try {
            if (!FaceDetect.detectFace(headPhoto.getBytes())) {
                throw new MyException(ErrorResult.photoDetectFaceError());
            }
            // 上传到阿里云OSS  返回地址
            String pictureURL = UploadPicUtil.uploadPicture(headPhoto.getBytes(),
                    StrUtil.subAfter(headPhoto.getOriginalFilename(), ".", true));
            // 把地址更新到MYSQL数据库
            userInfoAPI.updataLog(Convert.toLong(jwt.getPayload("uid")),pictureURL);
        } catch (IOException e) {
            throw new MyException(ErrorResult.photoFormartError());
        }
    }
}
