package com.example.demospringboot.dal.dao;

import com.example.demospringboot.dal.bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    @Insert({"insert into user (id,name,email) " +
            "values (#{id},#{name},#{email})"})
    int insert(User user);

    @Select("<script>" +
            "select count(*)" +
            " from user where name = #{name}" +
            "</script>")
    int count(@Param("name") String name);

    @Update({"update user" +
            " set email = #{email,jdbcType=VARCHAR}" +
            " where id = #{id}"})
    int updateById(User user);

    @Select({
            "select name from user limit 1000"
    })
    @Results({
            @Result(column = "name",property = "name",javaType = String.class)
    })
    List<String> queryAll();
}
