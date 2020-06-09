package com.mmall.service.impl;

import com.mmall.common.ServerRespose;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerRespose<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerRespose.createByErrorMessage("用户名不存在");
        }

        //密码登录 MD5 //TODO

        User user = userMapper.selectLogin(username, password);
        if (user == null){
            return ServerRespose.createByErrorMessage("密码错误");
        }

        // 为什么设置密码为空  //TODO
        user.setPassword(StringUtils.EMPTY);
        return ServerRespose.createBySuccess("登录成功",user);
    }

}
