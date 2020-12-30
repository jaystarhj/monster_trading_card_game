import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CardSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static List<Card> getCardsFromPackByUser(JSONObject headJSON){
        User user = UserSQL.getUserByName(headJSON.getString("userName"));
        List<String> pList = new ArrayList<>();
        List<Card>  cList = new ArrayList<>();
        String PackQuery = "SELECT id from package Where user_id = ?";

        // first check if it is valid token and correct user: admin
        if (!util.checkToken(headJSON) || user == null){
            return null;
        }else{
            try{
                ResultSet rs = CRUD.ReadSql(PackQuery, user.getId());
                if (rs.next() == false) {
                    return null;
                }else{
                    do {
                        int p_id = rs.getInt("id");
                        pList.add(Integer.toString(p_id));
                    }while (rs.next());
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }


            String CardQuery = "SELECT * from card WHERE package_id in (" + String.join(",", pList) + ")";
            try{
                ResultSet rs = CRUD.ReadSql(CardQuery);
                if (rs.next() == false) {
                    return null;
                }else{
                    do{
                        String c_id = rs.getString("id");
                        String c_name = rs.getString("name");
                        float c_damage = rs.getFloat("damage");
                        String type  = rs.getString("type");
                        Card c = new Card();
                        c.setDamage(c_damage);
                        c.setName(c_name);
                        c.setId(c_id);
                        c.setCardType(type);
                        cList.add(c);
                    }while (rs.next());
                }
                return cList;
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

        return null;
    }

    public static Card getCardByID(String card_id){
        String getCardQuery = "select * from card where id = ?";
        try{
            ResultSet rs = CRUD.ReadSql(getCardQuery, card_id);
            if (rs.next() == false) {
                return null;
            } else
            {
                do {
                    String id  = rs.getString("id");
                    String name  = rs.getString("name");
                    Float damage  = rs.getFloat("damage");
                    String type  = rs.getString("type");
                    Card c = new Card();
                    c.setName(name);
                    c.setId(id);
                    c.setDamage(damage);
                    c.setCardType(type);
                    return c;
                }
                while (rs.next());
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

}
