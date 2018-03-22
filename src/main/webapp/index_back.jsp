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
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
    <link type="text/css" rel="stylesheet" href="js/themes/default/easyui.css"/>
    <link rel="stylesheet" type="text/css" href="js/themes/icon.css">
    <script type="text/javascript" src="js/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>

    <script type="application/javascript">
        function getList() {
            var keyword = $("input[name=keyword]").val();
            var categoryId = 1;
            var pageNum = $("input[class=pagination-num]").val();
            var pageSize = $("select[class=pagination-page-list]").find("option:selected").text();
            var orderBy = "";
//            alert(keyword);
//            alert(pageNum);
//            alert(pageSize);
            $.ajax({
                type: "get",
                url: "/book/list.do?keyword=" + keyword + "&categoryId=" + categoryId + "&pageNum=" + pageNum + "&pageSize=" + pageSize + "&orderBy=" + orderBy,
                success: function (response) {
                    console.log(response);
                    if (response.status == 0) {
                        var book_list = response.data.list;
                        console.log(book_list);
                        for (var i = 0; i < book_list.length; i++) {
                            console.log(i);
                            console.log(book_list[i]);
                            $("#book_list tbody").append('<tr></tr>');
                            $("#book_list tbody tr:last-child").append('<td><input type="checkbox" name="bookId" value="' + book_list[i].id + '"/></td>');
                            $("#book_list tbody tr:last-child").append('<td class="title">' + book_list[i].name + '</td>');
                            $("#book_list tbody tr:last-child").append('<td>&yen;' + book_list[i].price + '</td>');
                            $("#book_list tbody tr:last-child").append('<td>' + book_list[i].stock + '</td>');
                            $("#book_list tbody tr:last-child").append('<td class="thumb"><img src="' + book_list[i].imageHost + book_list[i].mainImage + '" /></td>');
                        }
                    }

                }
            });
        }

        $(function () {
            $('#pp').pagination({
                total: 100,
                pageSize: 123,
                pageNum: 2,
                pageList: [5, 10, 20, 30, 50]
            });
            getList();

        });
    </script>

</head>
<body>
<div id="header" class="wrap">
    <div id="logo">博达网上书城</div>
    <div id="navbar">
        <div class="userMenu">
            <ul>
                <li class="current"><a href="index.jsp">User首页</a></li>
                <li><a href="jsp/orderlist.jsp">我的订单</a></li>
                <li><a href="jsp/shopping.jsp">购物车</a></li>
                <li><a href="javascript:;" id="logout">注销</a></li>
            </ul>
        </div>
        <form method="get" name="search" action="">
            搜索：<input class="input-text" type="text" name="keyword"/><input class="input-btn" type="submit"
                                                                            name="submit" value=""/>
        </form>
    </div>
</div>
<div id="content" class="wrap">
    <div class="list bookList">
        <form method="post" name="shoping" action="jsp/shopping.jsp">
            <table id="book_list">
                <tr class="title">
                    <th class="checker"></th>
                    <th>书名</th>
                    <th class="price">价格</th>
                    <th class="store">库存</th>
                    <th class="view">图片预览</th>
                </tr>
            </table>
            <div id="pp" style="background:#efefef;border:1px solid #ccc;"></div>
            <div class="button"><input class="input-btn" type="submit" name="submit" value=""/></div>
        </form>
    </div>
</div>
<div id="footer" class="wrap">
    博达网上书城 &copy; 版权所有
</div>
</body>
</html>
