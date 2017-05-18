<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/17
  Time: 11:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title></title>
    <link type="text/css" rel="stylesheet" href="css/style.css" />
</head>
<body>
<div id="header" class="wrap">
    <div id="logo">博达网上书城</div>
    <div id="navbar">
        <div class="userMenu">
            <ul>
                <li class="current"><a href="index.jsp">User首页</a></li>
                <li><a href="orderlist.jsp">我的订单</a></li>
                <li><a href="shopping.jsp">购物车</a></li>
                <li><a href="javascript:;" id="logout">注销</a></li>
            </ul>
        </div>
        <form method="get" name="search" action="">
            搜索：<input class="input-text" type="text" name="keywords" /><input class="input-btn" type="submit" name="submit" value="" />
        </form>
    </div>
</div>
<div id="content" class="wrap">
    <div class="list bookList">
        <form method="post" name="shoping" action="shopping.jsp">
            <table>
                <tr class="title">
                    <th class="checker"></th>
                    <th>书名</th>
                    <th class="price">价格</th>
                    <th class="store">库存</th>
                    <th class="view">图片预览</th>
                </tr>
                <tr>
                    <td><input type="checkbox" name="bookId" value="1" /></td>
                    <td class="title">泰戈尔诗集</td>
                    <td>￥18.00</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_01.gif" /></td>
                </tr>
                <tr class="odd">
                    <td><input type="checkbox" name="bookId" value="2" /></td>
                    <td class="title">痕记</td>
                    <td>￥22.80</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_02.gif" /></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="bookId" value="3" /></td>
                    <td class="title">天堂之旅</td>
                    <td>￥25.00</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_03.gif" /></td>
                </tr>
                <tr class="odd">
                    <td><input type="checkbox" name="bookId" value="4" /></td>
                    <td class="title">钱钟书集（全10册）</td>
                    <td>￥332.50</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_04.gif" /></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="bookId" value="5" /></td>
                    <td class="title">赵俪生高昭—夫妻回忆录</td>
                    <td>￥38.00</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_05.gif" /></td>
                </tr>
                <tr class="odd">
                    <td><input type="checkbox" name="bookId" value="6" /></td>
                    <td class="title">无聊斋（张绍刚首部随笔杂文作品）</td>
                    <td>￥28.00</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_06.gif" /></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="bookId" value="7" /></td>
                    <td class="title">一颗热土豆是一张温馨的床</td>
                    <td>￥38.00</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_07.gif" /></td>
                </tr>
                <tr class="odd">
                    <td><input type="checkbox" name="bookId" value="8" /></td>
                    <td class="title">李戡戡乱记</td>
                    <td>￥22.00</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_08.gif" /></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="bookId" value="9" /></td>
                    <td class="title">生生世世未了缘</td>
                    <td>￥17.50</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_09.gif" /></td>
                </tr>
                <tr class="odd">
                    <td><input type="checkbox" name="bookId" value="10" /></td>
                    <td class="title">一生有多少爱</td>
                    <td>￥17.50</td>
                    <td>999</td>
                    <td class="thumb"><img src="images/book/book_10.gif" /></td>
                </tr>
            </table>
            <div class="page-spliter">
                <a href="#">&lt;</a>
                <a href="#">首页</a>
                <span class="current">1</span>
                <a href="#">2</a>
                <a href="#">3</a>
                <a href="#">4</a>
                <span>...</span>
                <a href="#">尾页</a>
                <a href="#">&gt;</a>
            </div>
            <div class="button"><input class="input-btn" type="submit" name="submit" value="" /></div>
        </form>
    </div>
</div>
<div id="footer" class="wrap">
    博达网上书城 &copy; 版权所有</div>
</body>
</html>
