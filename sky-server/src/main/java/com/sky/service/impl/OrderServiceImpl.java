package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import io.netty.util.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    //    订单表的Mapper
    @Autowired
    private OrderMapper orderMapper;

    //    订单明细表的mapper
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    // 和订单支付相关
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO){
        long user_id = BaseContext.getCurrentId();

        // 首先要获得其购物车的东西
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryById(user_id);

        // 其次要获得其地址
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());

        // 进行异常判定
        if (shoppingCarts==null||shoppingCarts.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 之后写入订单表
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(user_id);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        orderMapper.insert(order);  // 获得具体的订单号
        long order_id = order.getId();

        // 写入订单明细表
        List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
        shoppingCarts.forEach(cart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order_id);
            orderDetails.add(orderDetail);
        });
        orderDetailMapper.insertBatch(orderDetails);


        // 清空购物车
        shoppingCartMapper.clearById(user_id);

        // 返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order_id)
                .orderTime(LocalDateTime.now())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .build();
        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.queryUserId(String.valueOf(userId));

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        // 给客户端发送新订单提醒
        Map json_map = new HashMap<>();
        json_map.put("type", 1); // 来单提醒
        json_map.put("orderId", ordersDB.getId());
        json_map.put("content", "订单号：" + outTradeNo);
        String json_string = JSON.toJSONString(json_map);
        webSocketServer.sendToAllClient(json_string);
    }

    /**
     * 客户催单
     * @param id
     */
    public void remind(String id){
        // 查找id对应的订单
        Orders order = orderMapper.getByNumber(id);

        // 检查订单是否存在
        if(order==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 催单
        Map json_map = new HashMap<>();
        json_map.put("type", 2); // 催单提醒
        json_map.put("orderId", order.getId());
        json_map.put("content", "订单号：" + order.getNumber());
        String json_string = JSON.toJSONString(json_map);
        webSocketServer.sendToAllClient(json_string);
    }
}
