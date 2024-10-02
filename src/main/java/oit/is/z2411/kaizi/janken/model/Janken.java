package oit.is.z2411.kaizi.janken.model;

import java.util.Random;

public class Janken {
  private static final String[] HANDS = { "ぐー", "ちょき", "ぱー" };
  private Random random;

  public Janken() {
    this.random = new Random();
  }

  // CPUの手をランダムに決定するメソッド
  public String cpuHand() {
    int index = random.nextInt(HANDS.length);
    return HANDS[index];
  }

  // 勝敗を判定するメソッド
  public String judgeResult(String playerHand, String cpuHand) {
    if (playerHand.equals(cpuHand)) {
      return "引き分け";
    } else if ((playerHand.equals("ぐー") && cpuHand.equals("ちょき")) ||
        (playerHand.equals("ちょき") && cpuHand.equals("ぱー")) ||
        (playerHand.equals("ぱー") && cpuHand.equals("ぐー"))) {
      return "あなたの勝ち";
    } else {
      return "あなたの負け";
    }
  }
}
