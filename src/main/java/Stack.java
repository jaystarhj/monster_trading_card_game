import java.util.List;

public class Stack {
    private List<Card> cardList;
    private int user_id;

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Card card:cardList){
            str.append(card.toString()).append(",");
        }
        return str.toString();
    }
}
