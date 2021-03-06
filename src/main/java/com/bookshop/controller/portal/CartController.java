package com.bookshop.controller.portal;

import com.bookshop.common.Const;
import com.bookshop.common.ResponseCode;
import com.bookshop.common.ServerResponse;
import com.bookshop.controller.common.UserSessionUtil;
import com.bookshop.pojo.User;
import com.bookshop.service.ICartService;
import com.bookshop.util.CookieUtil;
import com.bookshop.util.JsonUtil;
import com.bookshop.util.RedisShardedPoolUtil;
import com.bookshop.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/5/25.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest request) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iCartService.list(user.getId());
    }


    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest request, Integer count, Integer bookId) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iCartService.add(user.getId(), bookId, count);
    }


    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest request, Integer count, Integer bookId) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iCartService.update(user.getId(), bookId, count);
    }

    @RequestMapping("delete_book.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteBook(HttpServletRequest request, String bookIds) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iCartService.deleteBook(user.getId(), bookIds);
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);


        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    //全反选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest request) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);


        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    //单独选
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest request, Integer bookId) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iCartService.selectOrUnSelect(user.getId(), bookId, Const.Cart.CHECKED);
    }

    //单独反选
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpServletRequest request, Integer bookId) {
        //是否登录已经在拦截器中进行了校验
        User user = (User) request.getAttribute(Const.CURRENT_USER);

        return iCartService.selectOrUnSelect(user.getId(), bookId, Const.Cart.UN_CHECKED);
    }


    //查询当前用户的购物车里的产品数量,如果一个用户有10个产品, 那么数量就是10
    @RequestMapping("get_cart_book_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartBookCount(HttpServletRequest request) {
        String sessionId = CookieUtil.readLoginToken(request);
        if (sessionId == null) {
            //未登录的用户的购物车中产品数量为0
            return ServerResponse.createBySuccess(0);
        }
        String userJsonStr = RedisShardedPoolUtil.get(sessionId);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null) {
            //未登录的用户的购物车中产品数量为0
            return ServerResponse.createBySuccess(0);
        }

        return iCartService.getCartBookCount(user.getId());

    }

}
