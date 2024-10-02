package oit.is.z2411.kaizi.janken.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JankenController {

  // ユーザ名を受け取り、janken.htmlを表示するエンドポイント
  @GetMapping("/janken")
  public String jankenStart(@RequestParam String username, Model model) {
    model.addAttribute("username", username);
    return "janken";
  }

  // /play エンドポイントを追加し、じゃんけんの結果を表示する
  @GetMapping("/play")
  public String playJanken(@RequestParam String hand, Model model) {
    // CPUの手は固定でグーにする
    String cpuHand = "ぐー";
    String result = judgeResult(hand);

    // Thymeleaf に表示するためのデータをモデルに追加
    model.addAttribute("playerHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);

    // 結果を janken.html に表示する
    return "janken";
  }

  // 勝敗を判定するメソッド
  private String judgeResult(String playerHand) {
    String cpuHand = "ぐー"; // CPUの手をここで指定

    if (playerHand.equals(cpuHand)) {
      return "引き分け";
    } else if (playerHand.equals("ちょき")) {
      return "あなたの負け"; // チョキはグーに負ける
    } else if (playerHand.equals("ぱー")) {
      return "あなたの勝ち"; // パーはグーに勝つ
    } else {
      return "無効な手"; // 無効な手が選ばれた場合
    }
  }
}
