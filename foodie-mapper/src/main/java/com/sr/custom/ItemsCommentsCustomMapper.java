package com.sr.custom;

import com.sr.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author shirui
 * @date 2020/2/24
 */
public interface ItemsCommentsCustomMapper {

    void saveComments(@Param("paramsMap") Map<String, Object> maps);

    List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> maps);
}
