import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class utilTest {

    @Test
    public void testGetUserNameFromHeadJSON(){
        JSONObject headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec\"," +
                "\"authorization\":\"Basic kienboec-mtcgToken\"}");
        String userName = util.getUserNameFromHeadJSON(headJSON);
        Assert.assertEquals("kienboec", userName);
    }

    @Test
    public void testCheckToken(){
        JSONObject headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec\"," +
                "\"authorization\":\"Basic kienboec-mtcgToken\"}");
        Boolean status = util.checkToken(headJSON);
        Assert.assertTrue(status);
        headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec\"," +
                "\"authorization\":\"Basic kienboec1-mtcgToken\"}");
        status = util.checkToken(headJSON);
        Assert.assertFalse(status);

    }

    @Test
    public void testCheckUser(){
        String userName = "kienboec";
        Boolean status = util.checkUser(userName);
        Assert.assertTrue(status);

        userName = "jackson";
        status = util.checkUser(userName);
        Assert.assertFalse(status);
    }



}
