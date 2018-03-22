package com.bookshop.vo;/**
 * Created by Administrator on 2018/3/22.
 */

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: book_shop
 * @description:
 * @author: LaoLiang
 * @create: 2018-03-22 19:06
 **/
public class OrderBookVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal bookTotalPrice;
    private String imageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getBookTotalPrice() {
        return bookTotalPrice;
    }

    public void setBookTotalPrice(BigDecimal bookTotalPrice) {
        this.bookTotalPrice = bookTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
