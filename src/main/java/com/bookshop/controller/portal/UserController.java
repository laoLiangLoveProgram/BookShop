package com.bookshop.controller.portal;

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.common.UserSessionUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/5/14.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        //service-->mybatis-->dao
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            //设置cookie的sessionId和Redis的session

            CookieUtil.writeLoginToken(httpServletResponse, session.getId());

            //setEx(key, value, exTime), 设置带有剩余时间的session
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 用户登出, 销毁Cookie信息和Redis中的session信息
     *
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    //ResponseBody注解：在返回的时候，自动通过springMVC的jackson插件将返回值序列化成Json
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String sessionId = CookieUtil.readLoginToken(request);

        //删除cookie
        CookieUtil.deleteLoginToken(request, response);

        //删除redis中的session记录
        RedisPoolUtil.del(sessionId);

        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    //ResponseBody注解：在返回的时候，自动通过springMVC的jackson插件将返回值序列化成Json
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户名和Email是否存在
     * str是username还是email，由type来决定，
     * @param str
     * @param type  只有两种值"username", "email"
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    //ResponseBody注解：在返回的时候，自动通过springMVC的jackson插件将返回值序列化成Json
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取登录用户的信息
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        //判断用户是否登录
        String sessionId = CookieUtil.readLoginToken(request);
        if (sessionId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(sessionId);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    /**
     * 忘记密码之获取密码提示问题
     * @param username
     * @return 返回密码提示问题
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }


    /**
     * 忘记密码之校验密码提示答案
     * @param username
     * @param question
     * @param answer
     * @return  返回token
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码之重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    //
    /**
     * 登录状态下的重置密码
     *
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest request, String passwordOld, String passwordNew){
        //判断是否登录
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, ResponseCode.NEED_LOGIN.getDesc());
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = (User) serverResponse.getData();

        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * 更新个人用户信息的接口
     *
     * @return 成功返回用户新的信息
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest request, User user){
        //判断是否登录
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, ResponseCode.NEED_LOGIN.getDesc());
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User currentUser = (User) serverResponse.getData();


        //User传过来的参数中是没有userId的，需要复制成当前登录的userId，可以有效防止横向越权问题
        user.setId(currentUser.getId());
        //username是不能被更新的
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            //如果更新成功，则需要更新session中的个人信息
            //更新数据库中的sessionId对应的用户信息
            String sessionId = CookieUtil.readLoginToken(request);
            RedisPoolUtil.setEx(sessionId, JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }
        return response;
    }

    /**
     * 获取用户详细信息的接口，需要判断是否登录
     *
     * @return 返回用户对象
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request){
        //先判断是否登录
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, ResponseCode.NEED_LOGIN.getDesc());
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = (User) serverResponse.getData();


        return iUserService.getInformation(user.getId());
    }


}
