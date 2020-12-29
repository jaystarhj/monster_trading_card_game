import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeckSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static Object getDeckByUser(JSONObject headJSON){
        Boolean authStatus = util.checkToken(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        JSONArray message = new JSONArray();
        StringBuilder mStr = new StringBuilder();
        // if valid user and token
        if (authStatus & user != null){
            String selectRows = "select * from deck where user_id = ?";
            try{
                ResultSet rs = CRUD.ReadSql(selectRows, user.getId());
                if (rs.next() == false) {
                    message.put(new JSONObject("{\"Message\": \"No Configure Deck yet\"}"));
                } else
                    {
                        do {
                            String tmpCardID = rs.getString("card_id");
                            message.put(tmpCardID);
                            mStr.append(tmpCardID).append(" ");
                        }
                        while (rs.next());
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }else{
            message.put(new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}"));
        }

        if (headJSON.get("url").equals("deck?format=plain")){
            return mStr;
        }
        return message;
    }

    public static JSONObject addCardToDeck(JSONObject headJSON, JSONArray bodyJSON){
        Boolean authStatus = util.checkToken(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        JSONObject message = new JSONObject("{\"Error\": \"Not such user / Invalid Token/User\"}");
        // if valid user and token
        if (authStatus & user != null){
            // add a new card to deck
            String insertRow = "insert into deck (card_id, user_id) values (?,?)";
            if (bodyJSON.length() == 4){
                for (Object c_id:bodyJSON){ // iterate jsonobject
                    // [\"845f0dc7-37d0-426e-994e-43fc3ac83c08\",..]
                    int rowCount = CRUD.CUDSql(insertRow, c_id, user.getId());
                    if (rowCount != 1){
                        return new JSONObject("{\"Error\":\"Invalid Data/Duplicate Data\"}");
                    }
                }
                message = new JSONObject("{\"Message\":\"" + "Added Cards to Deck Successfully\"}");
            }else{
                return new JSONObject("{\"Error\":\"Invalid Data Length\"}");
            }

        }
        return message;
    }



}
