package com.sr.pojo.vo;

import com.sr.pojo.Items;
import com.sr.pojo.ItemsImg;
import com.sr.pojo.ItemsParam;
import com.sr.pojo.ItemsSpec;
import lombok.*;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/18
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
