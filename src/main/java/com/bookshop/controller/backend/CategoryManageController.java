package com.bookshop.controller.backend;

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.common.UserSessionUtil;
import com.bookshop.pojo.User;
import com.bookshop.service.ICategoryService;
import com.bookshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/5/16.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, "用户未登录，请登录管理员");
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = (User) serverResponse.getData();

        //校验是否是管理员
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //是管理员，增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 更新category的name
     *
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest request, Integer categoryId, String categoryName) {
        //判断是否登录
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, "用户未登录，请登录管理员");
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = (User) serverResponse.getData();

        //校验是否是管理员
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //更新CategoryName
            return iCategoryService.updateCategoryName(categoryId, categoryName);

        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 根据categoryId获取下边平级的category信息，不递归
     *
     */
    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        //判断是否登录
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, "用户未登录，请登录管理员");
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = (User) serverResponse.getData();

        //校验是否是管理员
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    /**
     * 获取当前category的id和递归子节点的id
     *
     */
    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        //判断是否登录
        ServerResponse serverResponse = UserSessionUtil.getUserFromSession(request, "用户未登录，请登录管理员");
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = (User) serverResponse.getData();

        //校验是否是管理员
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //查询当前category的id和递归子节点的id
            //0-->1000-->10000
            return iCategoryService.selectCategoryAndChildrenById(categoryId);

        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
