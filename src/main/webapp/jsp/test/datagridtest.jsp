<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/23
  Time: 14:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>table</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript">
        $(function () {
            $("#dg").datagrid({
                url: "${pageContext.request.contextPath}/js/products.json",
                width: 500,
                columns: [[
                    {field: "productid", title: "ID", width: 100},
                    {field: "productname", title: "名称", width: 100}
                ]]
            });
        });

    </script>
</head>
<body>
<table id="dg"></table>


</body>
</html>
