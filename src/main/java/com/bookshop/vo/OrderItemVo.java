package com.bookshop.vo;/**
 * Created by Administrator on 2018/3/22.
 */

import java.math.BigDecimal;

/**
 * @program: book_shop
 * @description: 订单明细
 * @author: LaoLiang
 * @create: 2018-03-22 18:05
 **/
public class OrderItemVo {
    private Long orderNo;   //订单号

    private Integer bookId; //产品id

    private String bookName;    //产品名称

    private String bookImage;   //产品主图

    private BigDecimal currentUnitPrice;    //单价

    private Integer quantity;  //数量

    private BigDecimal totalPrice;  //总价

    private String createTime;  //创建时间

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
