package com.yang.appserver.controller;

import com.yang.appserver.config.MyCache;
import com.yang.appserver.service.PublishService;
import com.yang.commons.pojo.Publish;
import com.yang.commons.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-08 15:59
 **/
@RestController
@RequestMapping("/movements")
public class PublishController {

    @Autowired
    private PublishService publishService;

    /**
     * 发布动态
     */
    @PostMapping
    public ResponseEntity movements(@RequestParam("textContent") String textContent,
                                    @RequestParam(value = "location", required = false) String location,
                                    @RequestParam(value = "latitude", required = false) String latitude,
                                    @RequestParam(value = "longitude", required = false) String longitude,
                                    @RequestParam(value = "imageContent", required = false) MultipartFile[] multipartFiles) throws IOException {
        publishService.publishMovement(textContent, location, latitude, longitude, multipartFiles);
        return ResponseEntity.ok(null);
    }


    /**
     * 查询好友动态
     */
    @GetMapping
    @MyCache(cacheTime = 45)
    public ResponseEntity movements(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = publishService.findFriendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询推荐动态
     */
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = publishService.findRecommendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    @GetMapping("visitors")
    public ResponseEntity visitors() {
        return ResponseEntity.ok(null);
    }

}
