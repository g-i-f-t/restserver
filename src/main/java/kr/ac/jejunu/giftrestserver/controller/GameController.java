package kr.ac.jejunu.giftrestserver.controller;

import kr.ac.jejunu.giftrestserver.exception.GiftException;
import kr.ac.jejunu.giftrestserver.model.Developer;
import kr.ac.jejunu.giftrestserver.model.Game;
import kr.ac.jejunu.giftrestserver.payload.GamePayLoad;
import kr.ac.jejunu.giftrestserver.service.DeveloperService;
import kr.ac.jejunu.giftrestserver.service.GameService;
import kr.ac.jejunu.giftrestserver.payload.PurchaseTransactionPayload;
import kr.ac.jejunu.giftrestserver.service.GameTransactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final DeveloperService developerService;
    private final GameService gameService;
    private final GameTransactionService gameTransactionService;

    @GetMapping(value="/game")

    public Map<String, Object> getGameList(@RequestParam String list) {
        Map<String, Object> res = new HashMap<>();
        Collection<Game> collection;
        switch(list) {
            case "available":
                collection = gameService.getAll();
                break;
            case "done":
                collection = gameService.getAll();
                break;
            default:
                res.put("code", 400);
                res.put("messages", "unavailable list name.");
                return res;
        }
        res.put("code", 200);
        res.put("messages", "success");
        res.put("data", collection);

        return res;
    }

    @GetMapping(value="/game/{id}")
    public Map<String, Object> getGameFromId(@PathVariable Long id) {
        Optional<Game> game = gameService.getGameFromId(id);
        Map<String, Object> res = new HashMap<>();

        res.put("code", 200);
        res.put("messages", "success");
        res.put("data", game);
        return res;
    }

    @PostMapping(value="/game/{id}")
    public Map<String, Object> fundGameFromId(@PathVariable Long id, @RequestBody PurchaseTransactionPayload pt) {
        Map<String, Object> res = new HashMap<>();
        Optional<Game> gameOptional = gameService.getGameFromId(id);

        // id 에 해당하는 게임이 없으면?
        if(gameOptional.isEmpty()) return new HashMap<>() {{
            put("code", 400);
            put("messages", "game was null");
        }};

        Game game = gameOptional.get();
        System.out.println(game);
        System.out.println(pt.getPrice());
        // 사용자가 금액을 요상하게 입력했을 때

        // 가격 갱신
        try {
            gameService.updatePrice(game, Long.parseLong(pt.getPrice()));
        } catch (NumberFormatException e) {
            return new HashMap<>() {{
                put("code", 401);
                put("messages", "wrong number input");
            }};
        }

        Optional<Game> updatedGameOptional = gameService.getGameFromId(id);
        if(updatedGameOptional.isEmpty()) return new HashMap<>() {{
            put("code", 400);
            put("messages", "game was null");
        }};
        Game updatedGame = updatedGameOptional.get();

        if(!updatedGame.getCurrentPrice().equals(game.getCurrentPrice())) {
            res.put("code", 402);
            res.put("messages", "transaction failed");
            return res;
        }


        res.put("code", 200);
        res.put("messages", "transaction succeeded");
        return res;
    }

    @PostMapping(value="/game/insert", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> insertGame(@RequestBody GamePayLoad gamePayLoad) {
        Map<String, Object> res = new HashMap<>();
        try {
            gameService.insertGame(gamePayLoad);
            res.put("code", 200);
            res.put("message", "Successfully added");
        } catch (GiftException e) {
            res.put("code", e.getErrCode());
            res.put("messages", e.getMessage());
        }
        return res;
    }
}
