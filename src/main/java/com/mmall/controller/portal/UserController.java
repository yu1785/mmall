package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public ServerRespose<User> login(String username, String password, HttpSession session) {
        ServerRespose<User> respose = iUserService.login(username, password);
        if (respose.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,respose.getData());
        }
        return respose;
    }

}
