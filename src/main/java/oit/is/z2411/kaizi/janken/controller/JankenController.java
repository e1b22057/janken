package oit.is.z2411.kaizi.janken.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z2411.kaizi.janken.model.MatchInfo;
import oit.is.z2411.kaizi.janken.model.User;
import oit.is.z2411.kaizi.janken.model.MatchInfoMapper;
import oit.is.z2411.kaizi.janken.model.UserMapper;
import oit.is.z2411.kaizi.janken.model.Match;
import oit.is.z2411.kaizi.janken.model.MatchMapper;
import oit.is.z2411.kaizi.janken.service.AsyncKekka;

import java.security.Principal;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Controller
public class JankenController {

  @Autowired
  private MatchMapper matchMapper;
  @Autowired
  private MatchInfoMapper matchInfoMapper;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private AsyncKekka asyncKekka;

  @GetMapping("/janken")
  public String janken(Principal principal, ModelMap model) {
    String loginUser = principal.getName();
    model.addAttribute("user", loginUser);

    // ユーザー一覧を取得
    ArrayList<User> users = userMapper.selectAllUsers();
    model.addAttribute("users", users);

    ArrayList<MatchInfo> activeMatches = matchInfoMapper.findActiveMatches();
    model.addAttribute("activeMatches", activeMatches);
    return "janken";
  }

  @GetMapping("/match")
  public String match(@RequestParam int id, ModelMap model, Principal principal) {
    String loginUser = principal.getName();
    model.addAttribute("user", loginUser);

    // 対戦相手の情報を取得
    User opponent = userMapper.selectUserById(id);
    model.addAttribute("opponent", opponent);
    model.addAttribute("id", id);

    return "match";
  }

  // じゃんけんの手を選択
  @GetMapping("/fight")
  @Transactional
  public String fight(@RequestParam Integer id, @RequestParam String hand, Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    User currentUser = userMapper.selectUserByName(loginUser); // ログインユーザーのUser情報を取得
    int userId = currentUser.getId(); // UserからIDを取得

    // 自分と相手のActiveな対戦情報があるか確認
    MatchInfo activeMatch = matchInfoMapper.selectActiveMatchInfoByUserId(userId);

    if (activeMatch != null && activeMatch.getUser1() == id && activeMatch.getUser2() == userId) {
      // 対戦成立時: 両者が手を選んだ場合
      Match match = new Match();
      match.setUser1(activeMatch.getUser1());
      match.setUser2(userId);
      match.setUser1Hand(activeMatch.getUser1Hand());
      match.setUser2Hand(hand);
      match.setIsActive(true);
      matchMapper.insertMatch(match);

      // 対応するMatchInfoを非アクティブに更新
      activeMatch.setIsActive(false);
      matchInfoMapper.updateMatchInfo(activeMatch);

      model.addAttribute("match", match);
    } else {
      // 新規対戦待ち情報を登録
      MatchInfo newMatchInfo = new MatchInfo();
      newMatchInfo.setUser1(userId);
      newMatchInfo.setUser2(id);
      newMatchInfo.setUser1Hand(hand);
      newMatchInfo.setIsActive(true);
      matchInfoMapper.insertMatchInfo(newMatchInfo);

      model.addAttribute("matchinfo", newMatchInfo);
    }

    model.addAttribute("user", loginUser);
    model.addAttribute("hand", hand);
    return "wait";
  }

  @GetMapping("/wait")
  public SseEmitter waitResult() {
    final SseEmitter sseEmitter = new SseEmitter();
    this.asyncKekka.asyncMatchResult(sseEmitter);
    return sseEmitter;
  }
}
