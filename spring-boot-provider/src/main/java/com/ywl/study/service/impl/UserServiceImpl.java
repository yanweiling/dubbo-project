package com.ywl.study.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.ywl.study.dubbo.core.domain.User;
import com.ywl.study.dubbo.core.service.UserService;


@Service
public class UserServiceImpl  implements UserService {
    @Override
    public User saveUser(User user) {
        user.setId(1);
        System.out.println(user.toString());
        return user;
    }
}
