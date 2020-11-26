package com.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sr.Application;
import com.sr.pojo.ElasticEntity;
import com.sr.pojo.Items;
import com.sr.pojo.User;
import com.sr.util.ElasticsearchUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shirui
 * @date 2020/7/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class EsTest {

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    @Test
    public void createIndex() throws IOException {
        System.out.println( elasticsearchUtil.searchPageHighLight("foodie-items-ik","itemName", "好吃的吃货",1,1));
    }
}
