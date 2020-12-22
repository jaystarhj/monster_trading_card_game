import org.json.JSONArray;
import org.json.JSONObject;


public class User{
    private Integer id;
    private String name;
    private String password;

    // constructor
    public User(){}

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

    @Override
    public String toString(){
        return  "{"
                + "id:" + id
                + ",\"name\":" + name
                + ",\"password\":" + password
                + '}';
    }

}