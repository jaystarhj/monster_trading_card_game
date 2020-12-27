import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

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

    public HashMap<String, Object> parseHttpRequest(Socket socket) throws IOException {
        HashMap<String, Object> resultMap= new HashMap<>();
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
        JSONObject headerJson = parseHeader(headStrBuilder.toString());
        System.out.println(headStrBuilder.toString());
        resultMap.put("head", headerJson);

        // read body
        if (hasBody){
            char[] body = new char[bodyLength];
            bufferedReader.read(body, 0, bodyLength);
            requestBody = new String(body);

            if (requestBody.contains("[")){
                JSONArray bodyJSONArray = new JSONArray(requestBody);
                resultMap.put("bodyArray", bodyJSONArray);
            }else if (bodyLength ==0){
                JSONObject bodyJson = new JSONObject("{\"Empty\":\"Empty\"}");
                resultMap.put("body", bodyJson);
            }else{
                JSONObject bodyJson = parseBody(requestBody);
                resultMap.put("body", bodyJson);
            }


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
        for (String line:headLines){
            if (line.contains("Authorization:")){
                //Authorization: Basic kienboec-mtcgToken
                String userName = line.split("\\s")[2].split("-")[0];
                String token = line.split("\\s")[2].split("-")[1];
                tmpMap.put("authorization", line.split(": ")[1]);
                tmpMap.put("userName", userName);
                tmpMap.put("token", token);

            }
        }
        // if header no auth, add a default one which is wrong
        if(!tmpMap.containsKey("authorization")){
            tmpMap.put("authorization", "Authorization: Basic mtcgTokenmtcgToken-mtcgTokenmtcgToken");
        }

        // JSONObject
        return new JSONObject(tmpMap);
    }

    // parser body
    public JSONObject parseBody(String bodyStr){
        // json object
        return new JSONObject(bodyStr);
    }

    // parse url
    public Object responseData(JSONObject headJSON, Object bodyJSON){
        String method = (String) headJSON.get("method");
        String url = (String) headJSON.get("url");
        String name = null;
        String password = null;

        // register user
        if (method.equals("POST") & url.equals("users")){
            return UserSQL.register((JSONObject) bodyJSON);
        }

        // register user
        if (method.equals("POST") & url.equals("sessions")){
            return UserSQL.login((JSONObject) bodyJSON);
        }

        // edit user profile
        if (method.equals("PUT") & Pattern.matches("users/[a-zA-Z0-9]+", url)){
            return UserSQL.updateUserProfile(headJSON, (JSONObject) bodyJSON);

        }
        // get user profile
        if (method.equals("GET") & Pattern.matches("users/[a-zA-Z0-9]+", url)){
            return UserSQL.getUserProfile(headJSON);
        }
        // add package by admin
        if (method.equals("POST") & url.equals("packages")){
            return PackSQL.addPackage(headJSON, (JSONArray) bodyJSON);
        }
        // acquire package by user

        if (method.equals("POST") & url.equals("transactions/packages")){
            return PackSQL.acquirePackage(headJSON);
        }

        if (method.equals("GET") & url.equals("cards")){
            return CardSQL.getCardsFromPackByUser(headJSON);
        }

        if (method.equals("PUT") & url.equals("deck")){
            return DeckSQL.addCardToDeck(headJSON, (JSONArray) bodyJSON);
        }

        if (method.equals("GET") & (url.equals("deck") || url.equals("deck?format=plain"))){
            return DeckSQL.getDeckByUser(headJSON);
        }
        if (method.equals("GET") & url.equals("stats")){
            return StatsSQL.getStatsByUser(headJSON);
        }
        if (method.equals("GET") & url.equals("tradings")){
            return TradeSQL.getDeals(headJSON);
        }
        return new JSONObject("{\"Error\":\"Something went wrong\"}");

    }

    public static void main (String[] args) throws IOException {
        // init the server
        Server s = new Server();

        // server 将一直等待连接的到来
        System.out.println("waiting for connection now...");
        while (true){

            Socket socket = s.serverSocket.accept();

            // get data from http request
            HashMap<String, Object> map = s.parseHttpRequest(socket);
            JSONObject respondData;
            // get head json
            JSONObject headJSon = (JSONObject) map.get("head");
            OutputStream outputStream = socket.getOutputStream();
            Object bodyJSON;

            if (map.containsKey("body")){
                bodyJSON = map.get("body");

            }else{
                bodyJSON = map.get("bodyArray");
            }

            // write message to client
            outputStream.write(s.responseData(headJSon, bodyJSON).toString().getBytes("UTF-8"));

            // close the connection
            outputStream.close();
            socket.close();

        }
//        s.serverSocket.close();

    }


}
