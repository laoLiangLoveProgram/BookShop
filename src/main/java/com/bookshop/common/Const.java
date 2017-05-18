package com.bookshop.common;

/**
 * Created by Administrator on 2017/5/14.
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    //用户分组：0-普通用户，1-管理员
    public interface Role{
        Integer ROLE_CUSTOMER = 0;  //普通用户
        Integer ROLE_ADMIN = 1;         //管理员
    }
}
