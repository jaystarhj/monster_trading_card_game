import java.lang.reflect.Field;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class CRUD {
    // for better error print
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    // common method for insert, update, delete
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
    public static ResultSet ReadSql(String sqlQuery, Object... objects){
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
            return stmt.executeQuery();

        } catch (SQLException | ClassNotFoundException e) { // 如果报错
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

}


class JdbcConnection {
    // for better error print
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static Connection getConnection () throws ClassNotFoundException {
        // 参数：
        // jdbc协议:postgresql子协议://主机地址:数据库端口号/要连接的数据库名
        String url = "jdbc:postgresql://localhost:5432/monster_trading_card_game";
        // 数据库用户名
        String user = "demo";
        // 数据库密码
        String password = "demo";

        // 1.加载驱动
        Class.forName("org.postgresql.Driver");

        // 2. 连接数据库，返回连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return conn;
    }
}