package oit.is.z2411.kaizi.janken.service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z2411.kaizi.janken.model.Match;
import oit.is.z2411.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {
  private final Logger logger = LoggerFactory.getLogger(AsyncKekka.class);

  boolean dbUpdated = false;

  @Autowired
  MatchMapper matchMapper;

  /**
   * 試合の結果を計算し、データベースを更新する
   *
   * @param match 対戦情報
   * @return 結果の文字列
   */
  @Transactional
  public String syncMatchResult(Match match) {
    String result = determineWinner(match.getUser1Hand(), match.getUser2Hand());
    match.setIsActive(false);
    matchMapper.updateMatchInactive(match);
    this.dbUpdated = true;
    return result;
  }

  /**
   * 非同期で試合の結果を送信する
   *
   * @param emitter SSEEmitter
   */
  @Async
  public void asyncMatchResult(SseEmitter emitter) {
    try {
      while (true) {
        Match match = matchMapper.selectActiveMatch();
        if (match != null) {
          String result = this.syncMatchResult(match);
          emitter.send(result);
          TimeUnit.MILLISECONDS.sleep(1000);

          // matchesテーブルのisActiveをfalseに更新
          match.setIsActive(false);
          matchMapper.updateMatchInactive(match);

          dbUpdated = false; // dbUpdated変数をfalseに
          break;
        }
        TimeUnit.MILLISECONDS.sleep(500);
      }
    } catch (Exception e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    } finally {
      emitter.complete();
    }
    System.out.println("asyncMatchResult complete");
  }

  /**
   * じゃんけんの勝敗を判定する
   *
   * @param hand1 ユーザー1の手
   * @param hand2 ユーザー2の手
   * @return 勝敗の結果
   */
  private String determineWinner(String hand1, String hand2) {
    if (hand1.equals(hand2)) {
      return "引き分け";
    }

    if ((hand1.equals("Gu") && hand2.equals("Choki"))
        || (hand1.equals("Choki") && hand2.equals("Pa"))
        || (hand1.equals("Pa") && hand2.equals("Gu"))) {
      return "User1の勝ち!";
    }

    return "User2の勝ち!";
  }
}
