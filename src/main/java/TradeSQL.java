import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradeSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONArray getDeals(JSONObject headData){
        JSONArray mJsonArray = new JSONArray(new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}"));
        User user = UserSQL.getUserByName(headData.getString("userName"));

        if (util.checkToken(headData) & user != null){
            String getDealQuery = "select * from trade";
            try{

                ResultSet rs = CRUD.ReadSql(getDealQuery);
                if (rs.next() == false) {
                    mJsonArray = new JSONArray(new JSONObject("{\"Message\": \"No data yet\"}"));
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
                        mJsonArray.put(new JSONObject(t.toString()));
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
        User user = UserSQL.getUserByName(headData.getString("userName"));

        if (util.checkToken(headData) & user != null){
            String insertDeal = "insert into trade (id, card_id, user_id) values (?, ?, ?)";
            int id = bodyData.getInt("Id");
            int c_id = bodyData.getInt("CardToTrade");
            int user_id = user.getId();

            int rowCount = CRUD.CUDSql(insertDeal, id,  c_id, user_id);
            if (rowCount == 1){
                message = new JSONObject("{\"Message\": \"Add deal Successfully\"}");
            }else{
                message = new JSONObject("{\"Error\": \"Invalid Data Input\"}");
            }
        }
        return message;
    }

    public static JSONObject deleteDeal(JSONObject headData, JSONObject bodyData){
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        User user = UserSQL.getUserByName(headData.getString("userName"));

        if (util.checkToken(headData) & user != null){
            String insertDeal = "delete from trade where card_id = ?";
            int card_id = 0;
            int rowCount = CRUD.CUDSql(insertDeal, card_id, card_id);
            if (rowCount == 1){
                message = new JSONObject("{\"Message\": \"Delete deal Successfully\"}");
            }else{
                message = new JSONObject("{\"Error\": \"Invalid Data Input\"}");
            }
        }
        return message;
    }

    public static JSONObject makeDeal(){

        return null;
    }

}
