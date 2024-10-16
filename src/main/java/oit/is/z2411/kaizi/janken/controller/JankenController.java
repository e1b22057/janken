package oit.is.z2411.kaizi.janken.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z2411.kaizi.janken.model.Match;
import oit.is.z2411.kaizi.janken.model.MatchMapper;
import oit.is.z2411.kaizi.janken.model.User;
import oit.is.z2411.kaizi.janken.model.UserMapper;

@Controller
public class JankenController {

  @Autowired
  UserMapper userMapper;

  @Autowired
  MatchMapper matchMapper; // MatchMapperを@Autowiredで注入する必要があります。

  @GetMapping("/janken")
  public String janken(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    model.addAttribute("loginUser", loginUser);

    ArrayList<User> users = userMapper.selectAllUsers();
    model.addAttribute("users", users);

    ArrayList<Match> matches = matchMapper.selectAllMatches();
    for (Match match : matches) {
      User user1 = userMapper.selectUserById(match.getUser1());
      User user2 = userMapper.selectUserById(match.getUser2());
      String result = determineWinner(match.getUser1Hand(), match.getUser2Hand());
      String winnerName = result.equals("Draw") ? "引き分け"
          : result.equals("Win") ? user1.getName() + "の勝利" : user2.getName() + "の勝利";
      match.setResult(winnerName);
    }
    model.addAttribute("matches", matches);

    return "janken";
  }

  private String determineWinner(String hand1, String hand2) {
    if (hand1.equals(hand2)) {
      return "Draw";
    } else if ((hand1.equals("Gu") && hand2.equals("Choki")) ||
        (hand1.equals("Choki") && hand2.equals("Pa")) ||
        (hand1.equals("Pa") && hand2.equals("Gu"))) {
      return "Win";
    } else {
      return "Lose";
    }
  }

  // 対戦相手の情報を表示するメソッドを追加
  @GetMapping("/match")
  public String match(Principal prin, @RequestParam("id") int opponentId, ModelMap model) {
    String loginUserName = prin.getName();
    User opponent = userMapper.selectUserById(opponentId); // 対戦相手を取得
    User user = userMapper.selectUserByName(loginUserName); // ログインユーザーを取得

    model.addAttribute("loginUser", loginUserName);
    model.addAttribute("opponent", opponent);
    model.addAttribute("userId", user.getId()); // 自身のユーザーIDをモデルに追加

    return "match"; // match.htmlテンプレートを返す
  }

  @GetMapping("/fight")
  @Transactional
  public String fight(@RequestParam int id, @RequestParam String hand, ModelMap model, Principal prin) {
    String loginUser = prin.getName();
    User user = userMapper.selectUserByName(loginUser);
    User opponent = userMapper.selectUserById(id);

    String cpuHand = "Gu"; // CPU の手は常に Gu (グー)

    // 結果を判定
    String result = determineResult(hand, cpuHand);

    // マッチを作成し、データベースに挿入
    Match match = new Match();
    match.setUser1(user.getId());
    match.setUser2(opponent.getId());
    match.setUser1Hand(hand);
    match.setUser2Hand(cpuHand);
    matchMapper.insertMatch(match);

    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);
    model.addAttribute("userHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);

    return "match";
  }

  // 勝敗を判定するメソッドを追加
  private String determineResult(String userHand, String cpuHand) {
    if (userHand.equals(cpuHand)) {
      return "Draw";
    } else if ((userHand.equals("Gu") && cpuHand.equals("Choki")) ||
        (userHand.equals("Choki") && cpuHand.equals("Pa")) ||
        (userHand.equals("Pa") && cpuHand.equals("Gu"))) {
      return "Win";
    } else {
      return "Lose";
    }
  }
}
