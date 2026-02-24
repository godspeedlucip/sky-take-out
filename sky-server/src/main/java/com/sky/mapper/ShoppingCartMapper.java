package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 查找
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> get(ShoppingCart shoppingCart);

    /**
     * 更新一个购物车项目
     * @param shoppingCart
     */
    void update(ShoppingCart shoppingCart);

    /**
     * 插入一个新纪录
     * @param shoppingCart
     */
//    insert into shopping_cart as cart left join dish setmeal as sm
//    on cart.dish_id = dish.id and cart.setmeal_id = sm.id
//
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id来查询所有的购物车
     * @param userId
     * @return
     */

    List<ShoppingCart> queryById(Long userId);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void cleanCart(Long userId);

    /**
     * 根据user id来删除购物车数据
     * @param userId
     */
    void clearById(long userId);
}

