import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcConnection {
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

    public static void main(String[] args) throws ClassNotFoundException {
        Connection conn = getConnection();

    }

}