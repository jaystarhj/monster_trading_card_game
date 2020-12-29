import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class util {

    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    // check if token is valid
    public static Boolean checkToken(JSONObject headJSON) {
        String userName = getUserNameFromHeadJSON(headJSON);
        if (userName != null){
            if (checkUser(userName)){
                return true;
            }
        }

        return false;
    }

    // check if user is exist
    public static Boolean checkUser(String userName){
        // get user
        String SQLQuery = "SELECT * from usertable WHERE name = ?";
        try{
            ResultSet rs = CRUD.ReadSql(SQLQuery, userName);
            assert rs != null;
            if (rs.next()) {
                return true;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }

    public static String getUserNameFromHeadJSON(JSONObject headJSON){
        String authorization = (String) headJSON.get("authorization");
        String username = null;
        // Authorization: Basic kienboec-mtcgToken
        try{
            String basic = authorization.split(" ")[0];
            username = authorization.split(" ")[1].split("-")[0];
            String token = authorization.split("\\s")[1].split("-")[1];
            if (!token.equals("mtcgToken") || !basic.equals("Basic") ){
                return null;
            }
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return username;
    }


}
