public class Card{

    public enum CardType{
        MONSTER(0), SPELL(1);

        private int value;

        CardType(int val){
            this.value = val;
        }

        public String toString(){
            if (value == 1) {
                return "SPELL";
            }
            return "MONSTER";
        }
    }

    // Attributes ......
    // element Type
    private Card.CardType cardType;

    // name
    private String name;
    private float damage;
    private int id;

    public Card (int id, String name, int damage, CardType cardType){
        setId(id);
        setName(name);
        setCardType(cardType);
        setDamage(damage);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardType getCardType() {
        return cardType;
    }
}