package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    // 每分钟检查是否有超时的订单
    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void deleteOrder() {
        log.info("删除超时订单");
        LocalDateTime overTime = LocalDateTime.now().plusMinutes(-15);
        long status = Orders.PENDING_PAYMENT; // 未支付的状态
        List<Orders> byStatusAndOrdertimeGT = orderMapper.getByStatusAndOrdertimeGT(status, overTime);

        byStatusAndOrdertimeGT.forEach(order -> {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("订单超时");
            orderMapper.update(order); // 更新订单状态
        });
    }

    // 每天凌晨一点来处理处于派送中的订单
    @Scheduled(cron = "* * 1 * * ?")
//    @Scheduled(cron = "1/5 * * * * ?")
    public void completeOrder(){
        log.info("将还在派送中的订单设置为派送完成");
        LocalDateTime overTime = LocalDateTime.now().plusMinutes(-60); // 减去60就是处理上一个工作日的订单
        long status = Orders.DELIVERY_IN_PROGRESS; // 未支付的状态
        List<Orders> byStatusAndOrdertimeGT = orderMapper.getByStatusAndOrdertimeGT(status, overTime);

        byStatusAndOrdertimeGT.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order); // 更新订单状态
        });
    }
}
