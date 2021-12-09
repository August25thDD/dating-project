package com.yang.appserver.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.yang.appserver.config.NoAuthorization;
import com.yang.commons.constants.Constants;
import com.yang.commons.pojo.User;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-06 20:08
 **/
@Component
public class TokenInterceptor implements HandlerInterceptor {

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

        // 请求的资源必须是Controller中的方法
        // 如果不是Controller中的方法就直接放行

        if (!(handler instanceof HandlerMethod)){
            return true;
        }
//        Token 的解析
        if (((HandlerMethod) handler).hasMethodAnnotation(NoAuthorization.class)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }

//        校验token是否有效
        JWT jwt = JWTUtil.parseToken(token);
        jwt.setKey(Constants.JWT_SECRET.getBytes());

        if (!jwt.verify() || !jwt.validate(0)) {
            response.setStatus(401);
            return false;
        }
//        提取token中uid  phoneNum
        // 提取Token中的uid  phoneNum
        Object uid = jwt.getPayload("uid");
        Object phoneNum = jwt.getPayload("phoneNum");

        User user = User.builder().id(Convert.toLong(uid)).mobile(Convert.toStr(phoneNum)).build();
        UserThreadLocal.set(user);
        return true;

    }

    /**
     * 请求到达目标资源之后 返回给客户端之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 请求到达目标资源之后 返回给客户端之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
