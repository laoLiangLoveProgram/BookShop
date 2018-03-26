package com.bookshop.util;/**
 * Created by Administrator on 2018/3/25.
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: book_shop
 * @description:
 * @author: LaoLiang
 * @create: 2018-03-25 06:47
 **/
@Slf4j
public class CookieUtil {
    //cookie写在一级域名下, 二级三级可以读到
    private final static String COOKIE_DOMAIN = ".bookshop.com";
    private final static String COOKIE_NAME = "bookshop_login_token";

    /**
     * domain和path的关系 :
     * X: domain=".bookshop.com"   a,b,c,d,e都可以拿到X的cookie
     * a : A.bookshop.com               cookie: domain=A.bookshop.com; path="/"     a拿不到b的cookie
     * b : B.bookshop.com               cookie: domain=B.bookshop.com; path="/"     b拿不到a的cookie
     * c : A.bookshop.com/test/cc       cookie: domain=A.bookshop.com; path="/test/cc"      c,d共享a,e的cookie
     * d : A.bookshop.com/test/dd       cookie: domain=A.bookshop.com; path="/test/dd"      c,d直接拿不到对方的, 也拿不到b的
     * e : A.bookshop.com/test          cookie: domain=A.bookshop.com; path="/test"         e能拿到a, X的
     * 写入Cookie
     *
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);

        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");    //斜杠代表设置在根目录, 只有在此目录下的子目录的代码才能获取到这个cookie
        cookie.setHttpOnly(true);   //可以防止脚本访问cookie, 浏览器也不会把这个信息发送给任何第三方, 保障了安全性
        //如果maxAge不设置, cookie就不会写入硬盘, 而是写在内存, 只在当前页面有效
        cookie.setMaxAge(60 * 60 * 24 * 365);   //cookie的有效期, -1代表永久, 单位是秒

        log.info("write cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());

        response.addCookie(cookie);
    }

    /**
     * 读取Cookie
     */
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("read cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("return cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 注销的时候, 删除Cookie
     */
    public static void deleteLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);    //把有效期设置为0, 代表删除Cookie
                    log.info("delete cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }

    }

}
