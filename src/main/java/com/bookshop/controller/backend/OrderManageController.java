package com.bookshop.controller.backend;/**
 * Created by Administrator on 2018/3/22.
 */

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.common.UserSessionUtil;
import com.bookshop.pojo.User;
import com.bookshop.service.IOrderService;
import com.bookshop.service.IUserService;
import com.bookshop.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: book_shop
 * @description:
 * @author: LaoLiang
 * @create: 2018-03-22 19:46
 **/
@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    /**
     * 后台管理的订单list
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务逻辑
        return iOrderService.manageOrderList(pageNum, pageSize);

    }

    /**
     * 订单详情
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpServletRequest request, Long orderNo) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务逻辑
        return iOrderService.manageDetail(orderNo);

    }

    /**
     * 按订单号搜索
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpServletRequest request, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务逻辑
        return iOrderService.manageSearch(orderNo, pageNum, pageSize);

    }

    /**
     * 发货
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpServletRequest request, Long orderNo) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务逻辑
        return iOrderService.manageSendGoods(orderNo);

    }


}
