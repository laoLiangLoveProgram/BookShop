package com.bookshop.dao;

import com.bookshop.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 获取用户名的数量，校验用户名是否存在
     * @param username
     * @return 返回查询到的用户名的数量
     */
    int checkUsername(String username);

    /**
     * 获取Email的数量，校验Email
     * @param email
     * @return
     */
    int checkEmail(String email);
    /**
     * 验证用户名密码正确
     * mybatis传递多个参数需要添加@Param("xxx")注解，并且sql对应的是xxx
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username") String username, @Param("password") String password);

    /**
     * 通过用户名获取密码提示问题
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);

    /**
     * 验证密码提示答案的正确性
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int checkPassword(@Param("password") String password, @Param("userId") Integer userId);

    /**
     * 校验除了userId的用户以外的其他用户是否有相同的email存在
     * @param email
     * @param userId
     * @return
     */
    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);

}