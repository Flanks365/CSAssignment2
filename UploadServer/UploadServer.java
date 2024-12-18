import java.io.IOException;
import java.net.ServerSocket;

public class UploadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8082);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8082.");
            System.exit(-1);
        }
        while (true) {
            System.out.println("Waiting for connection on port 8082 to create thread");
            new UploadServerThread(serverSocket.accept()).start();
        }
    }
}
