package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping()
    @ApiOperation(value = "添加一个新菜品")
    public Result add(@RequestBody DishDTO dishDTO){
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
        dishService.deleteDish(ids);
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
        dishService.updateDish(dishDTO);
        return Result.success();
    }
}
