package com.bookshop.controller.backend;

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.common.UserSessionUtil;
import com.bookshop.pojo.Book;
import com.bookshop.pojo.User;
import com.bookshop.service.IBookService;
import com.bookshop.service.IFileService;
import com.bookshop.service.IUserService;
import com.bookshop.util.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/17.
 */
@Controller
@RequestMapping("/manage/book")
public class BookManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IBookService iBookService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse bookSave(HttpServletRequest request, Book book) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //添加或更新产品的业务逻辑
        return iBookService.saveOrUpdateBook(book);

    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request, Integer bookId, Integer status) {
        //已经通过拦截器检验了是否登录和是否为管理员
        return iBookService.setSaleStatus(bookId, status);

    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest request, Integer bookId) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务
        return iBookService.manageBookDetail(bookId);

    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpServletRequest request,
                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务
        return iBookService.getBookList(pageNum, pageSize);

    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse bookSearch(HttpServletRequest request, String bookName, Integer bookId,
                                     @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务
        return iBookService.searchBook(bookName, bookId, pageNum, pageSize);

    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            return ServerResponse.createByErrorMessage("上传文件失败");
        }

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccess(fileMap);

    }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpServletRequest request, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        //已经通过拦截器检验了是否登录和是否为管理员
        //业务
        //注意 富文本中对于返回值有自己的要求, 使用的是simditor, 则按照simditor的要求返回
        //并且设置response的头
//            {
//                "success" : true/false,
//                "msg" : "error message", # optional
//                "file_path" : "[real file path]"
//            }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);
        response.setHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;

    }


}
