import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONObject getStatsByUser(JSONObject headJSON){
        Boolean authStatus = util.checkToken(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        JSONObject message = null;
        // if valid user and token
        if (authStatus & user != null){
            String selectRows = "select * from stats where user_id = ?";
            try{
                ResultSet rs = CRUD.ReadSql(selectRows, user.getId());
                if (rs.next() == false) {
                    message = new JSONObject("{\"Message\": \"No battle yet\"}");
                } else
                {
                    do {
                        int s_user_id = rs.getInt("user_id");
                        int s_win = rs.getInt("win");
                        int s_loss = rs.getInt("loss");
                        Stats s = new Stats();
                        s.setLoss(s_loss);
                        s.setWin(s_win);
                        s.setUser_id(s_user_id);
                        message = new JSONObject(s.toString());
                    }
                    while (rs.next());
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }else{
            message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        }

        return message;
    }

    public static JSONArray scoreBoard(JSONObject headJSON){
        Boolean authStatus = util.checkToken(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        JSONArray message = new JSONArray();
        // if valid user and token
        if (authStatus & user != null){
            String selectRows = "select * from stats";
            try{
                ResultSet rs = CRUD.ReadSql(selectRows);
                if (rs.next() == false){
                    message = new JSONArray("[{\"Message\": \"Not data yet\"}]");
                }else{
                    do {
                        int user_id = rs.getInt("user_id");
                        int win = rs.getInt("win");
                        int loss = rs.getInt("loss");
                        int draw = rs.getInt("draw");
                        Stats s  = new Stats();
                        s.setUser_id(user_id);
                        s.setWin(win);
                        s.setLoss(loss);
                        int score = s.getELOScore();
                        User u = UserSQL.getUserByID(user_id);
                        assert u != null;
                        HashMap<String, Object> mMap = new HashMap();
                        mMap.put("name", u.getName());
                        mMap.put("win", s.getWin());
                        mMap.put("loss", s.getLoss());
                        mMap.put("draw", s.getDraw());
                        mMap.put("ELO", score);
                        message.put(mMap);
                    }while (rs.next());


                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }else{
            message = new JSONArray("[{\"Error\": \"Not such user / Invalid Token/User\"}]");
        }

        return message;
    }

}
