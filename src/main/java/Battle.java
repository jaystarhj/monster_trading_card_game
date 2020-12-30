import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.IllegalCharsetNameException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Battle {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    public static JSONObject runBattle(JSONObject headJSON){
        JSONObject message = new JSONObject();
        int count = 100;
        int winner = -1;

        if (util.checkToken(headJSON)){
            String userName = util.getUserNameFromHeadJSON(headJSON);
            User user = UserSQL.getUserByName(userName);
            if (user != null){
                User user_two = getRandomUser(user.getName());
                while (true){
                    if (count == 0){
                        break;
                    }
                    winner = oneRound(user.getId(), user_two.getId());
                    if (winner == user.getId()){

                    }else if (winner == user_two.getId()){

                    }else{

                    }


                    count -= 1;
                }

                // update status
            }
        }else{
            message = new JSONObject("{\"Error\":\"invalid token or user\"}");
        }


        return message;
    }

    public static int oneRound(int user_id_one, int user_id_two){
        int winner;
        Card cardOne = getRandomCard(user_id_one);
        Card cardTwo = getRandomCard(user_id_two);

        String cardOneType = cardOne.getCardType().toString();
        String cardTwoType = cardTwo.getCardType().toString();

        float cardOneDamageNew = 0;
        float cardTwoDamageNew = 0;


        // pure monster flights
        if (cardOneType.equals("monster") & cardTwoType.equals("monster")){
            cardOneDamageNew = cardOne.getDamage();
            cardTwoDamageNew = cardTwo.getDamage();
            // pure spell flights
        }else if (cardOneType.equals("spell") & cardTwoType.equals("spell")){
            // transform damage
            HashMap<String, Float> map = transformSpellDamage(cardOne, cardTwo);
            cardOneDamageNew = map.get("card_one");
            cardTwoDamageNew = map.get("card_two");
        // mixed
        }else if (cardOneType.equals("monster") & cardTwoType.equals("spell") ||
                cardOneType.equals("spell") & cardTwoType.equals("monster")){
            HashMap<String, Float> map = transformMixedDamage(cardOne, cardTwo);
            cardOneDamageNew = map.get("card_one");
            cardTwoDamageNew = map.get("card_two");
        }

        if (cardOneDamageNew > cardTwoDamageNew){
            winner = user_id_one;
        }else if (cardOneDamageNew < cardTwoDamageNew){
            winner = user_id_one;
        }else{
            winner = -1;
        }

        return winner;
    }

    public int getScore(){
        return 0;
    }

    // randomly pick card from deck by user_id
    public static int randomNumber(int min, int max){
        int randomInt = min + (int)(Math.random() * ((max - min) + 1));

        return randomInt;
    }

    public static List<String> getCardIdFromStack(int user_id){
        String SQLQuery = "select * from deck where user_id = ?";
        List<String> idList = new ArrayList<>();
        try{
            ResultSet rs = CRUD.ReadSql(SQLQuery, user_id);
            if (rs.next() == false){
                return null;
            }else{
                do {
                    String card_id = rs.getString("card_id");
                    idList.add(card_id);
                }while (rs.next());
            }

        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return idList;
    }

    public static Card getRandomCard(int user_id){
        Card card = null;
        List<String> cardIdList = getCardIdFromStack(user_id);
        List<Card> card_List = new ArrayList<>();
        if (cardIdList != null ){
            for (String card_id:cardIdList){
                card = CardSQL.getCardByID(card_id);
                if (card != null){
                    card_List.add(card);
                }
            }
            int randomInt = randomNumber();
            card = card_List.get(randomInt);
        }

        return card;
    }

    public static HashMap<String, Float> transformSpellDamage(Card cardOne, Card cardTwo){
        String cardOneName = cardOne.getName();
        String cardTwoName = cardTwo.getName();
        HashMap<String, Float> map = new HashMap<>();

        if (cardOneName.equals("WaterSpell") & cardTwoName.equals("FireSpell") ||
                cardOneName.equals("FireSpell") & cardTwoName.equals("RegularSpell") ||
                cardOneName.equals("RegularSpell") & cardTwoName.equals("WaterSpell")){
            map.put("card_one", doubleDamage(cardOne));
            map.put("card_two", havledDamage(cardTwo));
        }else if (cardOneName.equals("FireSpell") & cardTwoName.equals("WaterSpell") ||
                cardOneName.equals("RegularSpell") & cardTwoName.equals("FireSpell") ||
                cardOneName.equals("WaterSpell") & cardTwoName.equals("RegularSpell")){
            map.put("card_one", havledDamage(cardOne));
            map.put("card_two", doubleDamage(cardTwo));
        }else{
            map.put("card_one", cardOne.getDamage());
            map.put("card_two", cardTwo.getDamage());
        }

        return map;
    }

    public static HashMap<String, Float> transformMixedDamage(Card cardOne, Card cardTwo){
        String cardOneName = cardOne.getName();
        String cardTwoName = cardTwo.getName();
        HashMap<String, Float> map = new HashMap<>();

        if (cardOneName.contains("Water") & cardTwoName.contains("Fire") ||
                cardOneName.contains("Fire") & cardTwoName.contains("Regular") ||
                cardOneName.contains("Regular") & cardTwoName.contains("Water")){
            map.put("card_one", doubleDamage(cardOne));
            map.put("card_two", havledDamage(cardTwo));
        }else if (cardOneName.contains("Fire") & cardTwoName.contains("Water") ||
                cardOneName.contains("Regular") & cardTwoName.contains("Fire") ||
                cardOneName.contains("Water") & cardTwoName.contains("Regular")) {
            map.put("card_one", havledDamage(cardOne));
            map.put("card_two", doubleDamage(cardTwo));
            // knight vs water
        }else if (cardOneName.contains("Knight") & cardTwoName.contains("WaterSpell") ){
            map.put("card_one", havledDamage(cardOne));
            map.put("card_two", doubleDamage(cardTwo));
        }else if (cardOneName.contains("WaterSpell") & cardTwoName.contains("Knight") ){
            map.put("card_one", doubleDamage(cardOne));
            map.put("card_two", havledDamage(cardTwo));
        }else if (cardOneName.contains("Dragons") & cardTwoName.contains("Goblins") ){
            map.put("card_one", doubleDamage(cardOne));
            map.put("card_two", (float) 0.0);
        }else if (cardOneName.contains("Goblins") & cardTwoName.contains("Dragons") ){
            map.put("card_one", (float) 0.0);
            map.put("card_two", doubleDamage(cardTwo));
        }else if (cardOneName.contains("Wizzard") & cardTwoName.contains("Orks") ){
            map.put("card_one", doubleDamage(cardOne));
            map.put("card_two", (float) 0.0);
        }else if (cardOneName.contains("Orks") & cardTwoName.contains("Wizzard") ){
            map.put("card_one", (float) 0.0);
            map.put("card_two", doubleDamage(cardTwo));
        }else if (cardOneName.contains("Kraken") & cardTwoName.contains("Spell") ){
            map.put("card_one", doubleDamage(cardOne));
            map.put("card_two", (float) 0.0);
        }else if (cardOneName.contains("Spell") & cardTwoName.contains("Kraken") ){
            map.put("card_one", (float) 0.0);
            map.put("card_two",  doubleDamage(cardTwo));
        }else if (cardOneName.contains("FireElves") & cardTwoName.contains("Dragons") ){
            map.put("card_one", (float) 0.0);
            map.put("card_two",  (float) 0.0);
        }else if (cardOneName.contains("Dragons") & cardTwoName.contains("FireElves") ){
            map.put("card_one", (float) 0.0);
            map.put("card_two",  (float) 0.0);
        }else{
            map.put("card_one", cardOne.getDamage());
            map.put("card_two", cardTwo.getDamage());
        }
        return map;
    }

    public static float havledDamage (Card card){
        return card.getDamage()/2;
    }

    public static float doubleDamage(Card card){
        return card.getDamage()*2;
    }

    public static User getRandomUser(String userName){
        List<Integer> idList = new ArrayList<>();
        User user = null;
        List<User>  userList = new ArrayList<>();

        String sqlQuery = "SELECT * FROM usertable WHERE name NOT IN ('admin', ?)";
        try{
            ResultSet rs = CRUD.ReadSql(sqlQuery, userName);
            if (rs.next() == false){
                return null;
            }else{
                do {
                    User tmpUser = new User();
                    tmpUser.setId(rs.getInt("id"));
                    tmpUser.setName(rs.getString("name"));
                    tmpUser.setBio(rs.getString("bio"));
                    tmpUser.setImage(rs.getString("image"));
                    tmpUser.setCoin(rs.getInt("coin"));
                    userList.add(tmpUser);
                }while (rs.next());

                user = userList.get(randomNumber(0, userList.size()-1));
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    public static void main(String[] args){
        System.out.println(getRandomCard(1));
    }

}
