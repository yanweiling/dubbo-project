package com.ywl.study.dubbo.core.service;


import com.ywl.study.dubbo.core.domain.City;

public interface CityDubboService {
    /**
     * 根据城市名称，查询城市信息
     * @param cityName
     */
    City findCityByName(String cityName);
}
