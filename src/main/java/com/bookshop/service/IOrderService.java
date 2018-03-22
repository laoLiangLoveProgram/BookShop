package com.bookshop.service;

import com.bookshop.common.ServerResponse;
import com.bookshop.vo.OrderVo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * Created by Administrator on 2018/3/21.
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    Boolean verifyAliCallback(Map<String, String> params);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartBook(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderVoList(Integer userId, int pageNum, int pageSize);

    ServerResponse<PageInfo> manageOrderList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
