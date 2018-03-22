package com.bookshop.vo;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/5/25.
 */
public class CartBookVo {
    //结合了产品和购物车的一个抽象的对象

    //购物车部分
    private Integer id; //购物车ID
    private Integer userId;
    private Integer bookId;
    private Integer quantity;
    private Integer bookChecked;    //此商品是否勾选

    //商品部分
    private String bookName;
    private String bookSubtitle;
    private String bookMainImage;
    private BigDecimal bookPrice;
    private Integer bookStatus;
    private BigDecimal bookTotalPrice;
    private Integer bookStock;

    private String limitQuantity;   //根据库存有一个限制数量

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookSubtitle() {
        return bookSubtitle;
    }

    public void setBookSubtitle(String bookSubtitle) {
        this.bookSubtitle = bookSubtitle;
    }

    public String getBookMainImage() {
        return bookMainImage;
    }

    public void setBookMainImage(String bookMainImage) {
        this.bookMainImage = bookMainImage;
    }

    public BigDecimal getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }

    public Integer getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(Integer bookStatus) {
        this.bookStatus = bookStatus;
    }

    public BigDecimal getBookTotalPrice() {
        return bookTotalPrice;
    }

    public void setBookTotalPrice(BigDecimal bookTotalPrice) {
        this.bookTotalPrice = bookTotalPrice;
    }

    public Integer getBookStock() {
        return bookStock;
    }

    public void setBookStock(Integer bookStock) {
        this.bookStock = bookStock;
    }

    public Integer getBookChecked() {
        return bookChecked;
    }

    public void setBookChecked(Integer bookChecked) {
        this.bookChecked = bookChecked;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }
}
