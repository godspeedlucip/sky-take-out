package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 查询是否存在openid对应的user
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User queryUserId(String openid);

    /**
     * 插入一个新用户
     * @param user
     */

    void insertUser(User user);
}
