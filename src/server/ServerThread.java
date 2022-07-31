package server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ServerThread implements Runnable {
    private Thread self;
    private Socket sock;
    private Socket client2;
    private final Map<String ,  List<ServerThread> > serverMap;
    private BufferedReader reader;
    private BufferedWriter writer;
    private static int counter;
    private final int id;

    public ServerThread(Socket sock, Map<String, List<ServerThread>> serverMap) {
        this.sock = sock;
        this.serverMap = serverMap;
        self = new Thread(this);
        id = counter++;
    }

    public Socket getSock() {
        return sock;
    }

    public Socket getClient2() {
        return client2;
    }

    public void setClient2(Socket client2) {
        this.client2 = client2;
    }

    public void setupAndStart() throws IOException {
        reader = new BufferedReader(
                new InputStreamReader(sock.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(sock.getOutputStream()));
        self.start();
    }

    public void sendData(String coordinates) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
            writer.write(coordinates);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String str;
        try {
            while (true) {
                str = reader.readLine();
                System.out.println("client " + id + " sent " + str);
                if (checkForIp(str)) {
                    System.out.println("IP recognized");
                    handleIp(str);
                } else if (checkForCommand(str)) {
                    System.out.println("Command recognized");
                    handleCommand(str);
                } else {
                    System.out.println("Invalid command");
                    handlerErrorCommand(str);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("client  " + id + " discount ");
    }

    private boolean checkForIp(String s) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return s.matches(PATTERN);
    }

    private boolean checkForCommand(String s) {
        // O (1->9) O8
        // X (1->9) X3
        if (s.length() == 2) {
            if (s.charAt(0) == 'X' || s.charAt(0) == 'O') {
                return 0 <= (s.charAt(1) - '0') && (s.charAt(1) - '0') <= 9;
            }
        }
        return false;
    }

    private void handlerErrorCommand(String s) {
        sendData("err" + s);
    }

    private void handleIp(String s) {
        ServerThread secondPlayer = serverMap.get(s).get(1);
        System.out.println(secondPlayer);
        secondPlayer.setClient2(sock);
        setClient2(secondPlayer.getSock());
        System.out.println("Clients paired");
    }

    private void handleCommand(String s) {
        sendData(s);
    }
}
