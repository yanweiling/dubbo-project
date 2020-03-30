package com.ywl.study.rpc.service;


import com.ywl.study.rpc.domain.City;

public interface CityDubboService {
    /**
     * 根据城市名称，查询城市信息
     * @param cityName
     */
    City findCityByName(String cityName);
}
