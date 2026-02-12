package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlaverMapper {

    /**
     * 根据分类id查询菜品数量
//     * @param dishFlavor
     * @return
     */
    void addBatch(List<DishFlavor> dishFlavor);

    /**
     * 批量删除flavor 根据传入的dish ids
     * @param dishIds
     */
    void deleteBatch(List<Long> dishIds);

    /**
     * 根据dish id来获得所有的dish flavor
     * @param dishId
     * @return
     */
    List<DishFlavor> getByDishId(Long dishId);
}
