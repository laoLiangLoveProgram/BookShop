package com.bookshop.service;


import com.bookshop.common.ServerResponse;
import com.bookshop.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
