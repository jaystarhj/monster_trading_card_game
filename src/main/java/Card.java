public class Card{

    public enum CardType{
        MONSTER(0), SPELL(1);

        private int value;

        CardType(int val){
            this.value = val;
        }

        public String toString(){
            switch(value){
                case 0:   return "monster";
                case 1:   return "spell";
                default:  return "none";
            }
        }
    }

    // Attributes ......
    // element Type
    private Card.CardType cardType;

    // name
    private String name;
    private float damage;
    private String id;
    private Package aPackage;

    public Card (){}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
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

    public void setCardType(String type) {
        if (type.equals("monster")){
            this.cardType = CardType.MONSTER;
        }else{
            this.cardType = CardType.SPELL;
        }
    }

    public CardType getCardType() {
        return cardType;
    }

    public String toString(){
        return  "{"
                + "\"Id\":\"" + id
                + "\",\"Name\":\"" + name
                + "\",\"Damage\":\"" + damage
                + "\",\"Type\":\"" + cardType
                + "\"}";
    }
}