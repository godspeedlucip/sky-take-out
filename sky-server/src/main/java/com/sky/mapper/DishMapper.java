package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据分类id查询菜品数量
     * @param dish
     * @return
     */
    @AutoFill(value = OperationType.INSERT)
    long add(Dish dish);


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 根据id号获取到一个dish的信息
     * @param deleteId
     * @return
     */
    @Select("select * from dish where id = #{deleteId} ")
    Dish getById(Long deleteId);

    /**
     * 根据dish的id获取到对应的set_meal信息
     * @param deleteId
     * @return
     */
    @Select("select count(0) from setmeal where dish_id=#{deleteId}")
    int check_dish_in_setmeal(Long deleteId);

    /**
     * 根据传入的的dish id进行批量的删除
     * @param deleteIds
     */
    void deleteBatch(List<Long> deleteIds);


    /**
     * 根据给定的dish id来查询对应的dish + dish flavor
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改dish
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     *
     * @param dish
     */
    List<Dish> list(Dish dish);
}
