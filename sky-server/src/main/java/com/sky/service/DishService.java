package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService {
    /**
     * 添加新的菜品
     * @param dishDTO
     */
    void addWithFlaver(DishDTO dishDTO);

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除一个菜品
     * @param deleteId
     * @return
     */
    void deleteDish(List<Long> deleteId);

    /**
     * 根据id来查询菜品信息
     * @param id
     * @return
     */
    DishVO queryDishById(Long id);

    /**
     * 修改dish
     * @param dishDTO
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
