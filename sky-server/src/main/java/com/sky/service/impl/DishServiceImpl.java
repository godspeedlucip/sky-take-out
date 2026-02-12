package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlaverMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlaverMapper dishFlaverMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    //因为要同时更改两个数据库，因此要用这个
    @Transactional
    public void addWithFlaver(DishDTO dishDTO) {

        // 获取dish对象
        Dish dish = new Dish(); //dish没有flaver
        BeanUtils.copyProperties(dishDTO, dish);
//        long dish_id = dishMapper.add(dish);
        dishMapper.add(dish);
        Long dish_id = dish.getId();

        // 获取dishFlaver对象
//        DishFlavor dishFlavor = new DishFlavor();
        // DIsh中包含的DishFlaver实际上是一个List
        List<DishFlavor> dishFlavor = dishDTO.getFlavors();

        //dishFlaver实际上是可以为空的
        if(dishFlavor!=null && dishFlavor.size()>0){
            /// 批量插入
            dishFlavor.forEach(dishFlavor_item -> {
                dishFlavor_item.setDishId(dish_id);
            });
            dishFlaverMapper.addBatch(dishFlavor);
        }
    }

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 使用pageHelper插件
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        // 返回值必须是pageHelper插件要求的
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> result = page.getResult();

        return new PageResult(total, result);
    }

    @Override
    @Transactional
    public void deleteDish(List<Long> deleteIds){
        //检查能否被删除: 是否是被上架的，是的话就不可以删除
        deleteIds.forEach(deleteId -> {
            Dish dish = dishMapper.getById(deleteId);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        //检查能否被删除: 是否被包含在一个套餐之中，是的话就不可以删除
        List<Long> setmealIDs = setmealMapper.getSetmealIdsByDishIds(deleteIds);
        if(setmealIDs!=null && setmealIDs.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        dishMapper.deleteBatch(deleteIds);

        //删除这个菜品对应的flavor
        dishFlaverMapper.deleteBatch(deleteIds);

    }

    /**
     * 根据id来查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishVO queryDishById(@RequestParam("id") Long id){
        //sql联立查询一下就行
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavers = dishFlaverMapper.getByDishId(dish.getId());
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavers);
        return dishVO;
    }

    /**
     * 修改dish
     * @param dishDTO
     */
    public void updateDish(DishDTO dishDTO){
        // 修改dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除dish flavor中的dish id的数据
        List<Long> dishIds = new ArrayList<>();
        dishIds.add(dishDTO.getId());
        dishFlaverMapper.deleteBatch(dishIds);

        //在dish flavor中插入设定的多条数据
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors!=null && dishFlavors.size()>0) {
            /// 批量插入
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            dishFlaverMapper.addBatch(dishFlavors);
        }
        //修改category
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        // 模糊查询所有包含dish名字的菜品
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlaverMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
