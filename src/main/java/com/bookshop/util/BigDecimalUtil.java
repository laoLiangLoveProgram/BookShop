package com.bookshop.util;

import java.math.BigDecimal;

/**
 * BigDecimal类对于double类型的计算并不是精确的, 尽人意的,
 * 所以需要使用BigDecimal(String)的构造器,来得到一个精确的数值计算结果
 */
public class BigDecimalUtil {


    private BigDecimalUtil() {

    }

    public static BigDecimal add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal subtract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    public static BigDecimal multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal divide(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        //除不尽的情况, 我们可以使用BigDecimal的重载方法
//        return b1.divide(b2); //遇到除不尽会抛异常
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);  //四舍五入, 保留两位小数
    }

}
