package com.bookshop.service;

import com.bookshop.common.ServerResponse;

import java.util.Map;

/**
 * Created by Administrator on 2018/3/21.
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    Boolean verifyAliCallback(Map<String, String> params);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
