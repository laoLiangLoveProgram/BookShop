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
import com.bookshop.dao.OrderItemMapper;
import com.bookshop.dao.OrderMapper;
import com.bookshop.dao.PayInfoMapper;
import com.bookshop.pojo.Order;
import com.bookshop.pojo.OrderItem;
import com.bookshop.pojo.PayInfo;
import com.bookshop.service.IOrderService;
import com.bookshop.util.BigDecimalUtil;
import com.bookshop.util.DateTimeUtil;
import com.bookshop.util.FTPUtil;
import com.bookshop.util.PropertiesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
