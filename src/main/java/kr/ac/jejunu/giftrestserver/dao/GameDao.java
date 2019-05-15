package kr.ac.jejunu.giftrestserver.dao;

import kr.ac.jejunu.giftrestserver.vo.Game;
import kr.ac.jejunu.giftrestserver.vo.GameMinify;
import kr.ac.jejunu.giftrestserver.vo.PurchaseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class GameDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Map<String, Object> getTest() {
        String sql = "SELECT * FROM test";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("messages", "success");
        result.put("data", rows);

        return result;
    }

    public int insertGame(Game game) {
        String sql = "insert into game_info (name, developer, category, current_price, goal_price, game_information, investigation_information, investigation_condition, company_introduction, profile_image) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
                game.getName(),
                game.getDeveloper(),
                game.getCategory(),
                game.getCurrentPrice(),
                game.getGoalPrice(),
                game.getGameInformation(),
                game.getInvestmentInformation(),
                game.getInvestmentCondition(),
                game.getCompanyIntroduction(),
                game.getProfileImage()};

        return jdbcTemplate.update(sql, params);
    }

    public Collection<GameMinify> getGameCollection(boolean isAvailable) {

        String sql = "SELECT game_id, name, developer, category, Success, current_price, goal_price, profile_image FROM game_info WHERE success = " + (isAvailable ? '0' : '1');
        Collection<GameMinify> gameMinifyCollection = null;
        try {
            gameMinifyCollection = jdbcTemplate.query(sql, (rs, rowNum) -> {
               GameMinify game = new GameMinify();
               game.setGameId(rs.getInt("game_id"));
               game.setName(rs.getString("name"));
               game.setDeveloper(rs.getString("developer"));
               game.setCategory(rs.getString("category"));
               game.setCurrentPrice(rs.getInt("current_price"));
               game.setGoalPrice(rs.getInt("goal_price"));
               game.setProfileImage(rs.getString("profile_image"));
               return game;
           });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return gameMinifyCollection;
    }

    public Game getGameFromId(Long id) {
        String sql = "SELECT * FROM game_info WHERE game_id=?";
        String sqlDescribe = "SELECT describe_image FROM game_describe_image WHERE game_id = ?";
        Object[] params = new Object[] { id };
        Game result = null;
        try {
            result = jdbcTemplate.queryForObject(sql, params, /* RowMapper*/ (rs, rowNum) -> {
                Game game = new Game();
                game.setGameId(rs.getInt("game_id"));
                game.setName(rs.getString("name"));
                game.setDeveloper(rs.getString("developer"));
                game.setCategory(rs.getString("category"));
                game.setSuccess(rs.getBoolean("success"));
                game.setCurrentPrice(rs.getInt("current_price"));
                game.setGoalPrice(rs.getInt("goal_price"));
                game.setGameInformation(rs.getString("game_information"));
                game.setInvestmentInformation(rs.getString("investigation_information"));
                game.setInvestmentCondition(rs.getString("investigation_condition"));
                game.setCompanyIntroduction(rs.getString("company_introduction"));
                game.setProfileImage(rs.getString("profile_image"));

                return game;
            });

            assert result != null;
            result.setDescribeImageList(jdbcTemplate.query(sqlDescribe, params, (rs, rowNum) -> rs.getString(1)));
        } catch (EmptyResultDataAccessException e) { e.printStackTrace(); }
        return result;
    }

    public void fundGame(Game game) {
        String sql = "UPDATE game_info SET current_price = ? WHERE game_id = ?";
        Object[] params = new Object[] { game.getCurrentPrice(), game.getGameId() };
        jdbcTemplate.update(sql, params);
    }
}
