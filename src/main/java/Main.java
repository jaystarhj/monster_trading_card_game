import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        // init the server
        ServerSocket s = new ServerSocket(10001);
        System.out.println("Waiting for new connection...");

        // server will keep waiting 
        while (true){
            Socket socket = null;
            try{
                socket = s.accept();
                System.out.println("A new connection established...");
                // create a new thread object
                Thread t = new ConnectionHandler(socket);
                // Invoking the start() method
                t.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
