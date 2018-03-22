package com.bookshop.controller.portal;

import com.bookshop.common.ServerResponse;
import com.bookshop.service.IBookService;
import com.bookshop.vo.BookDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/5/19.
 */
@Controller
@RequestMapping("/book")
public class BookController {

    @Autowired
    private IBookService iBookService;

    /**
     * 前台获取书籍详细信息
     *
     * @param bookId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<BookDetailVo> detail(Integer bookId) {

        return iBookService.getBookDetail(bookId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false) String keyword,
                                         @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "rows", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {

        return iBookService.getBookByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
