package com.ywl.study.controller;


import com.ywl.study.service.CityDubboConsumerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CityDubboConsumerService service;

    @GetMapping("/save")
    @ResponseBody
    public Object saveUser() {

        return service.saveUser();
    }
}
