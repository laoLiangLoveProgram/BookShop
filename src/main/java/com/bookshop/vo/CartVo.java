package com.bookshop.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */
public class CartVo {

    private List<CartBookVo> cartBookVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked; //是否都已经勾选
    private String imageHost;

    public List<CartBookVo> getCartBookVoList() {
        return cartBookVoList;
    }

    public void setCartBookVoList(List<CartBookVo> cartBookVoList) {
        this.cartBookVoList = cartBookVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
