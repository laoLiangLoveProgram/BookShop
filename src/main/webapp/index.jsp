<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/19
  Time: 11:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
<h1>Tomcat1</h1>
<h1>Tomcat1</h1>
<h1>Tomcat1</h1>
<h1>Tomcat1</h1>
springmvc上传文件
<form name="form1" action="/manage/book/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc上传文件"/>
</form>
富文本上传文件
<form name="form2" action="/manage/book/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本上传文件"/>
</form>

</body>
</html>
