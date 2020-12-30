import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StoreSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONArray getDeals(JSONObject headData){
        JSONArray mJsonArray = new JSONArray();

        if (util.checkToken(headData)){
            User user = UserSQL.getUserByName(headData.getString("userName"));
            if (user != null){
                String getDealQuery = "select * from store";
                try{

                    ResultSet rs = CRUD.ReadSql(getDealQuery);
                    if (rs.next() == false) {
                        mJsonArray = new JSONArray("[{\"Message\": \"No data yet\"}]");
                    } else
                    {
                        do {
                            String card_id  = rs.getString("card_id");
                            int user_id = rs.getInt("user_id");
                            Card c  = CardSQL.getCardByID(card_id);
                            User u = UserSQL.getUserByID(user_id);
                            Store t = new Store();
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
        }else{
            mJsonArray = new JSONArray("[{\"Error\": \"Not such user / Invalid Token/User\"}]");

        }
        return mJsonArray;
    }

    public static JSONObject addDeal(JSONObject headData, JSONObject bodyData){
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");

        if (util.checkToken(headData)){
            User user = UserSQL.getUserByName(headData.getString("userName"));
            if (user != null){
                String insertDeal = "insert into store (id, card_id, user_id, require_type, minimum_damage) values (?, ?, ?,?,?)";
                String id = bodyData.getString("Id");
                String c_id = bodyData.getString("CardToTrade");
                String type = bodyData.getString("Type");
                float min_Damage = bodyData.getFloat("MinimumDamage");
                // first check if card is not in deck
                if (!checkCardInDeck(c_id, user.getId())){
                    // add to trading system
                    int user_id = user.getId();
                    int rowCount = CRUD.CUDSql(insertDeal, id,  c_id, user_id, type, min_Damage);
                    if (rowCount == 1){
                        message = new JSONObject("{\"Message\": \"Add deal Successfully\"}");
                    }else{
                        message = new JSONObject("{\"Error\": \"Invalid Data Input\"}");
                    }
                }else{
                    message = new JSONObject("{\"Error\": \"Card being trade can not be from deck\"}");

                }
            }
        }
        return message;
    }

    public static JSONObject deleteDeal(JSONObject headData){
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        if (util.checkToken(headData)){
            User user = UserSQL.getUserByName(headData.getString("userName"));
            if (user != null){
                String url = headData.getString("url");
                String trade_id = url.split("/")[1];
                // insert into row
                String insertDeal = "delete from store where id = ?";
                int rowCount = CRUD.CUDSql(insertDeal, trade_id);
                if (rowCount == 1){
                    message = new JSONObject("{\"Message\": \"Delete deal Successfully\"}");
                }else{
                    message = new JSONObject("{\"Error\": \"Invalid Data Input\"}");
                }
            }
        }
        return message;
    }

    public static JSONObject makeDeal(JSONObject headJSON, JSONArray bodyJSON){
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        // check token
        if (util.checkToken(headJSON)){
            User user = UserSQL.getUserByName(headJSON.getString("userName"));
            String trade_id = headJSON.getString("url").split("/")[1];
            // check trading id in store
            if (checkIdInStore(trade_id)){
                String card_id = (String) bodyJSON.get(0);
                // check card being trade not not in deck
                if (!checkCardInDeck(card_id, user.getId())){
                    // delete from trade
                    // add card to stack to each users
                    HashMap<String, String> map = getDealByTradeId(trade_id);
                    String deal_card_id = map.get("card_id");
                    String deal_user_id = map.get("user_id");
                    if (!user.getId().toString().equals(deal_user_id)){
                        if (deleteTradeAndAddCardToStack(trade_id, deal_card_id, deal_user_id, card_id, user.getId())){
                            message = new JSONObject("{\"Message\": \"trade successfully\"}");
                        }else {
                            message = new JSONObject("{\"Error\": \"invalid input data\"}");
                        }
                    }else{
                        message = new JSONObject("{\"Error\": \"you can not trade with yourself\"}");
                    }

                }else{
                    message = new JSONObject("{\"Error\": \"can not trade card from deck\"}");

                }

            }else{
                message = new JSONObject("{\"Error\": \"There is no such deal in store\"}");
            }
        }

        return message;
    }

    public static boolean checkCardInDeck(String card_id, int user_id){
        try{
            String sql = "select * from deck where card_id = ? and user_id = ?";
            ResultSet rs = CRUD.ReadSql(sql, card_id, user_id);
            if (rs.getFetchSize()!=0){
                return true;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public static boolean checkIdInStore(String trade_id){
        try{
            String sql = "select * from store where id = ?";
            ResultSet rs = CRUD.ReadSql(sql, trade_id);
            if (rs.next() != false){
                return true;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public static HashMap<String, String> getDealByTradeId(String tradeId){
        HashMap<String, String> map = new HashMap<>();
        String getDeal = "select * from store where id = ?";
        try{
            ResultSet rs = CRUD.ReadSql(getDeal,tradeId);
            if (rs.next() == false){
                return null;
            }else{
                do {
                    String card_id = rs.getString("card_id");
                    String user_id = rs.getString("user_id");
                    map.put("card_id", card_id);
                    map.put("user_id", user_id);
                }while (rs.next());
                return map;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static boolean deleteTradeAndAddCardToStack(String deal_id, String deal_card_id, String deal_user_id, String card_id, int user_id){
        String dropRow = "delete from store where id = ?"; // delete from store
        int RowCountOne = CRUD.CUDSql(dropRow, deal_id);

        if (RowCountOne == 1){
            // update from stack
            String addCard = "update stack set user_id = ? where card_id = ?"; // add to store
            int RowCountTwo = CRUD.CUDSql(addCard, Integer.parseInt(deal_user_id), card_id);
            int RowCountThree = CRUD.CUDSql(addCard, user_id, deal_card_id);
            if (RowCountTwo == 1 &  RowCountThree ==1){
                return true;
            }
        }

        return false;
    }

}
