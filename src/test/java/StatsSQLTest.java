import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class StatsSQLTest {

    @Test
    public void testGetStatsByUser(){
        JSONObject headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec\"," +
                "\"authorization\":\"Basic kienboec-mtcgToken\"," +
                "\"url\":\"/cards\"}");
        JSONObject result = StatsSQL.getStatsByUser(headJSON);
        // it will return a jsonobject, first check if it is a type of json array
        Assert.assertTrue(result instanceof JSONObject);
        // get a correct user status json object
        // {"loss":"97","user_id":"1","win":"95"}
        Assert.assertTrue(result.has("user_id"));

        // invalid user example
        headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec1\"," +
                "\"authorization\":\"Basic kienboec1-mtcgToken\"}");
        result = StatsSQL.getStatsByUser(headJSON);
        // it will return a jsonobject, first check if it is a type of json array
        Assert.assertTrue(result instanceof JSONObject);
        //{"Error":"Not such user / Invalid Token/User"}
        Assert.assertTrue(result.has("Error"));

    }
}
