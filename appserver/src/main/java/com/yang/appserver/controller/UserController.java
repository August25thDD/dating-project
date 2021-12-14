package com.yang.appserver.controller;

import com.yang.appserver.config.NoAuthorization;
import com.yang.appserver.service.UserInfoService;
import com.yang.appserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 22:06
 **/

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 根据手机号码发短信
     * @param params
     * @return
     */
    @PostMapping("login")
    @NoAuthorization
    public ResponseEntity userLogin(@RequestBody Map<String,String> params) {
        String phone = params.get("phone");
        userService.sendLoginMsg(phone);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 校验验证码
     * @param params
     * @return
     */
    @PostMapping("loginVerification")
    @NoAuthorization
    public ResponseEntity userloginVerification(@RequestBody Map<String,String> params) {

        Map map = userService.loginVerification(params.get("phone"), params.get("verificationCode"));
        return  ResponseEntity.ok(map);
    }


    /**
     * 完善个人信息
     * @param token
     * @param params
     * @return
     */
    @PostMapping("loginReginfo")
    public ResponseEntity loginReginfo(@RequestHeader("Authorization") String token,
                                       @RequestBody Map<String, String> params) {
        userInfoService.loginReginfo(token,params);
        return ResponseEntity.ok(null);
    }

    /**
     * 头像上传/头像修改
     * @param token
     * @param headPhoto
     * @return
     */
    @PostMapping("loginReginfo/head")
    public ResponseEntity loginReginfoHead(@RequestHeader("Authorization") String token,
                                           @RequestParam("headPhoto") MultipartFile headPhoto) {
        userInfoService.loginReginfoHead(token,headPhoto);
        return ResponseEntity.ok(null);
    }
}
