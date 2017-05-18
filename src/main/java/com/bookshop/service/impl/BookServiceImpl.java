package com.bookshop.service.impl;

import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.dao.BookMapper;
import com.bookshop.dao.CategoryMapper;
import com.bookshop.pojo.Book;
import com.bookshop.pojo.Category;
import com.bookshop.service.IBookService;
import com.bookshop.util.DateTimeUtil;
import com.bookshop.util.PropertiesUtil;
import com.bookshop.vo.BookDetailVo;
import com.bookshop.vo.BookListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */
@Service("iBookService")
public class BookServiceImpl implements IBookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    //保存或更新产品
    public ServerResponse saveOrUpdateBook(Book book) {
        if (book != null) {
            if (StringUtils.isNotBlank(book.getSubImages())) {
                //约定以英文逗号分割不同的图片url
                String[] subImageArray = book.getSubImages().split(",");
                //将子图中的第一张图设为主图
                if (subImageArray.length > 0) {
                    book.setMainImage(subImageArray[0]);
                }
            }

            //如果更新产品，则id必须有
            if (book.getId() != null) {
                //默认更新产品时，所有字段都更新
                int rowCount = bookMapper.updateByPrimaryKey(book);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            } else {
                //插入全字段
                int rowCount = bookMapper.insert(book);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessMessage("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("需要新增或更新的产品的参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer bookId, Integer status) {
        if (bookId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(status);
        int rowCount = bookMapper.updateByPrimaryKeySelective(book);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    public ServerResponse<BookDetailVo> manageBookDetail(Integer bookId) {
        if (bookId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //VO对象--value Object
        //pojo --> bo(business object) --> vo(view object)
        BookDetailVo bookDetailVo = assembleBookDetailVo(book);

        return ServerResponse.createBySuccess(bookDetailVo);
    }

    //组装bookDetailVo的方法
    private BookDetailVo assembleBookDetailVo(Book book) {
        BookDetailVo bookDetailVo = new BookDetailVo();
        bookDetailVo.setId(book.getId());
        bookDetailVo.setCategoryId(book.getCategoryId());
        bookDetailVo.setName(book.getName());
        bookDetailVo.setSubtitle(book.getSubtitle());
        bookDetailVo.setMainImage(book.getMainImage());
        bookDetailVo.setSubImages(book.getSubImages());
        bookDetailVo.setDetail(book.getDetail());
        bookDetailVo.setPrice(book.getPrice());
        bookDetailVo.setStock(book.getStock());
        bookDetailVo.setStatus(book.getStatus());

        //imageHost，从配置文件中获取
        bookDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.bookshop.com/"));

        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(book.getCategoryId());
        if (category == null) {
            bookDetailVo.setParentCategoryId(0);    //默认根节点
        } else {
            bookDetailVo.setParentCategoryId(category.getParentId());
        }

        //createTime
        bookDetailVo.setCreateTime(DateTimeUtil.dateToStr(book.getCreateTime()));

        //updateTime
        bookDetailVo.setUpdateTime(DateTimeUtil.dateToStr(book.getUpdateTime()));
        return bookDetailVo;
    }

    public ServerResponse<PageInfo> getBookList(int pageNum, int pageSize) {
        //startPage-->start
        //填充自己的sql查询逻辑
        //pageHelper-->收尾

        PageHelper.startPage(pageNum, pageSize);
        List<Book> bookList = bookMapper.selectList();

        List<BookListVo> bookListVoList = Lists.newArrayList();
        for (Book book : bookList) {
            BookListVo bookListVo = assembleBookListVo(book);
            bookListVoList.add(bookListVo);
        }

        PageInfo pageResult = new PageInfo(bookList);
        pageResult.setList(bookListVoList);

        return ServerResponse.createBySuccess(pageResult);

    }

    public ServerResponse<PageInfo> searchBook(String bookName, Integer bookId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(bookName)) {
            bookName = new StringBuilder().append("%").append(bookName).append("%").toString();
        }
        List<Book> bookList = bookMapper.selectByNameAndBookId(bookName, bookId);


        List<BookListVo> bookListVoList = Lists.newArrayList();
        for (Book book : bookList) {
            BookListVo bookListVo = assembleBookListVo(book);
            bookListVoList.add(bookListVo);
        }

        PageInfo pageResult = new PageInfo(bookList);
        pageResult.setList(bookListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }


    private BookListVo assembleBookListVo(Book book) {
        BookListVo bookListVo = new BookListVo();
        bookListVo.setId(book.getId());
        bookListVo.setCategoryId(book.getCategoryId());

        bookListVo.setName(book.getName());
        bookListVo.setSubtitle(book.getSubtitle());
        bookListVo.setMainImage(book.getMainImage());
        bookListVo.setPrice(book.getPrice());
        bookListVo.setStatus(book.getStatus());
        bookListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.bookshop.com/"));
        return bookListVo;
    }

}
