
public class User{
    private Integer id;
    private String name;
    private String password;
    private String bio;
    private String image;
    private int coin;
    private boolean battle_status;


    // constructor
    public User() {}

    // setter
    public void setName(String name) {
        this.name = name;
    }

    // setter
    public void setPassword(String password) {
        this.password = password;
    }

    // setter
    public void setId(Integer id) {
        this.id = id;
    }

    // getter
    public String getName() {
        return name;
    }

    // getter
    public String getPassword() {
        return password;
    }

    // getter
    public Integer getId() {
        return id;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public void setBattle_status(boolean battle_status) {
        this.battle_status = battle_status;
    }

    public boolean getBattle_status() {
        return battle_status;
    }

    @Override
    public String toString(){
        if (bio == null){
            bio = "null";
        }
        if (image == null){
            image = "null";
        }
        return  "{"
                + "id:\"" + id
                + "\",\"name\":\"" + name
                + "\",\"password\":\"" + password
                + "\",\"bio\":\"" + bio
                + "\",\"image\":\"" + image
                + "\",\"coin\":\"" + coin
                + "\"}";
    }

}