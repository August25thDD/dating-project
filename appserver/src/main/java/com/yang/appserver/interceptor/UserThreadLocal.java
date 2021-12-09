package com.yang.appserver.interceptor;

import com.yang.commons.pojo.User;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-06 20:08
 **/

public class UserThreadLocal {
    private static final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    // 设置User到当前线程
    public static void set(User user) {
        threadLocal.set(user);
    }

    // 从当前线程获取User
    public static User get() {
        return threadLocal.get();
    }

    // 从当前线程删除User
    public static void remove() {
        threadLocal.remove();
    }

    // 获取User的id
    public static Long getUserId() {
        return threadLocal.get().getId();
    }

    // 获取User的手机号
    public static String getUserMobile() {
        return threadLocal.get().getMobile();
    }
}
