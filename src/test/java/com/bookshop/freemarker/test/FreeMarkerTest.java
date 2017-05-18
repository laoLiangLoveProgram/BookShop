package com.bookshop.freemarker.test;

import com.bookshop.pojo.test.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/18.
 */
public class FreemarkerTest {
    @Test
    public void testFreemarker() throws IOException, TemplateException {
        //1. 创建一个模版文件
        //hello.ftl

        //2. 创建一个Configuration对象
        Configuration configuration = new Configuration(Configuration.getVersion());

        //3. 设置模版所在的路径
        configuration.setDirectoryForTemplateLoading(new File("E:\\IdeaProjects\\BookShop\\src\\main\\webapp\\WEB-INF\\ftl"));

        //4. 设置模版的字符集,UTF-8
        configuration.setDefaultEncoding("UTF-8");

        //5. 使用Configuration对象加载一个文件,需要指定模版文件的文件名
//        Template template = configuration.getTemplate("hello.ftl");
        Template template = configuration.getTemplate("student.ftl");

        //6. 创建一个数据集, 可以是pojo也可以使map, 推荐使用map
        Map data = new HashMap();
        data.put("hello", "hello freemarker");
        Student student = new Student(1, "小明", 22, "北京");
        data.put("student", student);
        List<Student> list = new ArrayList<>();
        list.add(new Student(2, "小A", 23, "北京"));
        list.add(new Student(3, "小B", 24, "北京"));
        list.add(new Student(4, "小C", 25, "北京"));
        list.add(new Student(5, "小D", 26, "北京"));
        list.add(new Student(6, "小E", 27, "北京"));
        list.add(new Student(7, "小F", 28, "北京"));
        list.add(new Student(8, "小G", 29, "北京"));
        data.put("stuList", list);

        //7. 创建一个Writer对象,指定输出文件的路径及文件名
//        Writer out = new FileWriter(new File("D:\\ftpfile\\hello.txt"));
        Writer out = new FileWriter(new File("D:\\ftpfile\\student.html"));
        //8. 使用模版对象的process方法输出文件.
        template.process(data, out);

        //9. 关闭流
        out.close();
    }
}
