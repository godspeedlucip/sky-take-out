package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入一条新数据
     * @param order
     * @return
     */
    long insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 找出超时的订单
     * @param overTime
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time < #{overTime} ")
    List<Orders> getByStatusAndOrdertimeGT(long status, LocalDateTime overTime);

    /**
     * 查询得到总的订单数
     * @param begin
     * @param end
     * @param status
     * @return
     */
    Double getCountByTime(LocalDateTime begin, LocalDateTime end, Integer status);

    /**
     * 查询销量前10的菜品
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getTop10Dish(LocalDateTime begin, LocalDateTime end, Integer status);
}
