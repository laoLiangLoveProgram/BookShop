package com.bookshop.service;

import com.bookshop.common.ServerResponse;
import com.bookshop.vo.CartVo;

/**
 * Created by Administrator on 2017/5/25.
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer bookId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer bookId, Integer count);

    ServerResponse<CartVo> deleteBook(Integer userId, String bookIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer bookId, Integer checked);

    ServerResponse<Integer> getCartBookCount(Integer userId);
}
