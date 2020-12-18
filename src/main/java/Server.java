import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Server {
    // listen to port
    private int port = 10001;
    // server socket
    private ServerSocket serverSocket;
    // url path pattern
    private List<String> pathList= Arrays.asList("users", "sessions",
            "packages", "/transactions/packages",
            "cards", "deck", "tradings", "score", "stats");

    // HashMap for http request header and body
    private HashMap<String, String> requestMap=new HashMap<String, String>();

    // constructor
    public Server () throws IOException {
        // init ServerSocket
        this.serverSocket = new ServerSocket(this.port);
    }

    public void parseHttpRequest(Socket socket) throws IOException {
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

//        parseHeader(headStrBuilder.toString());
        // add header to hashMap
        requestMap.put("header", headStrBuilder.toString());

        // read body
        if (hasBody){
            char[] body = new char[bodyLength];
            bufferedReader.read(body, 0, bodyLength);
            requestBody = new String(body);
            parseBody(requestBody);
        }

        // add body to hashMap
        requestMap.put("body", requestBody);

    }

    // parse header
    public void parseHeader(String headStr){
        String[] headLines = headStr.split("\r\n");
        String[] pathLine = headLines[0].split("\\s");
        String method = "method: " + pathLine[0];
        String url = "url: " + pathLine[1];
        String otherLines = Arrays.copyOfRange(headLines, 1, headLines.length + 1).toString();
        // jsonobject
        JSONObject headJson = new JSONObject(method +  url + otherLines);
        JSONArray keys = headJson.names ();
        for (int i = 0; i < keys.length (); i++) {
            String key = keys.getString (i); // Here's your key
            String value = headJson.getString (key); // Here's your value
            System.out.println(value);
        }
    }

    // parser body
    public void parseBody(String bodyStr){
        System.out.println(bodyStr);
        // json object
        JSONObject bodyJson = new JSONObject(bodyStr);
        JSONArray keys = bodyJson.names ();
        for (int i = 0; i < keys.length (); i++) {
            String key = keys.getString (i); // Here's your key
            String value = bodyJson.getString (key); // Here's your value
            System.out.println(value);
        }

    }

    // parse url
    public void parseURL(String urlStr){}



    public static void main (String[] args) throws IOException {
        // init the server
        Server s = new Server();

        // server 将一直等待连接的到来
        System.out.println("waiting for connection now...");
        while (true){

            Socket socket = s.serverSocket.accept();

            // get data from http request
            s.parseHttpRequest(socket);
            System.out.println("get header message from client: \n" + s.requestMap.get("header"));
            if (s.requestMap.containsKey("body")){
                System.out.println("get body message from client: \n" + s.requestMap.get("body"));
            }

            JSONObject json = new JSONObject();
            json.put("type", "CONNECT");

            OutputStream outputStream = socket.getOutputStream();
            // write message to client
            outputStream.write(json.toString().getBytes("UTF-8"));

            // close the connection
            outputStream.close();
            socket.close();

        }
//        s.serverSocket.close();

    }


}
