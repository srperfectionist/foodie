package com.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sr.Application;
import com.sr.pojo.ElasticEntity;
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
        Map map = Maps.newHashMap();
        List list = Lists.newArrayList();
        for(int i = 0; i <= 100; i++){
            User user = new User();
            user.setId(String.valueOf(i));
            user.setName("测试" + i);

            list.add(user);
        }
//        User user = new User();
//        user.setId(String.valueOf(1));
//        user.setName("测试");
//        elasticsearchUtil.createDoc("bulk",user);
       // elasticsearchUtil.createIndex("bulk");
       // elasticsearchUtil.createDocBatch("bulk",list);
        //System.out.println(elasticsearchUtil.searchPage("bulk", "name", "测",1,10));;
        //System.out.println(elasticsearchUtil.searchById("bulk","userList"));
        /*List<User> users = elasticsearchUtil.searchPageHighLight("bulk", "name", "测", 1, 60, User.class);
        for (User user : users) {
            System.out.println(user);
        }*/
        System.out.println( elasticsearchUtil.searchPageHighLight("bulk","name", "测试",1,60));
    }
}
