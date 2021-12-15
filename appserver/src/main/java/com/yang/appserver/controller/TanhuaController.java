package com.yang.appserver.controller;

import com.yang.appserver.service.TanhuaService;
import com.yang.commons.vo.PageResult;
import com.yang.commons.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.controller
 * @作者: 李云飞
 * @日期: 2021-12-06
 * <p>
 * 探花控制器
 * 请求tanhua开头的所有请求
 */
@RestController
@RequestMapping("tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    /**
     * 首页今日佳人功能
     * @return
     */
    @GetMapping("todayBest")
    public ResponseEntity todayBest() {
        TodayBest todayBest = tanhuaService.todayBest();
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 首页推荐列表功能
     * @param params
     * @return
     */
    @GetMapping("recommendation")
    public ResponseEntity recommendation(@RequestParam Map<String, Object> params) {
        PageResult pageResult = tanhuaService.recommendationList(params);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询个人主页的个人信息
     *
     * @param userId
     * @return
     */
    @GetMapping("{id}/personalInfo")
    public ResponseEntity<TodayBest> queryUserInfo(@PathVariable("id") Long userId) {
        TodayBest todayBest = tanhuaService.queryUserInfo(userId);
        return ResponseEntity.ok(todayBest);
    }
}
