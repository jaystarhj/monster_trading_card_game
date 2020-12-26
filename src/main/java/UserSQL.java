import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserSQL {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    // 注册用户
    public static JSONObject register(JSONObject bodyJSON){
        String SQLQuery = "INSERT INTO usertable (name, password) values(?, ?)";
        JSONObject message = new JSONObject("{\"Error\": \"invalid input or user name already exists\"}");
        String name = bodyJSON.getString("Username");
        String password = bodyJSON.getString("Password");
        int num = CRUD.CUDSql(SQLQuery, name, password);
        if (num == 1){
            message = new JSONObject("{\"message\": \"Successfully Added\"}");
        }
        return message;
    }

    public static JSONObject login(JSONObject bodyJSON) {
        String SQLQuery = "select * from usertable where name = ?";
        String name = bodyJSON.getString("Username");
        String password = bodyJSON.getString("Password");
        JSONObject tmp = new JSONObject("{\"Error\": \"Cant find User" + name + "\"}");
        User user = null;
        try{
            ResultSet resultSet = CRUD.ReadSql(SQLQuery, name);
            while (resultSet.next()) {
                String userName = resultSet.getString("name");
                String userPassword = resultSet.getString("password");
                user = new User();
                user.setName(userName);
                user.setPassword(userPassword);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        if (user != null){
            tmp = new JSONObject(user.toString());
        }
        if (!tmp.has("Error")){
            if (tmp.has("name")){
                String userName = tmp.getString ("name");
                String userPassWord = tmp.getString ("password");
                if (userName.equals(name) & userPassWord.equals(password)){
                    return new JSONObject("{\"message\":\"Successfully Login\"}");
                }else{
                    return new JSONObject("{\"Error\":\"Invaild password\"}");
                }
            }
        }
        return tmp;
    }

    public static JSONObject updateUserProfile(JSONObject headData, JSONObject bodyData){
        Boolean auth = AuthSQL.checkAuth(headData);
        String url = headData.getString("url");
        String name = url.split("/")[1];
        User user = getUserByName(name);
        if (user == null || ! auth){ // if invaild token
            return new JSONObject("{\"Error\": \"Invaild Token or User\"}");
        }
        String password;
        String bio;
        String image;
        if (bodyData.has("Password")){
            password = bodyData.getString("Password");
        }else{
            password = user.getPassword();
        }
        if (bodyData.has("Bio")){
            bio = bodyData.getString("Bio");
        }else{
            bio = user.getBio();
        }
        if (bodyData.has("Image")){
            image = bodyData.getString("Image");
        }else{
            image = user.getImage();
        }

        JSONObject message = new JSONObject("{\"Error\": \"Invaild Data\"}");
        String SQLQuery = "UPDATE usertable SET password = ?, bio = ?, image = ? WHERE name = ?;";
        int num = CRUD.CUDSql(SQLQuery, password, bio, image, name);
        if (num == 1){
            message = new JSONObject("{\"message\": \"Successfully update user profile\"}");
        }

        return message;
    }

    public static User getUserByName(String username) {
        // get user
        String SQLQuery = "SELECT * from usertable WHERE name = ?";
        try{
            ResultSet rs = CRUD.ReadSql(SQLQuery, username);
            if (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("name");
                String userPassword = rs.getString("password");
                String userBio = rs.getString("bio");
                String userImage = rs.getString("image");
                int coin = rs.getInt("coin");
                User u = new User();
                u.setId(userId);
                u.setName(userName);
                u.setPassword(userPassword);
                u.setBio(userBio);
                u.setImage(userImage);
                u.setCoin(coin);
                return u;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject getUserProfile(JSONObject headData){
        JSONObject message = null;
        Boolean auth = AuthSQL.checkAuth(headData);
        String url = headData.getString("url");
        String name = url.split("/")[1];
        User user = getUserByName(name);
        if (user != null & auth){
            System.out.println(user.toString());
            message = new JSONObject(user.toString());
        }else{
            message = new JSONObject("{\"Error\": \"Not such user / Invalid Token\"}");
        }

        return message;

    }

} // end class

