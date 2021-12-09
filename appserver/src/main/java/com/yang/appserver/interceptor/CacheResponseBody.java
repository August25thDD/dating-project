package com.yang.appserver.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.yang.appserver.config.MyCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.concurrent.TimeUnit;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.interceptor
 * @作者: 李云飞
 * @日期: 2021-12-07
 */

@ControllerAdvice
public class CacheResponseBody implements ResponseBodyAdvice {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 如果这个方法返回值是true 就会走下面的beforeBodyWrite这个方法
     * 如果这个方法返回值是false 就不会走下面的beforeBodyWrite这个方法
     *
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        // 必须是Get请求 必须有MyCache注解
        return methodParameter.hasMethodAnnotation(GetMapping.class) && methodParameter.hasMethodAnnotation(MyCache.class);
    }

    /**
     * 增强响应体
     *
     * @param body               响应体
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter,
                                  MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (ObjectUtil.isNull(body)) {
            return null;
        }

        // 把body存储到redis中
        // 把body转成JSON字符串
        // SpringMVC默认返回的就是一个JSON字符串
        String redisValue = null;
        if (body instanceof String) {
            redisValue = (String) body;
        } else {
            redisValue = JSONUtil.toJsonStr(body);
        }

        // Redis的Key
        String cacheKey = com.yang.appserver.interceptor.CacheInterceptor.getCacheKey(((ServletServerHttpRequest) serverHttpRequest).getServletRequest());

        // 拿到目标方法上的那个注解 然后获取它的缓存时间
        MyCache myCache = methodParameter.getMethodAnnotation(MyCache.class);
        // 把缓存数据存储到Redis中
        redisTemplate.opsForValue().set(cacheKey,redisValue,myCache.cacheTime(), TimeUnit.SECONDS);

        System.out.println("已经把响应体中的数据存储到了Redis");

        return body;
    }
}
