package com.bookshop.service.impl;

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.dao.BookMapper;
import com.bookshop.dao.CartMapper;
import com.bookshop.pojo.Book;
import com.bookshop.pojo.Cart;
import com.bookshop.service.ICartService;
import com.bookshop.util.BigDecimalUtil;
import com.bookshop.util.PropertiesUtil;
import com.bookshop.vo.CartBookVo;
import com.bookshop.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private BookMapper bookMapper;

    public ServerResponse<CartVo> add(Integer userId, Integer bookId, Integer count) {
        if (bookId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdBookId(userId, bookId);
        if (cart == null) {
            //如果这个bookId的不在这个userId的购物车中, 需要新增一个这个产品的记录
            //先判断bookId是否存在对应的书籍数据在数据库中
            Book book = bookMapper.selectByPrimaryKey(bookId);
            if (book == null) {
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setBookId(bookId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        } else {
            //产品已存在于uesrId的购物车中, 则增加数量
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        // 需要将购物车的数量和库存联动起来
        return this.list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId, Integer bookId, Integer count) {
        if (bookId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdBookId(userId, bookId);
        if (cart != null) {
            cart.setQuantity(count);
            //cart中的数量变化了, 数据库需要更新
            cartMapper.updateByPrimaryKeySelective(cart);
        } else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return this.list(userId);

    }

    public ServerResponse<CartVo> deleteBook(Integer userId, String bookIds) {
        //guava的Splitter类的方法,会将字符串分割成集合
        List<String> bookIdList = Splitter.on(",").splitToList(bookIds);
        if (CollectionUtils.isEmpty(bookIdList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdBookIds(userId, bookIdList);

        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);

        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer bookId, Integer checked) {
        cartMapper.checkedOrUncheckedBook(userId, bookId, checked);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartBookCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }

        return ServerResponse.createBySuccess(cartMapper.selectCartBookCount(userId));
    }


    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartBookVo> cartBookVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartBookVo cartBookVo = new CartBookVo();

                cartBookVo.setId(cartItem.getId());
                cartBookVo.setUserId(cartItem.getUserId());
                cartBookVo.setBookId(cartItem.getBookId());
                cartBookVo.setBookChecked(cartItem.getChecked());

                Book book = bookMapper.selectByPrimaryKey(cartItem.getBookId());
                if (book != null) {
                    cartBookVo.setBookName(book.getName());
                    cartBookVo.setBookSubtitle(book.getSubtitle());
                    cartBookVo.setBookMainImage(book.getMainImage());
                    cartBookVo.setBookPrice(book.getPrice());
                    cartBookVo.setBookStatus(book.getStatus());
                    cartBookVo.setBookStock(book.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (book.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候,
                        buyLimitCount = cartItem.getQuantity();
                        //库存大于数量,  符合要求
                        cartBookVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = book.getStock();
                        cartBookVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //超出库存, 购物车中要更新有效库存
                        //只是去更新数据库中购物车商品的数量
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartBookVo.setQuantity(buyLimitCount);  //无论充足还是不充足的时候都赋值为正确的数量
                    //计算总价
                    cartBookVo.setBookTotalPrice(BigDecimalUtil.multiply(cartBookVo.getBookPrice().doubleValue(), cartBookVo.getQuantity()));
                }

                if (Const.Cart.CHECKED.equals(cartItem.getChecked())) {
                    //book为已勾选, 需要更新到CartVo中的总价
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartBookVo.getBookTotalPrice().doubleValue());
                }

                cartBookVoList.add(cartBookVo);
            }
        }
        cartVo.setCartBookVoList(cartBookVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }

        //通过查询没有勾选的 数量, 如果大于0, 则表示非全选
        return cartMapper.selectCartBookCheckedStatusByUserId(userId) == 0;
    }
}
