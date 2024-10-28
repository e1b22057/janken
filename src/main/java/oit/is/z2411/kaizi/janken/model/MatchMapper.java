package oit.is.z2411.kaizi.janken.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {
  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand}, #{isActive})")
  void insertMatch(Match match);

  @Select("SELECT * FROM matches WHERE isActive = TRUE")
  Match selectActiveMatch();

  @Update("UPDATE matches SET isActive = FALSE WHERE user1 = #{user1} AND user2 = #{user2} AND isActive = TRUE")
  void updateMatchInactive(Match match);
}
