<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>测试页面</title>
</head>
<body>
学生信息: <br>
学号: ${student.id} <br>
姓名: ${student.name} <br>
年龄: ${student.age} <br>
地址: ${student.address} <br>
学生列表:
<table border="1">
    <tr>
        <th>编号</th>
        <th>学号</th>
        <th>姓名</th>
        <th>年龄</th>
        <th>地址</th>
    </tr>
<#list stuList as stu>
    <#if stu_index%2==0>
    <tr bgcolor="#888">
    <#else >
    <tr bgcolor="white">
    </#if>
    <td>${stu_index}</td>
    <td>${stu.id}</td>
    <td>${stu.name}</td>
    <td>${stu.age}</td>
    <td>${stu.address}</td>
</tr>
</#list>
</table>

</body>
</html>