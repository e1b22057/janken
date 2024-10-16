package oit.is.z2411.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

  @Select("SELECT * FROM users")
  ArrayList<User> selectAllUsers();

  @Select("SELECT * FROM users WHERE id = #{id}")
  User selectUserById(int id);

  // 名前を利用してUserを取得するメソッドを追加
  @Select("SELECT * FROM users WHERE name = #{name}")
  User selectUserByName(String name);
}
