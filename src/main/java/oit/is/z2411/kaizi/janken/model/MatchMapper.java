package oit.is.z2411.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {

  @Select("SELECT * FROM matches")
  ArrayList<Match> selectAllMatches();

  // idを利用してUserを取得するメソッドを追加
  @Select("SELECT * FROM users WHERE id = #{id}")
  User selectUserById(int id);

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand) VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand})")
  @Options(useGeneratedKeys = true, keyProperty = "id") // idを自動生成
  void insertMatch(Match match);
}
