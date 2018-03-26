package com.bookshop.common;/**
 * Created by Administrator on 2018/3/26.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: book_shop
 * @description: 处理全局异常
 * @author: LaoLiang
 * @create: 2018-03-26 19:33
 **/
@Slf4j
//需要将处理全局异常的Bean类注入到spring容器中, 才能起到作用
@Component
public class ExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        //打印异常日志
        log.error("{} Exception : ", httpServletRequest.getRequestURI(), e);

        //传入View接口的实现类 MappingJacksonJsonView对象
        //注意当使用 jackson2.x的时候 , 需要使用MappingJackson2JsonView
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());

        //返回给前端的是一个高复用响应对象 ServerResponse
        modelAndView.addObject("status", ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg", "接口异常, 详情请查看服务端日志的异常信息");
        modelAndView.addObject("data", e.toString());

        return modelAndView;
    }
}
