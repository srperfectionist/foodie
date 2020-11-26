package com.sr.service;

import com.sr.utils.PageGridResult;

import java.io.IOException;

/**
 * @author shirui
 * @date 2020/11/23
 */
public interface IItemsESService {

    PageGridResult searchItems(String keyword, String sort, Integer page, Integer pageSize) throws IOException;
}
