package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 更新购物车
     * @param shoppingCartDTO
     */
    public void updateCart(ShoppingCartDTO shoppingCartDTO){
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        // 获取到目前的user id
        long user_id = BaseContext.getCurrentId();
        shoppingCart.setUserId(user_id);

        // 假如已经在购物车中存在，就将数量加1
        List<ShoppingCart> carts =  shoppingCartMapper.get(shoppingCart);
        if(carts!=null && carts.size()>0){
            // 说明存在
            ShoppingCart cart = carts.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.update(cart);
            return;
        }

        // 假如不存在，就插入一条新数据
        if(shoppingCart.getDishId()!=null){
            // 假如需要插入的是一个dish
            Dish dish = dishMapper.getById(shoppingCart.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setImage(dish.getImage());
        }

        else if(shoppingCart.getSetmealId()!=null){
            // 假如插入是的是一个setmeal
            Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setImage(setmeal.getImage());
        }

        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查询购物车
     * @return
     */
    public List<ShoppingCart> listCart(){
        Long user_id = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryById(user_id);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     */
    public void cleanCart(){
        Long user_id = BaseContext.getCurrentId();
        shoppingCartMapper.cleanCart(user_id);
    }
}
