package com.yang.appserver.controller;

import com.yang.appserver.service.HuanXinService;
import com.yang.appserver.service.HuanXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @项目名称: tanhua-53
 * @包名: com.yang.appserver.controller
 * @作者: 李云飞
 * @日期: 2021-12-09
 */

@RestController
@RequestMapping("huanxin")
public class HuanxinController {

    @Autowired
    private HuanXinService huanXinService;

    @GetMapping("user")
    public ResponseEntity queryHuanXinUser() {
        Map<String, String> huanXinUser = huanXinService.queryHuanXinUser();
        return ResponseEntity.ok(huanXinUser);
    }
}
