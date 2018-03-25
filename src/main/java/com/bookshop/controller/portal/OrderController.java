package com.bookshop.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.pojo.User;
import com.bookshop.service.IOrderService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * 由于具体的支付和订单的依赖比较强, 并且接口比较少, 所以直接放在订单类下面
 *
 * @program: book_shop
 * @description: 订单类
 * @author: LaoLiang
 * @create: 2018-03-21 18:36
 **/
@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

//    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }


    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }

    /**
     * 获取购物车中已经选中的商品详情
     *
     * @param session
     * @return
     */
    @RequestMapping("get_order_cart_book.do")
    @ResponseBody
    public ServerResponse getOrderCartBook(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartBook(user.getId());
    }

    /**
     * 获取订单详情
     *
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 查看所有的订单
     *
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderVoList(user.getId(), pageNum, pageSize);
    }














    /**
     * 支付订单号, 通过request获得servlet上下文, 获取upload文件夹, 把自动生成的二维码传到ftp服务器上, 然后返回给前端二维码的图片地址
     *
     * @param session
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");

        return iOrderService.pay(orderNo, user.getId(), path);
    }


    /**
     * 支付宝的回调函数
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map<String, String[]> requestParameters = request.getParameterMap();
        Iterator<String> iterator = requestParameters.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            String[] values = requestParameters.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = valueStr + values[i];
                if (i != values.length - 1) {
                    valueStr = valueStr + ",";
                }
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调, 签名sign:{}, trade_status:{}, 参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
        //验证签名, 非常重要, 验证回调的正确性, 是不是支付宝发的, 并且还要避免重复通知
        //必须先移除sign和sign_type两个key, 由于sign交给了Alipay的内置的rsaCheckV2()方法移除, 所以我们只需要自己移除sign_type
        params.remove("sign_type");

        boolean alipayRSACheckedV2 = false;
        try {
            alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求, 验证不通过, 如果重复多次, 将向网警举报");
            }
        } catch (AlipayApiException e) {
            log.error("验证过程有异常", e);
        }


        // 验证通知数据中的out_trade_no是否为商户系统中创建的订单号, total_amount, seller_id, seller_email(有时, 一个商户可能有多个seller_id/seller_email),只要有一个验证不通过, 则表明本次通知是异常通知, 务必忽略
        Boolean verifyAliCallback = iOrderService.verifyAliCallback(params);
        if (!verifyAliCallback) {
            log.info("支付宝回调通知数据不正确");
            return "";
        }

        // 在上述验证通过后 商户必须根据支付宝不同类型的业务通知, 正确的进行不同的业务处理, 并且过滤重复的通知结果数据. 在支付宝的业务通知中, 只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时, 支付宝才会认定为买家付款成功.
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 前台轮训查询订单的状态, 如果付款成功, 会给前台一个指引, 跳到订单
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


}
