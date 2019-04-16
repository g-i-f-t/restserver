package kr.ac.jejunu.giftrestserver.controller;

import kr.ac.jejunu.giftrestserver.BankService;
import kr.ac.jejunu.giftrestserver.dao.AccountDao;
import kr.ac.jejunu.giftrestserver.dao.UserDao;
import kr.ac.jejunu.giftrestserver.vo.Account;
import kr.ac.jejunu.giftrestserver.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@ResponseBody
public class AuthController {

    @Autowired
    BankService bankService;

    @Autowired
    UserDao userdao;


    @GetMapping(value="/auth")
    public Map<String, String> authUser(@RequestParam String code, @RequestParam String scope, @RequestParam String client_info) {
        Map<String, String> res = new HashMap<>();
        res.put("code", code);
        res.put("scope", scope);
        res.put("client_info", scope);
        return res;
    }

    @PostMapping(value="/addAccount", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> addAccount(
            @RequestBody String id,
            @RequestBody String password,
            @RequestBody String name,
            @RequestBody String birthGender,
            @RequestBody String email,
            @RequestBody String authCode,
            @RequestBody String scope,
            @RequestBody String clientInfo) {
//        new AccountDao().addAccount(account);

        Map<String, Object> tokenRes = bankService.getToken(authCode);

        final String accessToken = (String) tokenRes.get("access_token");
        final String refreshToken = (String) tokenRes.get("refresh_token");
        final String userSeqNo = (String) tokenRes.get("user_seq_no");

        User user = new User();

        user.setId(id);
        user.setPassword(password);
        user.setName(name);
        user.setBirthGender(birthGender);
        user.setEmail(email);
        user.setScope(scope);
        user.setRefreshToken(refreshToken);
        user.setUserSeqId(userSeqNo);

        userdao.createUser(user);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("messages", "success");
        res.put("access_token", accessToken);
        return res;
    }
}
