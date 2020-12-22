import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.xpath.XPathResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    // listen to port
    private int port = 10001;
    // server socket
    private ServerSocket serverSocket;
    // url path pattern
    private List<String> pathList= Arrays.asList("users", "sessions",
            "packages", "/transactions/packages",
            "cards", "deck", "tradings", "score", "stats");

    // constructor
    public Server () throws IOException {
        // init ServerSocket
        this.serverSocket = new ServerSocket(this.port);
    }

    public HashMap<String, JSONObject> parseHttpRequest(Socket socket) throws IOException {
        HashMap<String, JSONObject> resultMap= new HashMap<>();
        // idea from  https://stackoverflow.com/questions/34143039/extracting-the-body-from-http-post-requesth
        // 建立好连接后, 从socket中获取输入流, 并建立缓冲区进行读取
        InputStream inputStream = socket.getInputStream();

        StringBuilder headStrBuilder = new StringBuilder();
        String line;
        int bodyLength=0;
        boolean hasBody=false;
        String requestBody=null;

        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,StandardCharsets.UTF_8));

        // read header first
        while ((line = bufferedReader.readLine()) != null && !line.equals("")) {
            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            headStrBuilder.append(line).append("\r\n");
            if (line.startsWith("Content-Length:")) {
                bodyLength = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                hasBody = true;
            }
        }
        System.out.println(headStrBuilder.toString());
        JSONObject headerJson = parseHeader(headStrBuilder.toString());
        resultMap.put("head", headerJson);

        // read body
        if (hasBody){
            char[] body = new char[bodyLength];
            bufferedReader.read(body, 0, bodyLength);
            requestBody = new String(body);
            JSONObject bodyJson = parseBody(requestBody);
            resultMap.put("body", bodyJson);

        }

        return resultMap;
    }

    // parse header
    public JSONObject parseHeader(String headStr){
        String[] headLines = headStr.split("\r\n");
        // loop through strings[]
        String[] firstLine = headLines[0].split(" ");
        String method = firstLine[0];
        String url = firstLine[1];
        HashMap<String, String> tmpMap = new HashMap<>();
        tmpMap.put("method", method);
        tmpMap.put("url", url.substring(1));

        // jsonobject
        return new JSONObject(tmpMap);
    }

    // parser body
    public JSONObject parseBody(String bodyStr){
        // json object
        JSONObject bodyJson = new JSONObject(bodyStr);
        return new JSONObject(bodyStr);
    }

    // parse url
    public JSONObject responseData(JSONObject headJSON, JSONObject bodyJSON){
        String method = (String) headJSON.get("method");
        String url = (String) headJSON.get("url");
        String name = null;
        String password = null;
        JSONObject response = new JSONObject("{}");

        JSONArray keys = bodyJSON.names ();
        for (int i = 0; i < keys.length (); i++) {
            String key = keys.getString (i); // Here's your key
            String value = bodyJSON.getString (key); // Here's your value
            if (key.equals("Username")){
                name = value;
            }
            if (key.equals("Password")){
                password = value;
            }
        }

        // register user
        if (method.equals("POST") & url.equals("users")){
            response = registerUser(name, password);
        }

        // register user
        if (method.equals("POST") & url.equals("sessions")){
            response = loginUser(name, password);
        }

        return response;
    }

    public JSONObject registerUser(String name, String password){
        String SQLQuery = "INSERT INTO usertable (name, password) values(?, ?)";
        return CRUD.add(SQLQuery, name, password);
    }

    public JSONObject loginUser(String name, String password){
        String SQLQuery = "select * from usertable where name = ?";
        JSONObject tmp = CRUD.get(SQLQuery, name);
        if (!tmp.has("Error")){
            if (tmp.has("name")){
                String userName = tmp.getString ("name");
                String userPassWord = tmp.getString ("password");
                if (userName.equals(name) & userPassWord.equals(password)){
                    return new JSONObject("{\"message\":\"Successfully Login\"}");
                }else{
                    return new JSONObject("{\"Error\":\"Invaild password\"}");
                }
            }
        }
        return tmp;
    }
    public static void main (String[] args) throws IOException {
        // init the server
        Server s = new Server();

        // server 将一直等待连接的到来
        System.out.println("waiting for connection now...");
        while (true){

            Socket socket = s.serverSocket.accept();

            // get data from http request
            HashMap<String, JSONObject> map = s.parseHttpRequest(socket);
            // get head json
            JSONObject headJSon = map.get("head");
            JSONObject bodyJSON = map.get("body");
            JSONObject responsed = s.responseData(headJSon, bodyJSON);

            OutputStream outputStream = socket.getOutputStream();
            // write message to client
            outputStream.write(responsed.toString().getBytes("UTF-8"));

            // close the connection
            outputStream.close();
            socket.close();

        }
//        s.serverSocket.close();

    }


}
