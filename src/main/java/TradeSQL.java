import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradeSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONArray getDeals(JSONObject headData){
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        JSONArray mJsonArray = new JSONArray();
        if (AuthSQL.checkAuth(headData)){
            String getDealQuery = "select * from trade";
            try{

                ResultSet rs = CRUD.ReadSql(getDealQuery);
                if (rs.next() == false) {
                    message = new JSONObject("{\"Message\": \"No data yet\"}");
                } else
                {
                    do {
                        String card_id  = rs.getString("card_id");
                        int user_id = rs.getInt("user_id");
                        Card c  = CardSQL.getCardByID(card_id);
                        User u = UserSQL.getUserByID(user_id);
                        Trade t = new Trade();
                        t.setCard(c);
                        t.setUser(u);
                        System.out.println(t.toString());
                        message = new JSONObject(t.toString());
                        mJsonArray.put(message);
                    }
                    while (rs.next());
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return mJsonArray;
    }

    public static JSONObject addDeal(JSONObject headData, JSONObject bodyData){
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        if (AuthSQL.checkAuth(headData)){
            String insertDeal = "insert into trade";

        }
        return message;
    }

    public static JSONObject deleteDeal(){
        return null;
    }

    public static JSONObject makeDeal(){
        return null;
    }

}
