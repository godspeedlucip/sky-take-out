package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    /**
     * 查询满足要求的所有订单
     * @param begin
     * @param end
     * @return
     */
    Double getByStatusAndTime(LocalDateTime begin, LocalDateTime end, Integer status);
}
