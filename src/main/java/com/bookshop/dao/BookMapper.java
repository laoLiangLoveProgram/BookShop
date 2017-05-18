package com.bookshop.dao;

import com.bookshop.pojo.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);

    List<Book> selectList();

    List<Book> selectByNameAndBookId(@Param("bookName")String bookName, @Param("bookId") Integer bookId);
}