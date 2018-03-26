package com.bookshop.controller.portal;/**
 * Created by Administrator on 2018/3/26.
 */

import com.bookshop.common.Const;
import com.bookshop.common.ServerResponse;
import com.bookshop.pojo.User;
import com.bookshop.service.IUserService;
import com.bookshop.util.CookieUtil;
import com.bookshop.util.JsonUtil;
import com.bookshop.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @program: book_shop
 * @description: 测试springMVC全局异常
 * @author: LaoLiang
 * @create: 2018-03-26 19:15
 **/
@Controller
@RequestMapping("/user/exception/")
public class GlobalExceptionControllerTest {


    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        //测试全局异常
        int i=0;
        int j=666/i;

        //service-->mybatis-->dao
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            //设置cookie的sessionId和Redis的session

            CookieUtil.writeLoginToken(httpServletResponse, session.getId());

            //setEx(key, value, exTime), 设置带有剩余时间的session
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

}
