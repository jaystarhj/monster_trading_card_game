import org.junit.Assert;
import org.junit.Test;

public class UserSQLTest {

    // test register users
    @Test
    public void testGetUserByID(){
        // test user already register
        int id = 1;
        User u = UserSQL.getUserByID(id);
        Assert.assertNotNull(u);
        // test user not register yet
        id = 1000;
        u = UserSQL.getUserByID(id);
        Assert.assertNull(u);
    }

    @Test
    public void testGetUserByName(){
        String userName = "kienboec";
        User u = UserSQL.getUserByName(userName);
        Assert.assertNotNull(u);
        // test user not register yet
        userName = "kkkkienboec";
        u = UserSQL.getUserByName(userName);
        Assert.assertNull(u);
    }

    // test login users

}
