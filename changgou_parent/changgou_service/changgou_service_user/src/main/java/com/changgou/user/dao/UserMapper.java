package com.changgou.user.dao;

import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:sz.itheima
 * @Description:User的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface UserMapper extends Mapper<User> {
    /**
     * 增加积分
     *
     * @param point
     */
    @Update("UPDATE  tb_user SET points=points+#{point} WHERE username=#{username}")
    void addPoints(@Param("point") Integer point, @Param("username") String username);
}
