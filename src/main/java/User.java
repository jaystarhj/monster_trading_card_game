
public class User{
    private Integer id;
    private String name;
    private String password;
    private String bio;
    private String image;

    // constructor
    public User(int id, String name, String password, String bio, String image){
        setId(id);
        setName(name);
        setPassword(password);
        setBio(bio);
        setImage(image);
    }
    // constructor
    public User(String name, String password){
        setId(id);
        setName(name);
        setPassword(password);
    }

    public User() {

    }

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
                + "\"}";
    }

}