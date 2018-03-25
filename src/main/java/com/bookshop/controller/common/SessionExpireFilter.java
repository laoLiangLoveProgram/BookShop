package com.bookshop.controller.common;/**
 * Created by Administrator on 2018/3/25.
 */

import com.bookshop.common.Const;
import com.bookshop.pojo.User;
import com.bookshop.util.CookieUtil;
import com.bookshop.util.JsonUtil;
import com.bookshop.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @program: book_shop
 * @description: 过滤*.do请求, 实现更新数据库中session的有效时间的功能
 * @author: LaoLiang
 * @create: 2018-03-25 17:35
 **/
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String sessionId = CookieUtil.readLoginToken(httpServletRequest);

        //如果sessionId不为空或"", 则拿取user的Json字符串信息
        if (StringUtils.isNotEmpty(sessionId)) {
            String userJsonStr = RedisPoolUtil.get(sessionId);
            User user = JsonUtil.string2Obj(userJsonStr, User.class);

            //如果user不为空, 则重置session的有效时间
            if (user != null) {
                RedisPoolUtil.expire(sessionId, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        //继续执行过滤器链
        filterChain.doFilter(servletRequest, servletResponse);


    }

    @Override
    public void destroy() {

    }
}
