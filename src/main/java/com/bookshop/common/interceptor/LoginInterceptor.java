package com.bookshop.common.interceptor;/**
 * Created by Administrator on 2018/3/26.
 */

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.backend.BookManageController;
import com.bookshop.controller.portal.BookController;
import com.bookshop.controller.portal.CartController;
import com.bookshop.controller.portal.OrderController;
import com.bookshop.controller.portal.UserController;
import com.bookshop.pojo.User;
import com.bookshop.util.CookieUtil;
import com.bookshop.util.JsonUtil;
import com.bookshop.util.RedisShardedPoolUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @program: book_shop
 * @description: 验证是否登录的拦截器
 * @author: LaoLiang
 * @create: 2018-03-26 22:07
 **/
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求中Controller的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //解析handlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getName();

        //解析参数, 具体的参数key及value是什么, 我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();

        Map paramMap = request.getParameterMap();

        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;
            //request 这个参数的map, 里面的value返回的是一个String数组
            Object value = entry.getValue();    //value是一个String[] : 例如 :{"admin"}
            if (value instanceof String[]) {
                String[] strings = (String[]) value;
                mapValue = Arrays.toString(strings);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue).append(";");
        }


        //不拦截登录和注册的请求
        if (StringUtils.equals(methodName, "login") || StringUtils.equals(methodName, "register")) {
            //登录请求中, 不打印请求的参数, 因为参数中有密码, 如果打印到日志中, 则会造成信息泄露
            return true;
        }

        //不拦截支付宝回调请求
        if (StringUtils.equals(methodName, "alipayCallback") && StringUtils.equals(className, OrderController.class.getName())) {
            return true;
        }

        //不拦截获取购物车产品数量的请求, 即使未登录, 也可以获取, 只不过为0
        if (StringUtils.equals(methodName, "getCartBookCount") && StringUtils.equals(className, CartController.class.getName())) {
            return true;
        }

        // 不拦截用户请求中的 校验用户名和Email是否存在, 忘记密码之获取密码提示问, 忘记密码之校验密码提示答案, 忘记密码之重置密码
        if (StringUtils.equals(className, UserController.class.getName())){
            if (StringUtils.equals(methodName, "checkValid") || StringUtils.equals(methodName, "forgetGetQuestion")
                    || StringUtils.equals(methodName, "forgetCheckAnswer") || StringUtils.equals(methodName, "forgetResetPassword")){
                return true;
            }
        }


        log.info("登陆拦截器拦截到请求, className:{}, methodName:{}", className, methodName);
        log.info("请求的参数为: {}", requestParamBuffer.toString());
        //不拦截获取商品信息的所有请求
        if (StringUtils.equals(className, BookController.class.getName())) {
            return true;
        }

        //登录验证

        User user = null;
        String sessionId = CookieUtil.readLoginToken(request);
        if (sessionId != null) {
            String userJsonStr = RedisShardedPoolUtil.get(sessionId);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }

        //user == null 有两种情况, 一种是sessionId为空, 一种是数据库中没有session信息
        if (user == null) {
            //由于需要重写response, 这样response就脱离了SpringMVC的流程, 就需要我们自己去实现写数据到response
            response.reset();   //这里需要reset, 否则会报异常, getWriter() hash already been called for this response
            //因为已经脱离了springMVC的流程, 那么springMVC的characterEncodingFilter就不起作用了, 需要我们自己设置编码
            response.setCharacterEncoding("UTF-8");
            //由于前后端交互使用的json, 所以需要设置response的ContentType类型为json
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter out = response.getWriter();

            //由于富文本上传有特殊的返回值要求, 所以要做处理
            if (StringUtils.equals(className, BookManageController.class.getName()) && StringUtils.equals(methodName, "richtextImgUpload")) {
                Map resultMap = Maps.newHashMap();
                resultMap.put("success", false);
                resultMap.put("msg", "请登录管理员");
                out.print(JsonUtil.obj2String(resultMap));
            }else{
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc())));
            }

            out.flush();
            out.close();

            return false;
        }
        //将user放入request中 以便下面的拦截器或调用的方法使用
        request.setAttribute(Const.CURRENT_USER, user);
        request.setAttribute("requestParams", requestParamBuffer.toString());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
