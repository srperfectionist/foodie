package com.test;

import com.sr.util.ElasticsearchUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author shirui
 * @date 2020/7/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchUtil.class)
public class EsTest {

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    @Test
    public void createIndex() throws IOException {
        System.out.println( elasticsearchUtil.searchPageHighLight("foodie-items-ik","itemName", "好吃的吃货",1,1));
    }
}
