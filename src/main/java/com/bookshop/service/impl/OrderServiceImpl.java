package com.bookshop.service.impl;/**
 * Created by Administrator on 2018/3/21.
 */

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.bookshop.common.Const;
import com.bookshop.common.ServerResponse;
import com.bookshop.dao.*;
import com.bookshop.pojo.*;
import com.bookshop.service.IOrderService;
import com.bookshop.util.BigDecimalUtil;
import com.bookshop.util.DateTimeUtil;
import com.bookshop.util.FTPUtil;
import com.bookshop.util.PropertiesUtil;
import com.bookshop.vo.OrderBookVo;
import com.bookshop.vo.OrderItemVo;
import com.bookshop.vo.OrderVo;
import com.bookshop.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: book_shop
 * @description:
 * @author: LaoLiang
 * @create: 2018-03-21 18:44
 **/
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;

    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        //charset如果不设置,  那么在build的时候会将charset赋值为utf-8
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 后台, 发货
     */
    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
            } else if (order.getStatus() == Const.OrderStatusEnum.NO_PAY.getCode()) {
                return ServerResponse.createByErrorMessage("该订单: " + Const.OrderStatusEnum.NO_PAY.getValue());
            } else if (order.getStatus() == Const.OrderStatusEnum.SHIPPED.getCode()) {
                return ServerResponse.createByErrorMessage("该订单: " + Const.OrderStatusEnum.SHIPPED.getValue());
            } else if (order.getStatus() == Const.OrderStatusEnum.CANCELLED.getCode()) {
                return ServerResponse.createByErrorMessage("该订单: " + Const.OrderStatusEnum.CANCELLED.getValue());
            } else if (order.getStatus() == Const.OrderStatusEnum.ORDER_CLOSE.getCode()) {
                return ServerResponse.createByErrorMessage("该订单: " + Const.OrderStatusEnum.ORDER_CLOSE.getValue());
            } else if (order.getStatus() == Const.OrderStatusEnum.ORDER_SUCCESS.getCode()) {
                return ServerResponse.createByErrorMessage("该订单: " + Const.OrderStatusEnum.ORDER_SUCCESS.getValue());
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * 后台, 按订单号 搜索, 分页, 模糊匹配, 支持多条件查询: 手机号, 姓名, 收货地址等
     */
    public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);

            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList(Lists.newArrayList(orderVo));

            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");

    }

    /**
     * 后台, 订单详情
     */
    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");

    }


    /**
     * 后台, 获取orderList
     */
    public ServerResponse<PageInfo> manageOrderList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }


    /**
     * 分页获取所有的订单
     */
    public ServerResponse<PageInfo> getOrderVoList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, userId);

        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            //将订单明细加载出来, 并组装成OrderItemVo
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                //管理员查询时, 不需要userId
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            } else {
                //用户查询
                orderItemList = orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(), userId);
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    /**
     * 获取订单详情
     */
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    /**
     * 获取用户购物车中已经选中的商品详情
     */
    public ServerResponse getOrderCartBook(Integer userId) {
        OrderBookVo orderBookVo = new OrderBookVo();
        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItemList(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }

        //计算总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            //组装OrderItemVo
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderBookVo.setOrderItemVoList(orderItemVoList);
        orderBookVo.setBookTotalPrice(payment);
        orderBookVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return ServerResponse.createBySuccess(orderBookVo);
    }

    /**
     * 取消订单
     */
    public ServerResponse<String> cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户不存在此订单");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("已付款, 无法取消订单");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELLED.getCode());

        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


    /**
     * 根据用户id和地址id, 从该用户的购物车中获取数据 创建订单, 并创建订单详细条目
     *
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        //计算订单的总价, 通过遍历购物车, 返回订单条目List
        ServerResponse serverResponse = this.getCartOrderItemList(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入OrderItem
        orderItemMapper.batchInsert(orderItemList);

        //生成成功, 减少库存
        this.reduceBookStock(orderItemList);

        //清空购物车
        this.cleanCart(cartList);

        //将订单明细返回给前端
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    //组装订单的信息,详细信息, 地址等给前端
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDescription(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());

        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDescription(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            //组装shippingVo
            orderVo.setShippingVo(this.assembleShippingVo(shipping));
        }

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        //组装OrderItemVo
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);


        return orderVo;
    }

    //组装OrderItemVo
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();

        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setBookId(orderItem.getBookId());
        orderItemVo.setBookName(orderItem.getBookName());
        orderItemVo.setBookImage(orderItem.getBookImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));

        return orderItemVo;
    }


    //组装shippingVO
    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }

    //清空购物车
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    //减少库存
    private void reduceBookStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Book book = bookMapper.selectByPrimaryKey(orderItem.getBookId());
            book.setStock(book.getStock() - orderItem.getQuantity());
            bookMapper.updateByPrimaryKeySelective(book);
        }
    }

    //组装订单对象
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        //生成订单号
        Long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);    //全场包邮
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间
        //付款时间
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    //生成订单号
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    //获得订单的总价
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    //通过购物车列表, 返回详细的订单条目List
    private ServerResponse<List<OrderItem>> getCartOrderItemList(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //校验购物车中的数据, 包括产品的状态和数量
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            Book book = bookMapper.selectByPrimaryKey(cartItem.getBookId());
            if (Const.BookStatusEnum.ON_SALE.getCode() != book.getStatus()) {
                return ServerResponse.createByErrorMessage("产品:" + book.getName() + " 不是在售卖状态");

            }
            //校验库存
            if (cartItem.getQuantity() > book.getStock()) {
                return ServerResponse.createByErrorMessage("产品:" + book.getName() + " 库存不足");
            }

            //组装
            orderItem.setUserId(userId);
            orderItem.setBookId(book.getId());
            orderItem.setBookName(book.getName());
            orderItem.setBookImage(book.getMainImage());
            //订单条目中的单价需要被记录下来, 防止具体的商品调价
            orderItem.setCurrentUnitPrice(book.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            //计算总价
            orderItem.setTotalPrice(BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), orderItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        //获取订单之前先重置订单的总价
        Order order = resetTotalPrice(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // 测试当面付2.0生成支付二维码
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("老梁的网上书城扫码支付, 订单号: ").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";   //使用默认的签约的Appid

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单: ").append(outTradeNo).append(", 购买商品总价共: ￥ ").append(totalAmount).append(" 元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);

        for (OrderItem orderItem : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getBookId().toString(), orderItem.getBookName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), orderItem.getQuantity()).longValue(), orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置, 扫码成功后和付款成功后, 支付宝回调通知的url
                .setGoodsDetailList(goodsDetailList);


        //生成订单
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                /**
                 * 下单成功, 生成二维码, 然后把二维码上传到我们的服务器中, 组装出url返回给前端
                 */
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + File.separator + "qr-%s.png", response.getOutTradeNo());
//                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                //工具类的作用, 就是根据url, 生成二维码的持久化文件
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(qrPath);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码到FTP服务器异常", e);
                    return ServerResponse.createByErrorMessage("二维码上传异常");
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    /**
     * 出于安全考虑，验签完成后，请开发者务必检查通知中的appid、外部订单号和订单金额与预下单时传入的是否一致。
     *
     * @param params
     * @return
     */
    public Boolean verifyAliCallback(Map<String, String> params) {
        Long appId = Long.valueOf(params.get("app_id"));
        if (appId == null || !appId.equals(Long.valueOf(Configs.getAppid()))) {
            return false;
        }
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        Order order = resetTotalPrice(orderNo);
        if (order == null) {
            return false;
        }
        BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));
        //获取order的总价
        BigDecimal orderTotalPrice = order.getPayment();
        if (totalAmount.compareTo(orderTotalPrice) != 0) {
            return false;
        }
        return true;
    }

    public ServerResponse aliCallback(Map<String, String> params) {
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");    //支付宝交易凭证号
        String tradeStatus = params.get("trade_status");    //交易目前所处的状态

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("非本商场的订单, 回调忽略");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            //支付宝回调告诉我们支付成功
            //将订单置为已付款
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //gmt_payment 该笔交易的买家付款时间。格式为yyyy-MM-dd HH:mm:ss
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo); //支付宝交易凭证号
        payInfo.setPlatformStatus(tradeStatus); //交易目前所处的状态

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }


    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();


    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    /**
     * 根据单品的实际价格重置订单中的总价, 包括orderItem的总价和order的总价
     *
     * @param orderNo
     */
    private Order resetTotalPrice(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return null;
        }

        Integer userId = order.getUserId();
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);

        BigDecimal orderTotalPrice = new BigDecimal("0");

        for (OrderItem orderItem : orderItemList) {
            BigDecimal orderItemTotalPrice = BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), orderItem.getQuantity());
            orderTotalPrice = BigDecimalUtil.add(orderItemTotalPrice.doubleValue(), orderTotalPrice.doubleValue());
            orderItem.setTotalPrice(orderItemTotalPrice);
            //更新数据库中订单条目的总价
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
        }
        //更新数据库订单的总价
        order.setPayment(orderTotalPrice);
        orderMapper.updateByPrimaryKeySelective(order);
        return order;
    }
}
