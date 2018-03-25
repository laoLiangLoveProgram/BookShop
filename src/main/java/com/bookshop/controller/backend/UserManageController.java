package com.bookshop.controller.backend;

import com.bookshop.common.Const;
import com.bookshop.common.ServerResponse;
import com.bookshop.pojo.User;
import com.bookshop.service.IUserService;
import com.bookshop.util.CookieUtil;
import com.bookshop.util.JsonUtil;
import com.bookshop.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/5/15.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    //后台管理员登录
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (Const.Role.ROLE_ADMIN.equals(user.getRole())) {
                //说明登录的是管理员

                CookieUtil.writeLoginToken(httpServletResponse, session.getId());

                //setEx(key, value, exTime), 设置带有剩余时间的session
                RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

                return response;
            } else {
                //不是管理员
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }

        return response;
    }
}
