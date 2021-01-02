import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class ConnectionHandler extends Thread{

    private Socket socket;

    public ConnectionHandler (Socket socket){
        this.socket = socket;
    }

    public HashMap<String, Object> parseHttpRequest() throws IOException {
        HashMap<String, Object> resultMap= new HashMap<>();
        // idea from  https://stackoverflow.com/questions/34143039/extracting-the-body-from-http-post-requesth
        // 建立好连接后, 从socket中获取输入流, 并建立缓冲区进行读取
        InputStream inputStream = this.socket.getInputStream();

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
        resultMap.put("head", headerJson);
        System.out.println(headStrBuilder.toString());

        // read body
        if (hasBody){
            char[] body = new char[bodyLength];
            bufferedReader.read(body, 0, bodyLength);
            requestBody = new String(body);
            System.out.println(requestBody);

            if (requestBody.contains("[")){
                JSONArray bodyJSONArray = new JSONArray(requestBody);
                resultMap.put("bodyArray", bodyJSONArray);
            }else if (bodyLength ==0){
                JSONObject bodyJson = new JSONObject("{\"Empty\":\"Empty\"}");
                resultMap.put("body", bodyJson);
            }else if (requestBody.contains("{")){
                JSONObject bodyJson = parseBody(requestBody);
                resultMap.put("body", bodyJson);
            }else{
                JSONArray bodyJSONArray = new JSONArray("["+requestBody + "]");
                resultMap.put("bodyArray", bodyJSONArray);
            }


        }

        return resultMap;
    }

    // parse header
    public static JSONObject parseHeader(String headStr){
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
    public static JSONObject parseBody(String bodyStr){
        // json object
        return new JSONObject(bodyStr);
    }

    // parse url
    public static Object responseData(JSONObject headJSON, Object bodyJSON){
        String method = (String) headJSON.get("method");
        String url = (String) headJSON.get("url");

        // register user
        if (method.equals("POST") & url.equals("users") & bodyJSON instanceof JSONObject){
            return UserSQL.register((JSONObject) bodyJSON);
        }

        // register user
        if (method.equals("POST") & url.equals("sessions") & bodyJSON instanceof JSONObject){
            return UserSQL.login((JSONObject) bodyJSON);
        }

        // edit user profile
        if (method.equals("PUT") & Pattern.matches("users/[a-zA-Z0-9]+", url) & bodyJSON instanceof JSONObject) {
            return UserSQL.updateUserProfile(headJSON, (JSONObject) bodyJSON);

        }
        // get user profile
        if (method.equals("GET") & Pattern.matches("users/[a-zA-Z0-9]+", url)){
            return UserSQL.getUserProfile(headJSON);
        }
        // add package by admin
        if (method.equals("POST") & url.equals("packages") & bodyJSON instanceof JSONArray){
            return PackSQL.addPackage(headJSON, (JSONArray) bodyJSON);
        }
        // acquire package by user

        if (method.equals("POST") & url.equals("transactions/packages")){
            return PackSQL.acquirePackage(headJSON);
        }

        if (method.equals("GET") & url.equals("cards")){
            return StackSQL.getCardsFromStack(headJSON);
        }

        if (method.equals("PUT") & url.equals("deck") & bodyJSON instanceof JSONArray){
            return DeckSQL.addCardToDeck(headJSON, (JSONArray) bodyJSON);
        }

        if (method.equals("GET") & (url.equals("deck") || url.equals("deck?format=plain"))){
            return DeckSQL.getDeckByUser(headJSON);
        }

        if (method.equals("GET") & url.equals("stats")){
            return StatsSQL.getStatsByUser(headJSON);
        }

        if (method.equals("GET") & url.equals("tradings")){
            return StoreSQL.getDeals(headJSON);
        }

        if (method.equals("POST") & url.equals("tradings") & bodyJSON instanceof JSONObject){
            return StoreSQL.addDeal(headJSON, (JSONObject) bodyJSON);
        }
        if (method.equals("POST") & Pattern.matches("tradings/[a-zA-Z0-9-]+", url)  & bodyJSON instanceof JSONArray){
            return StoreSQL.makeDeal(headJSON, (JSONArray) bodyJSON);
        }
        if (method.equals("DELETE") & Pattern.matches("tradings/[a-zA-Z0-9-]+", url)){
            return StoreSQL.deleteDeal(headJSON);
        }

        if (method.equals("POST") & url.equals("battles")){
            return Battle.runBattle(headJSON);
        }


        return new JSONObject("{\"Error\":\"Invalid HTTP method or URL or input data\"}");

    }

    @Override
    public void run() {
        // get data from http request
        HashMap<String, Object> map = null;
        try {
            map = parseHttpRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject respondData;
        // get head json
        JSONObject headJSon = (JSONObject) map.get("head");
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object bodyJSON;

        if (map.containsKey("body")){
            bodyJSON = map.get("body");

        }else{
            bodyJSON = map.get("bodyArray");
        }

        // write message to client
        try {
            outputStream.write(responseData(headJSon, bodyJSON).toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the connection
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
