package com.ywl.study.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ywl.study.rpc.domain.City;
import com.ywl.study.rpc.service.CityDubboService;


@Service
public class CityDubboServiceImpl implements CityDubboService {
    public City findCityByName(String cityName) {
        return new City(1L,2L,"广州","是我的故乡");
    }
}
