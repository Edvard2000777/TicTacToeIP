package server;

import java.net.*;
import java.util.Enumeration;

public class ip {
    public static void main(String[] args) throws SocketException {
        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

        } catch (UnknownHostException e) {

            e.printStackTrace();

        }

    }
}
