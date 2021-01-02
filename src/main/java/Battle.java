import org.json.JSONObject;
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
        int count = 1;
        int winner = -1;

        if (util.checkToken(headJSON)){
            String userName = util.getUserNameFromHeadJSON(headJSON);
            User user = UserSQL.getUserByName(userName);
            if (user != null){
                if (!user.getBattle_status()){
                    User user_two = getRandomUser(user.getName());
                    if (user_two != null){
                        // update battle status
                        updateUserBattleStatus(userName);
                        updateUserBattleStatus(user_two.getName());

                        System.out.println(" user one is: " + user.getName() );
                        System.out.println(" user two is: " + user_two.getName() );

                        while (true){ // 循环
                            // 如果轮数达到了100,或者任意一方deck没有卡牌了
                            if (count == 101 || checkEmptyDeckByUser(user.getId(), user_two.getId())){
                                // 终止游戏
                                break;
                            }

                            // 开始一轮游戏,随机选择两张卡牌进行PK,
                            winner = oneRound(user.getId(), user_two.getId());
                            if (winner == user.getId()){
                                System.out.println("round " + count + "  winner is : " + user.getName());
                                updateStatus(user.getId(), 1, 0,0);
                                updateStatus(user_two.getId(), 0, 1,0);
                            }else if (winner == user_two.getId()){
                                System.out.println("round " + count + "  winner is : " + user_two.getName());
                                updateStatus(user_two.getId(), 1, 0,0);
                                updateStatus(user.getId(), 0, 1, 0);
                            }else{
                                System.out.println("round " + count + " is : " + "draw");
                                updateStatus(user_two.getId(), 0, 0,1);
                                updateStatus(user.getId(), 0, 0, 1);
                            }
                            count += 1;
                        }

                        // update battle status
                        updateUserBattleStatus(userName);
                        updateUserBattleStatus(user_two.getName());

                        message = new JSONObject("{\"Message\":\"Battle is completed\"}");
                    }else{
                        message = new JSONObject("{\"Error\":\"There is no user available for battle now\"}");
                    }
                }else{
                    message = new JSONObject("{\"Error\":\"You already in a battle now\"}");
                }
            }
        }else{
            message = new JSONObject("{\"Error\":\"invalid token or user\"}");
        }


        return message;
    }

    public static int oneRound(int user_id_one, int user_id_two){
        // 每个player随机从deck选出一张卡牌
        int winner;
        String card_ID;
        Card cardOne = getRandomCard(user_id_one);
        Card cardTwo = getRandomCard(user_id_two);
        System.out.println("card_one: " + cardOne.getId());
        System.out.println("card_two: " + cardTwo.getId());

        // pk两张牌
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


        // 比较牌的大小
        if (cardOneDamageNew > cardTwoDamageNew){
            winner = user_id_one;
            card_ID = cardTwo.getId();
        }else if (cardOneDamageNew < cardTwoDamageNew){
            winner = user_id_two;
            card_ID = cardOne.getId();
        }else{
            winner = -1;
            card_ID = null;

        }

        // 把卡牌从输的一方换到赢的一方
        updateDeck(winner, card_ID);

        return winner;
    }

    public static void updateDeck(int user_id_winner, String card_id_loser){
        if (user_id_winner != -1 & card_id_loser != null){
            // up deck
            String updateDeckQuery = "UPDATE deck SET user_id = ? WHERE card_id = ?";
            String updateStackQuery = "UPDATE stack SET user_id = ? WHERE card_id = ?";

            int rowCount = CRUD.CUDSql(updateDeckQuery, user_id_winner, card_id_loser);
            int rowCountTwo = CRUD.CUDSql(updateStackQuery, user_id_winner, card_id_loser);
        }

    }


    // randomly pick card from deck by user_id
    public static int randomNumber(int min, int max){
        int randomInt = min + (int)(Math.random() * ((max - min) + 1));

        return randomInt;
    }

    public static List<String> getCardIdFromDeck(int user_id){
        String SQLQuery = "select * from deck where user_id = ?";
        List<String> idList = new ArrayList<>();
        try{
            ResultSet rs = CRUD.ReadSql(SQLQuery, user_id);
            if (rs.next() == false){
                return idList;
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
        List<String> cardIdList = getCardIdFromDeck(user_id);
        if (!cardIdList.isEmpty()){
            List<Card> card_List = new ArrayList<>();
            for (String card_id:cardIdList){
                card = CardSQL.getCardByID(card_id);
                if (card != null){
                    card_List.add(card);
                }
            }
            int randomInt = randomNumber(0, card_List.size()-1);
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

        String sqlQuery = "SELECT * FROM usertable WHERE name NOT IN ('admin', ?) and (userTable.battle_status = false)";
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
                    tmpUser.setBattle_status(rs.getBoolean("battle_status"));
                    userList.add(tmpUser);
                }while (rs.next());

                user = userList.get(randomNumber(0, userList.size()-1));
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    public static Boolean checkEmptyDeckByUser(int user_id_one, int user_id_two){
        String checkDeck = "select * from deck where user_id = ?";
        try{
            ResultSet rs = CRUD.ReadSql(checkDeck, user_id_one);
            if (rs.next() == false){
                return true;
            }
            ResultSet rs_two = CRUD.ReadSql(checkDeck, user_id_two);
            if (rs_two.next() == false){
                return true;
            }
        }catch (SQLException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }

    public static void updateStatus(int user_id, int win, int loss, int draw){
        // check if user in stats table
        String updateStats = "update stats set win = win + ?, loss = loss + ?, draw = draw +? where user_id = ?";
        CRUD.CUDSql(updateStats, win, loss, draw, user_id);
    }

    public static  void updateUserBattleStatus(String userName){
        CRUD.CUDSql("update usertable set battle_status = not userTable.battle_status where name = ?", userName);
    }

    public static void main(String[] args){
        System.out.println(getRandomCard(1));
    }

}
