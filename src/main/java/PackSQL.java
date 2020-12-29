import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONObject addPackage(JSONObject headJSON, JSONArray bodyJSON){
        JSONObject message = new JSONObject();
        String authStr = headJSON.getString("authorization");
        // first check if it is valid token and correct user: admin
        if (!util.checkToken(headJSON) || !authStr.equals("Basic admin-mtcgToken")){
            message = new JSONObject("{\"Error\":\"Invalid Token\"}");
        }else{
            // add a new package
            Package p = insertRowToPack();
            String insertRow = "insert into card (id, name, damage, package_id, type) values (?, ?, ?, ?, ?)";
            for (Object js:bodyJSON){ // iterate jsonobject
                // {\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}
                JSONObject tmp = (JSONObject) js;
                String c_id = tmp.getString("Id");
                String c_name = tmp.getString("Name");
                Float c_damage = tmp.getFloat("Damage");
                String c_type;
                if (c_name.contains("Spell")){
                    c_type = "monster";
                }else{
                    c_type = "spell";
                }

                int rowCount = CRUD.CUDSql(insertRow, c_id, c_name, c_damage, p.getId(), c_type);
                if (rowCount == 1){
                    message = new JSONObject("{\"Message\":\"Package added successfully\"}");
                }else{
                    message = new JSONObject("{\"Error\":\"Invalid Data/Duplicate Data\"}");
                }
            }
        }

        return message;
    }

    public static Package insertRowToPack() {
        // insert a new row to package table and return the id
        String SQLQuery = "insert into package (status) values (1)";
        String selectLastRow = "SELECT id, status FROM package ORDER BY id DESC LIMIT 1";
        Package p = new Package();
        int rowCount = CRUD.CUDSql(SQLQuery);
        if (rowCount == 1){
            try{
                ResultSet r = CRUD.ReadSql(selectLastRow);
                if(r.next()){
                    int p_id = r.getInt("id");
                    String p_status = r.getString("status");
                    p.setId(p_id);
                    p.setStatus(p_status);
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return p;
    }

    public static Boolean updatePack(int user_id, int p_id){
        String SQLQuery = "update package set status = -1, user_id = ? where id = ?";
        int rowCount = CRUD.CUDSql(SQLQuery, user_id, p_id);
        if (rowCount == 1){
            return true;
        }
        return false;

    }
    public static Boolean updateCoin(int user_id, int coin){
        String updateUserCoin = "update usertable set coin = ? where id = ?";
        int rowCount2 = CRUD.CUDSql(updateUserCoin, coin, user_id);

        if (rowCount2 ==1){
            return true;
        }
        return false;
    }





        public static JSONObject acquirePackage(JSONObject headJSON){
        JSONObject message  = new JSONObject("{\"Error\":\"Something went wrong\"}");
        String userName = headJSON.getString("authorization").split("\\s")[1].split("-")[0];
        User user = UserSQL.getUserByName(userName);
        Package p = new Package();
        // 检查token
        if (!util.checkToken(headJSON) || user == null){
            message = new JSONObject("{\"Error\":\"Invalid Token or User\"}");
        }else if (user.getCoin() == 0){ // 检查金钱是否足够
            message = new JSONObject("{\"Error\":\"No enough money\"}");
        }else{
            // return null or return the last row of package id if there is still package
            String SQLQuery = "SELECT * from package Where status = 1 ORDER BY id DESC LIMIT 1";
            try{
                ResultSet rs = CRUD.ReadSql(SQLQuery);
                if(rs.next()){
                    int p_id = rs.getInt("id");
                    String p_status = rs.getString("status");
                    // form package instance
                    p.setId(p_id);
                    p.setStatus(p_status);
                    p.setUser(user);
                    int remainCoin = chargeCoin(user);
                    // update package row
                    if (updatePack(user.getId(), p_id)){
                        if (updateCoin(user.getId(), remainCoin)){
                            // add card to stack
                            if (StackSQL.addCardToStack(headJSON)){
                                message = new JSONObject("{\"message\":\"Acquired Package Successfully\"}");
                            }
                        }
                    }
                }else{
                    message = new JSONObject("{\"Error\":\"No package\"}");
                }
            }catch (SQLException e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }



        return message;
    }

    public static int chargeCoin(User user){
        int currentCoin = user.getCoin() - 5;
        if (currentCoin  <= 0){
            currentCoin = 0;
        }
        return currentCoin;
    }

}
