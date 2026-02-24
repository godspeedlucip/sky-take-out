package com.sky.service.impl;


import com.sky.constant.MessageConstant;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import com.sun.org.apache.bcel.internal.generic.LXOR;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 查询总的订单金额
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end){
        // 得到VO的日期string
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while(! begin.isEqual(end)){
            begin = begin.plusDays(1); //向后加一天
            dates.add(begin);
        }

        List<Double> sums = new ArrayList<>();
        // 依次处理所有的日期
        for (LocalDate date : dates) {
            // 得到能和数据表中的时间相比较的对象
            LocalDateTime start_date_time = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end_date_time = LocalDateTime.of(date, LocalTime.MAX);

            // 查询这个区间内满足要求的所有订单.
            Double orders_sum = reportMapper.getByStatusAndTime(start_date_time, end_date_time, Orders.COMPLETED);
            if(orders_sum == null){
                orders_sum = 0d;
            }
            sums.add(orders_sum);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .turnoverList(StringUtils.join(sums, ","))
                .build();
    }

    /**
     * 查询用户的信息
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end){
        // 得到VO的日期string
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while(! begin.isEqual(end)){
            begin = begin.plusDays(1); //向后加一天
            dates.add(begin);
        }

        List<Double> all_user_sums = new ArrayList<>();
        List<Double> new_user_sums = new ArrayList<>();
        // 依次处理所有的日期
        for (LocalDate date : dates) {
            // 得到能和数据表中的时间相比较的对象
            LocalDateTime start_date_time = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end_date_time = LocalDateTime.of(date, LocalTime.MAX);

            // 查询这个区间内满足要求的所有订单.
            Double user_sum = userMapper.getCountByTime(null, end_date_time);
            Double user_new = userMapper.getCountByTime(start_date_time, end_date_time);
            if(user_sum == null){
                user_sum = 0d;
            }
            if(user_new == null){
                user_new = 0d;
            }
            all_user_sums.add(user_sum);
            new_user_sums.add(user_new);
        }

        // 计算新增
        return UserReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .totalUserList(StringUtils.join(all_user_sums, ","))
                .newUserList(StringUtils.join(new_user_sums, ","))
                .build();
    }

    /**
     * 统计订单的信息
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end){
        // 得到VO的日期string
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while(! begin.isEqual(end)){
            begin = begin.plusDays(1); //向后加一天
            dates.add(begin);
        }

        List<Double> all_order_sum = new ArrayList<>();
        List<Double> all_order_valid_sum = new ArrayList<>();
        // 依次处理所有的日期
        for (LocalDate date : dates) {
            // 得到能和数据表中的时间相比较的对象
            LocalDateTime start_date_time = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end_date_time = LocalDateTime.of(date, LocalTime.MAX);

            // 查询这个区间内满足要求的所有订单.
            Integer status = Orders.COMPLETED;
            Double order_sum = orderMapper.getCountByTime(start_date_time, end_date_time, null);
            Double order_valid_sum = orderMapper.getCountByTime(start_date_time, end_date_time, status);
            if(order_sum == null){
                order_sum = 0d;
            }
            if(order_valid_sum == null){
                order_valid_sum = 0d;
            }
            all_order_sum.add(order_sum);
            all_order_valid_sum.add(order_valid_sum);
        }

        // 计算查询的列表的总和
        int orders_sum = 0;
        for (Double v : all_order_sum) {
            orders_sum += v.intValue();
        }

        int order_valid_sum = 0;
        for (Double v : all_order_valid_sum) {
            order_valid_sum += v.intValue();
        }

        // 另外一种简洁的计算求和的方法
//        Integer totalOrderCount = all_order_sum.stream().reduce(Integer::sum).get();
//        Integer validOrderCount = all_order_valid_sum.stream().reduce(Integer::sum).get();

        Double com_rate = 0.0;
        if(order_valid_sum!=0){
            com_rate = (double)order_valid_sum / (double)orders_sum;
        }


        // 计算新增
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .orderCountList (StringUtils.join(all_order_sum, ","))
                .validOrderCountList (StringUtils.join(all_order_valid_sum, ","))
                .totalOrderCount(orders_sum)
                .validOrderCount(order_valid_sum)
                .orderCompletionRate(com_rate)
                .build();
    }

    /**
     * 计算销量在前10的菜品的数据
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO top10Statistics(LocalDate begin, LocalDate end){
        LocalDateTime start_date_time = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime end_date_time = LocalDateTime.of(end, LocalTime.MAX);

        // 查到满足要求的Order
        Integer status = Orders.COMPLETED;
        List<GoodsSalesDTO> top10_dishs = orderMapper.getTop10Dish(start_date_time, end_date_time, status);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO top10Dish : top10_dishs) {
            nameList.add(top10Dish.getName());
            numberList.add(top10Dish.getNumber());
        }

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList (StringUtils.join(numberList, ","))
                .build();
    }
}
