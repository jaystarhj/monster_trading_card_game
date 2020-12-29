import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
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
            JdbcConnection.closeConnection(conn, stmt);
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
            JdbcConnection.closeConnection(conn, stmt);
        }
        return null;
    }

}