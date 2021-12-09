package com.yang.appserver.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;

import com.yang.appserver.config.MyCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.interceptor
 * @作者: 李云飞
 * @日期: 2021-12-06
 * <p>
 * 统一处理Token的拦截器
 */

@Component
public class CacheInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 请求到达目标资源之前
     * 请求到达控制器之前 可以去增强Request
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        // 如果不是Get请求 就直接放行
        if (!((HandlerMethod) handler).hasMethodAnnotation(GetMapping.class)) {
            return true;
        }

        if (!((HandlerMethod) handler).hasMethodAnnotation(MyCache.class)) {
            return true;
        }

        // 从Redis中获取缓存数据
        String cacheData = redisTemplate.opsForValue().get(getCacheKey(request));
        if (StrUtil.isBlank(cacheData)) {
            // 该请求还没有缓存 直接放行
            return true;
        }

        System.out.println("数据是从缓存中取走了....");

        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(cacheData);
        return false;
    }

    public static String getCacheKey(HttpServletRequest request) {
        // 获取请求地址
        String requestURI = request.getRequestURI();
        // 获取请求参数
        Map<String, String[]> map = request.getParameterMap();
        // 获取请求的Token
        String token = request.getHeader("Authorization");
        return SecureUtil.md5(requestURI + "_" + JSONUtil.toJsonStr(map) + "_" + token);
    }

}
