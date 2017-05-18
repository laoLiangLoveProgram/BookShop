package com.bookshop.service;

import com.bookshop.common.ServerResponse;
import com.bookshop.pojo.Book;
import com.bookshop.vo.BookDetailVo;
import com.github.pagehelper.PageInfo;

/**
 * Created by Administrator on 2017/5/17.
 */
public interface IBookService {

    ServerResponse saveOrUpdateBook(Book book);

    ServerResponse<String> setSaleStatus(Integer bookId, Integer status);

    ServerResponse<BookDetailVo> manageBookDetail(Integer bookId);

    ServerResponse<PageInfo> getBookList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchBook(String bookName, Integer bookId, int pageNum, int pageSize);
}
