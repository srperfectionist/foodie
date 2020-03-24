package com.sr.utils;

import lombok.*;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/26
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PageGridResult {

    private Integer page;
    private Integer total;
    private Long records;
    private List<?> rows;
}
