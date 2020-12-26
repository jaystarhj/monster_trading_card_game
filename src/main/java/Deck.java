public class Deck{

    private String card_id;
    private int user_id;

    public Deck (){}

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "{\"card_id\":\"" + card_id  +
                "\", \"user_id\":\"" + user_id + "\"}";
    }
}