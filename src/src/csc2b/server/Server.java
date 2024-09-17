package src.csc2b.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String[] argv)
    {
        try(ServerSocket serverSocket =  new ServerSocket(2018)){
            System.out.println("Server started");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new BUKAHandler(socket);
            }
        }
        catch (IOException e){
            System.out.println("Running server error: " + e.getMessage());
        }
    }

}
