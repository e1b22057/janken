package oit.is.z2411.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchInfoMapper {
  @Select("SELECT * FROM matchinfo WHERE user1 = #{user1} AND user2 = #{user2} AND isActive = TRUE")
  MatchInfo selectActiveMatch(int user1, int user2);

  // 自分のIDが含まれていて、isActiveがTRUEのマッチを取得
  @Select("SELECT * FROM matchinfo WHERE (user1 = #{userId} OR user2 = #{userId}) AND isActive = TRUE")
  MatchInfo selectActiveMatchInfoByUserId(int userId);

  @Insert("INSERT INTO matchinfo (user1, user2, user1Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  void insertMatchInfo(MatchInfo matchInfo);

  @Update("UPDATE matchinfo SET isActive = FALSE WHERE id = #{id}")
  void updateMatchInfo(MatchInfo matchInfo);

  @Select("SELECT * FROM matchinfo")
  ArrayList<MatchInfo> findActiveMatches();
}
