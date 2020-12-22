import java.lang.reflect.Field;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class CRUD {
    // for better error print
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONObject add(String SQLQuery, Object...objects){
        JSONObject message = new JSONObject("{\"Error\": \"Can not add\"}");
        Integer num = CUDSql(SQLQuery, objects);
        if (num == 1){
            message = new JSONObject("{\"message\": \"Successfully Added\"}");
        }
        return message;
    }

    public static JSONObject get(String SQLQuery, Object...objects){
        JSONObject message = new JSONObject("{\"Error\": \"Can not find object\"}");
        User user= ReadSql(SQLQuery, objects);

        if (user != null){
            message = new JSONObject(user.toString());
        }
        return message;
    }


    // common method for create, update, delete
    public static int CUDSql(String sqlQuery, Object... objects){
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            // 链接数据库
            conn = JdbcConnection.getConnection();
            // 构造执行语句
            stmt = conn.prepareStatement(sqlQuery);
            // 填充占位符
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i+1, objects[i]);
            }
            // 最后执行语句
            return stmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) { // 如果报错
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            closeConnection(conn, stmt);
        }
        return 0;
    }

    // read operation
    public static User ReadSql(String sqlQuery, Object... objects){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try{
            // 链接数据库
            conn = JdbcConnection.getConnection();
            // 构造执行语句
            stmt = conn.prepareStatement(sqlQuery);
            // 填充占位符
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i+1, objects[i]);
            }
            // 最后执行语句
            resultSet = stmt.executeQuery();
            //获取结果集的元数据 :ResultSetMetaData
            ResultSetMetaData rsmd = resultSet.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount = rsmd.getColumnCount();

            if(resultSet.next()){
                User user = new User();
                //处理结果集一行数据中的每一个列
                for(int i = 0;i <columnCount;i++){
                    //获取列值
                    Object columValue = resultSet.getObject(i + 1);

                    //获取每个列的列名
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //给cust对象指定的columnName属性，赋值为columValue：通过反射
                    Field field = User.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(user, columValue);
                }
                return user;
            }

        } catch (SQLException | ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) { // 如果报错
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            closeConnection(conn, stmt);
        }
        return null;
    }

    // close conn
    public static void closeConnection(Connection conn, Statement stmt){
        try {
            if(stmt != null)
                stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void main(String[] args){
        String SQLQuery = "select * from usertable where name = ?";
        System.out.println(CRUD.get(SQLQuery, "king"));

        SQLQuery = "INSERT INTO usertable (name, password) values(?, ?)";
        System.out.println(CRUD.add(SQLQuery, "mario11", "sdssd"));

    }


}
