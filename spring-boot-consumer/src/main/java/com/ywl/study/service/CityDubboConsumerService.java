package com.ywl.study.service;

import com.alibaba.dubbo.config.annotation.Reference;

import com.ywl.study.rpc.domain.City;
import com.ywl.study.rpc.domain.User;
import com.ywl.study.rpc.service.CityDubboService;
import com.ywl.study.rpc.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CityDubboConsumerService {
    @Reference
    CityDubboService cityDubboService;

    @Reference
    UserService userService;

    public void printCity() {
        String cityName = "广州";
        City city = cityDubboService.findCityByName(cityName);
        System.out.println(city.toString());
    }


    public User saveUser() {
        User user = new User();
        user.setUsername("jaycekon")
                .setPassword("jaycekong824");
        return userService.saveUser(user);
    }
}
