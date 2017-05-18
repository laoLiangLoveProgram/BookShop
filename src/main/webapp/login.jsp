<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/17
  Time: 11:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
    <script type="application/javascript" src="js/jquery.js"></script>
    <script type="application/javascript" src="js/login.js"></script>
</head>
<body>
<div id="header" class="wrap">
    <div id="logo">博达网上书城</div>
    <div id="navbar">
        <form method="get" name="search" action="">
            搜索：<input class="input-text" type="text" name="keywords"/><input class="input-btn" type="submit"
                                                                             name="submit" value=""/>
        </form>
    </div>
</div>
<div id="login">
    <h2>用户登陆</h2>
    <form method="post" id="login_form">
                <dl>
                <dt>用户名：</dt>
                <dd><input class="input-text" type="text" name="username"/><span id="span_username"></span></dd>
            </tr>
            <dt>密　码：</dt>
            <dd><input class="input-text" type="password" name="password"/><span id="span_password"></span></dd>
            <dt></dt>
            <dd class="button"><input class="input-btn" type="button" name="submit" value=""/>
                <input class="input-reg" type="button" name="register" value="" onclick=";"/>
            </dd>
            </dl>
    </form>
</div>
<div id="footer" class="wrap">
    博达网上书城 &copy; 版权所有
</div>
</body>
</html>

