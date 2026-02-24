package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    private void clearCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    @PostMapping()
    @ApiOperation(value = "添加一个新菜品")
    public Result add(@RequestBody DishDTO dishDTO){
        log.info("添加一个新菜品, ",dishDTO);

        // 删除redis中的单个redis缓存
        String query_key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(query_key);

        // 数据库添加数据
        dishService.addWithFlaver(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "菜品的分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        //PageResult中存的应该是DishVO, 里面包含了CategoryName
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation(value = "删除一个菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("删除菜品, ", ids);

        // 数据库删除数据
        dishService.deleteDish(ids);

        //删除所有缓存
        clearCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取到dish对象")
    public Result<DishVO> queryDishById(@PathVariable("id") Long id, ServletRequest servletRequest){
        log.info("根据Id来查询菜品信息{}", id);
        DishVO dishVO = dishService.queryDishById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改dish")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改dish{}", dishDTO);

        // 数据库更新数据
        dishService.updateDish(dishDTO);

        // 删除redis中的所有缓存
        clearCache("dish_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起/停售菜品")
    public Result statusDish(@PathVariable("status") int status, long id){
        log.info("起/停售菜品: status: {}, id: {}", status, id);
        // 更新菜品信息，并获取到category id
        long category_id = dishService.statusDish(status, id);

        // 删除redis中的数据
        String query_key = "dish_" + category_id;
        redisTemplate.delete(query_key);

        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);

        // 构造查询条件：只查询起售中的菜品 (status = 1)以及属于这个category的菜品
        // sql语句只会对传入dish的非空属性进行查询，因此其结果就是一个和dish非空属性相同的列表。
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        List<Dish> list = dishService.list(dish);
        return Result.success(list);
    }
}
