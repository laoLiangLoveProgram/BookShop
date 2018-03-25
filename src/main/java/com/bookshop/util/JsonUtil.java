package com.bookshop.util;/**
 * Created by Administrator on 2018/3/25.
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;

/**
 * @program: book_shop
 * @description:
 * @author: LaoLiang
 * @create: 2018-03-25 01:49
 **/
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();


    static {
        init();
    }

    /**
     * JsonUtil的初始化方法
     */
    private static void init() {
        //设置影响序列化的行为

        //ALWAYS : 对象的所有字段全部列入
        //NON_NULL : 非空的才会有
        //NON_DEFAULT : 如果不是默认值的才会有, 默认值是指对象在初始化的时候赋予的值
        //NON_EMPTY : String不是"",str.length!=0,  Collection不是0
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

        //忽略空Bean转json的错误, true表示如果一个bean类没有setter,getter方法, 则会抛一个异常
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        //所有的日期格式 都统一为 "yyyy-MM-dd HH:mm:ss"
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //反序列化时 忽略 在json字符串中存在, 但是在java对象中不存在对应属性的情况, 防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    /**
     * 对象转json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error", e);
            return null;
        }
    }

    /**
     * 返回格式化好的json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error", e);
            return null;
        }
    }

    /**
     * 字符串转对象
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    /**
     * 字符串转复杂对象, 多泛型类对象
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }

        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }


    /**
     * 字符串转复杂类型对象, 多泛型
     *
     * @param str
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);

        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

}
