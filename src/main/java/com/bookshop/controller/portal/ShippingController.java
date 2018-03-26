package com.bookshop.controller.portal;

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.common.UserSessionUtil;
import com.bookshop.pojo.Shipping;
import com.bookshop.pojo.User;
import com.bookshop.service.IShippingService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/5/26.
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Shipping shipping) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iShippingService.add(user.getId(), shipping);
    }


    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<String> delete(HttpServletRequest request, Integer shippingId) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iShippingService.delete(user.getId(), shippingId);
    }


    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Shipping shipping) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iShippingService.update(user.getId(), shipping);
    }


    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpServletRequest request, Integer shippingId) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iShippingService.list(user.getId(), pageNum, pageSize);
    }


}
