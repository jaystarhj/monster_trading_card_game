import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthSQL {

    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static Boolean checkAuth(JSONObject headData) {

        String authorization = (String) headData.get("authorization");
        String username;
        //Authorization: Basic kienboec-mtcgToken
        try{
            String basic = authorization.split(" ")[0];
            username = authorization.split(" ")[1].split("-")[0];
            String token = authorization.split("\\s")[1].split("-")[1];
            if (!token.equals("mtcgToken") || !basic.equals("Basic") ){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        // get user
        String SQLQuery = "SELECT * from usertable WHERE name = ?";
        try{
            ResultSet rs = CRUD.ReadSql(SQLQuery, username);
            assert rs != null;
            if (rs.next()) {
                return true;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }
}
