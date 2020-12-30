import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StackSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static Boolean addCardToStack(JSONObject headJSON){
        Boolean authStatus = util.checkToken(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        // if valid user and token
        if (authStatus & user != null){
            try{
                // get cards by user
                List<Card> cardList = CardSQL.getCardsFromPackByUser(headJSON);
                if (cardList != null){
                    System.out.println(cardList);
                    // iterate JSON Arrays
                    for (int i=0; i<cardList.size(); i++) {
                        Card item = cardList.get(i);
                        String card_id = item.getId();
                        String insertRowSQL = "insert into stack (card_id, user_id) values (?,?)  ON CONFLICT DO NOTHING";
                        int numCount = CRUD.CUDSql(insertRowSQL, card_id, user.getId());
                    }
                }
            }catch (ClassCastException e){
                return false;
            }
        }

        return true;

    }

    public static Boolean removeCardFromStack(JSONObject headJSON, String removed_card_id){
        Boolean authStatus = util.checkToken(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        JSONArray message = new JSONArray();
        StringBuilder mStr = new StringBuilder();
        // if valid user and token
        if (authStatus & user != null){
            String insertRowSQL = "delete from stack where card_id = ?";
            int numCount = CRUD.CUDSql(insertRowSQL, removed_card_id);
            if (numCount == 1){
                return true;
            }
        }
        return false;
    }

    public static JSONArray getCardsFromStack(JSONObject headJSON){
        Boolean authStatus = util.checkToken(headJSON);
        JSONArray message = new JSONArray();

        if (authStatus){
            User user = UserSQL.getUserByName(headJSON.getString("userName"));

            String insertRowSQL = "select * from stack where user_id = ?";
            try{
                ResultSet rs = CRUD.ReadSql(insertRowSQL, user.getId());
                if (rs.next() == false){
                    message = new JSONArray("[{\"Message\": \"No cards\"}]");
                }else{
                    do {
                        String card_id = rs.getString("card_id");
                        message.put(card_id);
                    }
                    while (rs.next());
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }else {
            message = new JSONArray("[{\"Error\": \"Invalid token / user\"}]");
        }

        return message;
    }

}
