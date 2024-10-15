package oit.is.z2411.kaizi.janken.controller;

import java.security.Principal;

import oit.is.z2411.kaizi.janken.model.Janken;
import oit.is.z2411.kaizi.janken.model.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JankenController {
  private Janken janken; // Jankenクラスのインスタンス
  @Autowired
  private Entry entry;

  public JankenController() {
    this.janken = new Janken();
  }

  // ユーザ名を受け取り、janken.htmlを表示するエンドポイント
  @GetMapping("/janken")
  public String jankenStart(Principal principal, Model model) {
    String username = principal.getName(); // ログインしているユーザ名を取得
    entry.addUser(username);
    int entryCount = entry.getUsers().size(); // エントリー人数を数える
    model.addAttribute("username", username);
    model.addAttribute("entryUsers", entry.getUsers()); // ログイン中の全ユーザを表示
    model.addAttribute("entryCount", entryCount); // 現在エントリーしているユーザ数
    return "janken";
  }

  // /play エンドポイントを追加し、じゃんけんの結果を表示する
  @GetMapping("/play")
  public String playJanken(@RequestParam String hand, Model model) {
    String cpuHand = janken.cpuHand(); // CPUの手をランダムに決定
    String result = janken.judgeResult(hand, cpuHand);

    // Thymeleaf に表示するためのデータをモデルに追加
    model.addAttribute("playerHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);

    // 結果を janken.html に表示する
    return "janken";
  }
}
