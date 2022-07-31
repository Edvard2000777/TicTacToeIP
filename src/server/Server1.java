package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is my server
 */
public class Server1 {
    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(30333);

        Map<String, List<ServerThread>> map = new HashMap<>();
        System.out.println("SERVER STARTED");
        while (true) {
            Socket sock = server.accept();
            System.out.println("client " + sock.getInetAddress().getCanonicalHostName() + " connected ");
            ServerThread client = new ServerThread(sock, map);
            List<ServerThread> lst = map.getOrDefault(sock.getInetAddress().getHostAddress(), new ArrayList<>());
            lst.add(client);

            map.put(sock.getInetAddress().toString().substring(1), lst);
            client.setupAndStart();
            System.out.println(map);




        }
    }
}
