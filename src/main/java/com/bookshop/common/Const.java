package com.bookshop.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Administrator on 2017/5/14.
 */
public final class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public static final String TOKEN_PREFIX = "token_";


    private Const() {
    }

    //商品状态, 1-在售, 2-下架, 3-删除
    public enum BookStatusEnum {
        ON_SALE(1, "在售");

        private String value;
        private int code;

        BookStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public static BookStatusEnum codeOf(int code) {
            for (BookStatusEnum bookStatusEnum : values()) {
                if (bookStatusEnum.getCode() == code) {
                    return bookStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    //订单状态的枚举
    public enum OrderStatusEnum {
        CANCELLED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSE(60, "订单关闭");


        private String value;
        private int code;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PayPlatformEnum {
        ALIPAY(1, "支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentTypeEnum {
        ONLINE_PAY(1, "在线支付");

        private int code;
        private String value;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public interface RedisCacheExtime {
        int REDIS_SESSION_EXTIME = 60 * 30;
    }

    public interface BookListOrderBy {
        //Set的contain方法的时间复杂度是O(1), List的contain方法的时间复杂度是O(n)
        Set<String> BOOK_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Cart {
        Integer CHECKED = 1; //购物车中商品处于选中状态
        Integer UN_CHECKED = 0;  //购物车中商品处于未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";   //购买数量限制失败
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    //用户分组：0-普通用户，1-管理员
    public interface Role{
        Integer ROLE_CUSTOMER = 0;  //普通用户
        Integer ROLE_ADMIN = 1;         //管理员
    }

    public interface AlipayCallback {
        //交易创建，等待买家付款, 默认不触发通知
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        //未付款交易超时关闭，或支付完成后全额退款, 默认不触发通知
        String TRADE_STATUS_TRADE_CLOSED = "TRADE_CLOSED";
        //交易支付成功, 默认触发通知
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        //交易结束，不可退款, 默认不触发通知
        String TRADE_STATUS_TRADE_FINISHED = "TRADE_FINISHED";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
}