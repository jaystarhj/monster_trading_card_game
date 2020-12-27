import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class StackSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static Boolean addCardToStack(JSONObject headJSON){
        Boolean authStatus = AuthSQL.checkAuth(headJSON);
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        // if valid user and token
        if (authStatus & user != null){
            try{
                JSONArray cardList = (JSONArray) CardSQL.getCardsFromPackByUser(headJSON);
                // iterate JSON Arrays
                for (int i=0; i<cardList.length(); i++) {
                    JSONObject item = cardList.getJSONObject(i);
                    String card_id = item.getString("id");
                    String insertRowSQL = "insert into stack (card_id, user_id) values (?,?)";
                    int numCount = CRUD.CUDSql(insertRowSQL, card_id, user.getId());
                    if (numCount != 1){
                        return false;
                    }
                }
                return true;
            }catch (ClassCastException e){
                return false;
            }
        }

        return false;

    }

    public static Boolean removeCardFromStack(JSONObject headJSON, String removed_card_id){
        Boolean authStatus = AuthSQL.checkAuth(headJSON);
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

}
