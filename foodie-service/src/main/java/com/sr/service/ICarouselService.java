package com.sr.service;

import com.sr.pojo.Carousel;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/1
 */
public interface ICarouselService {

    /**
     * 获取首页轮播图
     * @param isShow
     * @return
     */
    List<Carousel> getCarousel(Integer isShow);
}
