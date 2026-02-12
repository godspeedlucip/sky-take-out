package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImple implements UserService {

    private static final String WX_LOGIN_SITE = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    UserMapper userMapper;

    public User wxLogin(UserLoginDTO userLoginDTO){

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("grant_type", "authorization_code");
        map.put("js_code", userLoginDTO.getCode());

        String result = HttpClientUtil.doGet(WX_LOGIN_SITE, map);

        JSONObject jsonObject = JSON.parseObject(result);
        String openid = jsonObject.getString("openid");

        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 检查是否是新用户
        User user = userMapper.queryUserId(openid);
        if(user==null){
            // 插入一个新用户
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insertUser(user);
        }

        // 返回user
        return user;
    }
}
