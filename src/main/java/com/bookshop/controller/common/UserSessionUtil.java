package com.bookshop.controller.common;/**
 * Created by Administrator on 2018/3/25.
 */

import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.pojo.User;
import com.bookshop.util.CookieUtil;
import com.bookshop.util.JsonUtil;
import com.bookshop.util.RedisPoolUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: book_shop
 * @description: 抽取出的从session中获取用户对象的公共方法
 * @author: LaoLiang
 * @create: 2018-03-25 19:45
 **/
public class UserSessionUtil {

    public static ServerResponse getUserFromSession(HttpServletRequest request, String errorMsg){
        String sessionId = CookieUtil.readLoginToken(request);
        if (sessionId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), errorMsg);
        }
        String userJsonStr = RedisPoolUtil.get(sessionId);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), errorMsg);
        }

        return ServerResponse.createBySuccess(user);
    }
}
