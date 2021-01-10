import org.junit.Assert;
import org.junit.Test;

public class CardSQLTest {

    @Test
    public void testGetCardByID(){
        String card_id = "845f0dc7-37d0-426e-994e-43fc3ac83c08";
        Card c = CardSQL.getCardByID(card_id);
        Assert.assertNotNull(c);
        // test user not register yet
        card_id = "11111845f0dc7-37d0-426e-994e-43fc3ac83c08";
        c = CardSQL.getCardByID(card_id);
        Assert.assertNull(c);

    }


}
