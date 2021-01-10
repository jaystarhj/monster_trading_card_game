import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class DeckSQLTest {

    @Test
    public void getDeckByUser(){
        JSONObject headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec\"," +
                "\"authorization\":\"Basic kienboec-mtcgToken\"," +
                "\"url\":\"/cards\"}");
        Object results = DeckSQL.getDeckByUser(headJSON);
        // it will return a json array, first check if it is a type of json array
        Assert.assertTrue(results instanceof JSONArray);
        JSONArray resultsArray = new JSONArray();
        resultsArray = (JSONArray) results;
        // check if it contains card on user's deck
        Object first = resultsArray.get(0);
        // ["b2237eca-0271-43bd-87f6-b22f70d42ca4","9e8238a4-8a7a-487f-9f7d-a8c97899eb48",...]
        Assert.assertFalse(first instanceof JSONObject);

        // check wrong user
        headJSON = new JSONObject("{" +
                "\"userName\":\"kienboec1\"," +
                "\"authorization\":\"Basic kienboec1-mtcgToken\"," +
                "\"url\":\"/cards\"}");
        results = DeckSQL.getDeckByUser(headJSON);
        // it will return a json array, first check if it is a type of json array
        Assert.assertTrue(results instanceof JSONArray);
        resultsArray = new JSONArray();
        resultsArray = (JSONArray) results;
        // check if it contains card on user's deck
        first = resultsArray.get(0);
        // ["b2237eca-0271-43bd-87f6-b22f70d42ca4","9e8238a4-8a7a-487f-9f7d-a8c97899eb48",...]
        // [{"Error":"Invalid token / user"}]
        Assert.assertTrue(first instanceof JSONObject);
    }
}
