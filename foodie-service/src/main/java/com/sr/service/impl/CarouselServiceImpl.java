package com.sr.service.impl;

import com.sr.mapper.CarouselMapper;
import com.sr.pojo.Carousel;
import com.sr.service.ICarouselService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/1
 */
@Service("iCarouselService")
@Slf4j
public class CarouselServiceImpl implements ICarouselService {

    private CarouselMapper carouselMapper;

    @Autowired
    public void setCarouselMapper(CarouselMapper carouselMapper) {
        this.carouselMapper = carouselMapper;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<Carousel> getCarousel(Integer isShow) {
        Example carouselExample = new Example(Carousel.class);
        carouselExample.orderBy("sort").asc();
        Example.Criteria carouselExampleCriteria = carouselExample.createCriteria();
        carouselExampleCriteria.andEqualTo("isShow", isShow);

        List<Carousel> carouselList = carouselMapper.selectByExample(carouselExample);

        return carouselList;
    }
}
