package com.example.gobang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {


    //服务器地址
    private static final String host = "192.168.1.110";
    //服务器端口port
    private static final int port = 38888;

    private static SocketClient instance;
    private Socket socket;
    private PrintWriter printWriter;
    private ServerMessageListener serverMessageListener;
    private ReceiveMessageThread receiveMessageThread;

    public static SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient(host, port);
        }
        return instance;
    }

    private SocketClient(String url, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(url, port);
                if (printWriter == null) {
                    printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                }
                receiveMessageThread = new ReceiveMessageThread(socket);
                receiveMessageThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void setServerMessageListener(ServerMessageListener serverMessageListener) {
        this.serverMessageListener = serverMessageListener;
    }

    private void sendMsg(String s) {
        new Thread(() -> {
            printWriter.println(s);
            printWriter.flush();
        }).start();
    }

    //发送落子消息到服务器
    public void downChess(int x, int y, int chessColor) {
        String msg = "gameData:" + x + "/" + y + "/" + chessColor;
        sendMsg(msg);
    }

    public void release() {
        try {
            printWriter.close();
            socket.close();
            receiveMessageThread.release();
            socket = null;
            instance = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ReceiveMessageThread extends Thread {
        private Socket socket;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;

        ReceiveMessageThread(Socket s) {
            socket = s;
            try {
                inputStreamReader = new InputStreamReader(socket.getInputStream(), "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String msg;
                while (socket!=null && !socket.isClosed() && (msg = bufferedReader.readLine()) != null) {
                    if (msg.startsWith("gameData:")) {
                        String info = msg.replace("gameData:", "");
                        if (info.contains("your chess color:")) {
                            int chessColor = Integer.parseInt(info.replace("your chess color:", ""));
                            serverMessageListener.onChessColorMessage(chessColor);
                        } else {
                            serverMessageListener.onOpponentChessDownMessage(info);
                        }
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void release() {
            try {
                inputStreamReader.close();
                bufferedReader.close();
                socket = null;
                interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ServerMessageListener {
        void onOpponentChessDownMessage(String str);

        void onChessColorMessage(int myChessColor);
    }

}
