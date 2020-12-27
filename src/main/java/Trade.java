
public class Trade {

    private Card card;
    private User user;

    public void setCard(Card card) {
        this.card = card;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "{" +
                "\"Id\":\"" + card.getId() +
                "\", \"Type\":\"" + card.getCardType().toString() +
                "\", \"MinimumDamage\":\"" + card.getDamage() +
                "\", \"User\":\"" + user.getName() +
                "\"}";
    }
}
