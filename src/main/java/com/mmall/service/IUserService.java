package com.mmall.service;

import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;

public interface IUserService {

    ServerRespose<User> login(String username, String password);

}
