import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

class JdbcConnection {
    // for better error print
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static Connection getConnection () throws ClassNotFoundException {
        // 参数：
        // jdbc协议:postgresql子协议://主机地址:数据库端口号/要连接的数据库名
        String url = "jdbc:postgresql://localhost:5432/monster_trading_card_game";
        // 数据库用户名Databank username
        String user = "demo";
        // 数据库密码password
        String password = "demo";

        // 1.加载驱动drive
        Class.forName("org.postgresql.Driver");

        // 2. 连接数据库，返回连接对象connect to databank,return the connected object
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return conn;
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
