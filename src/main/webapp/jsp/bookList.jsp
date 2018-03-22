<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/24
  Time: 15:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>图书列表</title>
    <link rel="stylesheet" type="text/css" href="../js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../js/easyui/themes/icon.css">
    <script type="text/javascript" src="../js/easyui/jquery.js"></script>
    <script type="text/javascript" src="../js/easyui/jquery.easyui.min.js"></script>
    <style type="text/css">
        .wrap {
            margin: 0 auto;
            width: 960px;
        }

        #header #logo {
            background: url(../images/logo.gif);
            width: 263px;
            height: 56px;
            text-indent: -1000em;
        }

        #content {
            margin-top: 20px;;
        }

        #footer {
            height: 40px;
            text-align: center;
            line-height: 32px;
            font-size: 16px;
            color: #aaa;
        }

    </style>
    <script type="text/javascript">


    </script>
</head>
<body>
<div id="cc" class="wrap easyui-layout" style="width:600px;height:400px;">
    <div id="header" data-options="region:'north',title:'North Title',split:true," style="height:100px;">
        <div id="logo">博达网上书城</div>
    </div>
    <div id="footer" data-options="region:'south',title:'South Title',split:true" style="height:100px;">
        版权所有@博达远创www.itbdyc.com
    </div>
    <div id="content" data-options="region:'center',title:'center title'" style="padding:5px;background:#eee;"></div>
</div>
</body>
</html>
