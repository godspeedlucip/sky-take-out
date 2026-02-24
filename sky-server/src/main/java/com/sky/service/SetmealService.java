package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService {

    /**
     * 添加新的setmeal
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);


    SetmealVO getByIdWithDish(Long id);

    void deleteBatch(List<Long> ids);

    PageResult pageQuery(SetmealPageQueryDTO queryDTO);

    void update(SetmealDTO setmealDTO);

    /**
     * 起售/停售 套餐
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

}




