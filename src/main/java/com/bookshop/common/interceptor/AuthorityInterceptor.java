package com.bookshop.common.interceptor;/**
 * Created by Administrator on 2018/3/26.
 */

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.backend.BookManageController;
import com.bookshop.pojo.User;
import com.bookshop.util.JsonUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @program: book_shop
 * @description: 权限拦截器
 * @author: LaoLiang
 * @create: 2018-03-26 20:46
 **/
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    /**
     * Controller之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求中Controller的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //解析handlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //不拦截 登录请求
        if (StringUtils.equals(methodName, "login")) {
            return true;
        }
        User user = (User) request.getAttribute(Const.CURRENT_USER);
        String requestParams = (String) request.getAttribute("requestParams");
        log.info("权限拦截器拦截到请求, className:{}, methodName:{}", className, methodName);
        log.info("请求的参数为: {}", requestParams);
        if (user != null) {
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                return true;
            }
        }
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
            resultMap.put("msg", "无权限操作");
            out.print(JsonUtil.obj2String(resultMap));
        }else {
            out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("无权限操作")));
        }

        out.flush();
        out.close();

        return false;
    }

    /**
     * Controller之后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在所有处理完成之后被调用, 如果需要返回ModelAndView, 在视图呈现之后被调用
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
