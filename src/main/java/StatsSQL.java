import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONObject getStatsByUser(JSONObject headJSON){
        Boolean authStatus = AuthSQL.checkAuth(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        JSONObject message = null;
        // if valid user and token
        if (authStatus & user != null){
            String selectRows = "select * from stats where user_id = ?";
            try{
                ResultSet rs = CRUD.ReadSql(selectRows, user.getId());
                if (rs.next() == false) {
                    message = new JSONObject("{\"Message\": \"No data yet\"}");
                } else
                {
                    do {
                        int s_user_id = rs.getInt("user_id");
                        int s_win = rs.getInt("win");
                        int s_loss = rs.getInt("loss");
                        int s_Elo = rs.getInt("Elo");
                        Stats s = new Stats();
                        s.setElo(s_Elo);
                        s.setLoss(s_loss);
                        s.setWin(s_win);
                        s.setUser_id(s_user_id);
                        System.out.println(s.toString());
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

}
