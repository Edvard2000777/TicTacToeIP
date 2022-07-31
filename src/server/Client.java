package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client extends Application {
    private Socket sock;//за место локал хоста можно поставить IP
    private final StackPane root = new StackPane();
    private final Map<Integer, Button> map;
    private final char[][] board;
    private final BufferedWriter writer;
    private int emptyCells;
    private boolean isTurn;
    private Label label;
    private InetAddress ip;

    public Client() {
        try {
            sock = new Socket("localhost", 30333);
            writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            ip =sock.getInetAddress().getLocalHost();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("CLIENT ERROR");
        }
        board = new char[][]{{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};
        map = new HashMap<>();
        emptyCells = 9;
        isTurn = true;
        label = new Label("");
        label.setTranslateX(80);
        label.setTranslateY(-100);
    }

    private void createButton(int x, int y, int width, int height, int number) {

        Button button = new Button("");
        button.setTranslateX(x);
        button.setTranslateY(y);
        button.setMinSize(width, height);
        button.setOnAction(event -> {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                System.out.println(isTurn);
                if (button.getText().isEmpty() && isTurn) {
                    button.setText("X");
                    setSymbol('X', number);
                    writer.write("X" + number);
                    writer.newLine();
                    writer.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        root.getChildren().add(button);
        map.put(number, button);
    }

    @Override
    public void start(Stage stage) {

        createButton(-85, -45, 80, 60, 1);
        createButton(5, -45, 80, 60, 2);
        createButton(95, -45, 80, 60, 3);
        createButton(-85, 25, 80, 60, 4);
        createButton(5, 25, 80, 60, 5);
        createButton(95, 25, 80, 60, 6);
        createButton(-85, 95, 80, 60, 7);
        createButton(5, 95, 80, 60, 8);
        createButton(95, 95, 80, 60, 9);
        Button b = new Button("Search");
        b.setTranslateX(-8);
        b.setTranslateY(-100);
        TextField b1 = new TextField();
        b1.setTranslateX(90);
        b1.setTranslateY(-100);
        b1.setPrefColumnCount(10);
        b1.setMaxWidth(110);
        b1.setMaxHeight(20);
        b.setOnAction(event -> {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                writer.write(b1.getText());
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        label = new Label( String.valueOf(ip.getHostAddress()));
        label.setTranslateX(-90);
        label.setTranslateY(-100);


        root.getChildren().add(b1);
        root.getChildren().add(label);
        root.getChildren().add(b);
        stage.setTitle("XO&X local seting");
        stage.setScene(new Scene(root, 350, 270));

        stage.show();
        Thread t = createNewThread();
        t.start();
    }

    private boolean isWinner(char sym) {
        for (int i = 0; i < 3; i++) {
            int countC = 0, countR = 0;
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == sym) countC++;
                if (board[j][i] == sym) countR++;
            }
            if (countR == 3 || countC == 3) return true;
        }
        int countA = 0, countB = 0;
        for (int i = 0; i < 3; i++) {
            if (board[i][i] == sym) countA++;
            if (board[i][2 - i] == sym) countB++;
        }
        return countA == 3 || countB == 3;
    }

    private void setSymbol(char symbol, int pos) {
        --pos;
        isTurn = !isTurn;
        if (--emptyCells == 0) {
            label.setText("It's tie!!!");
            isTurn = false;
            return;
        }
        int i = Math.max((pos / 3), 0), j = pos % 3;
        board[i][j] = symbol;
        if (isWinner(symbol)) {
            label.setText(symbol + " win the round");
            isTurn = false;
        }
    }

    private Thread createNewThread() {
        return new Thread(() -> {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("CLIENT ERROR");
            }
            while (true) {
                String str = " ";
                try {
                    str = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Received message: " + str);
                String finalStr = str;
                Platform.runLater(() -> {
                    try {
                        int position = finalStr.charAt(finalStr.length() - 1) - '0';
                        Button button = map.get(position);
                        button.setText("O");
                        setSymbol('O', position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}


