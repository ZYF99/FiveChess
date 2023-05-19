package com.example.gobang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class OLServer {
    private final static int port = 38888;
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);//创建绑定到特定端口的服务器Socket。

            int count = 0;//记录客户端数量
            System.out.println("服务器启动");
            //定义一个死循环，不停的接收客户端连接
            while (true) {
                Socket clientSocket = serverSocket.accept();//侦听并接受到此套接字的连接
                //InetAddress inetAddress = clientSocket.getInetAddress();//获取客户端的连接
                socketList.add(clientSocket);
                new Thread(new OLServerThread(clientSocket)).start();
                count++;//如果正确建立连接
                System.out.println("客户端：" + clientSocket.getLocalAddress() + "客户端数量：" + count);//打印客户端
                System.out.println();//打印客户端数量

                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "utf-8"));

                String chessColor;
                if (count % 2 == 0) {
                    chessColor = "1";
                } else {
                    chessColor = "2";
                }
                String content = "gameData:your chess color:" + chessColor;
                printWriter.println(content);
                printWriter.flush();
                System.out.println("toC---：" + content);//打印发送到客户端的内容

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


class OLServerThread implements Runnable {
    //定义当前线程所处理的Socket
    Socket socket = null;
    //该线程所处理的Socket所对应的输入流
    BufferedReader br = null;

    public OLServerThread(Socket s) throws IOException {
        this.socket = s;
        //初始化该Socket对应的输入流
        br = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf-8"));
    }

    public void run() {
        try {
            String content;
            //采用循环不断从Socket中读取客户端发送过来的数据
            while ((content = readFromClient()) != null) {
                System.out.println("S-----" + content);
                //遍历socketList中的每个Socket，
                //将读到的内容向每个Socket发送一次
                for (Iterator<Socket> it = OLServer.socketList.iterator(); it.hasNext(); ) {
                    Socket s = it.next();
                    if (!s.isClosed() && s != socket) {
                        try {
                            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "utf-8"));
                            printWriter.println(content);
                            printWriter.flush();
                            System.out.println("toC---：" + content);//打印发送到客户端的内容
                        } catch (SocketException e) {
                            e.printStackTrace();
                            //删除该Socket。
                            it.remove();
                            System.out.println(OLServer.socketList);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //定义读取客户端数据的方法
    private String readFromClient() {
        try {
            return br.readLine();
        }
        //如果捕捉到异常，表明该Socket对应的客户端已经关闭
        catch (IOException e) {
            e.printStackTrace();
            //删除该Socket。
            OLServer.socketList.remove(socket);
        }
        return null;
    }
}

